package ffm

import dev.suresh.ffm.*
import java.lang.foreign.*

/** Retrieves Windows user identity information for the current user. */
object WinUser {

  /** Current user's login name, or `null` if unavailable. */
  val username: String?

  /** Current user's SID (e.g., "S-1-5-21-..."). */
  val userSid: String?

  /** Current user's primary group SID. */
  val primaryGroupSid: String?

  /** Current user's group SIDs. */
  val groupSids: List<String>

  private const val TOKEN_QUERY = 0x0008
  private const val TOKEN_USER = 1
  private const val TOKEN_GROUPS = 2
  private const val TOKEN_PRIMARY_GROUP = 5

  private val POINTER_SIZE = C_POINTER.byteSize()
  private val SID_ATTR_SIZE = POINTER_SIZE + 8 // PSID(8) + DWORD(4) + padding(4)

  private val GetCurrentProcess =
      downcallHandle("GetCurrentProcess", FunctionDescriptor.of(C_POINTER))
  private val OpenProcessToken =
      downcallHandle(
          "OpenProcessToken",
          FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER),
      )
  private val GetTokenInformation =
      downcallHandle(
          "GetTokenInformation",
          FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER, C_INT, C_POINTER),
      )
  private val GetUserNameW =
      downcallHandle("GetUserNameW", FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER))
  private val ConvertSidToStringSidW =
      downcallHandle(
          "ConvertSidToStringSidW",
          FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER),
      )
  private val CloseHandle = downcallHandle("CloseHandle", FunctionDescriptor.of(C_INT, C_POINTER))
  private val LocalFree = downcallHandle("LocalFree", FunctionDescriptor.of(C_POINTER, C_POINTER))

  init {
    Arena.ofConfined().use { arena ->
      username = fetchUsername(arena)

      val tokenPtr = arena.allocate(C_POINTER)
      val process = GetCurrentProcess.invokeExact() as MemorySegment
      val opened = OpenProcessToken.invokeExact(process, TOKEN_QUERY, tokenPtr) as Int

      if (opened == 0) {
        userSid = null
        primaryGroupSid = null
        groupSids = emptyList()
        return@use
      }

      val token = tokenPtr.get(C_POINTER, 0)
      try {
        userSid = fetchSid(arena, token, TOKEN_USER)
        primaryGroupSid = fetchSid(arena, token, TOKEN_PRIMARY_GROUP)
        groupSids = fetchGroupSids(arena, token)
      } finally {
        CloseHandle.invokeExact(token)
      }
    }
  }

  private fun fetchUsername(arena: Arena): String? {
    val sizePtr = arena.allocate(C_INT).also { it.set(C_INT, 0, 256) }
    val buffer = arena.allocate(WCHAR, 256)
    if ((GetUserNameW.invokeExact(buffer, sizePtr) as Int) == 0) return null
    val len = sizePtr.get(C_INT, 0) - 1
    return if (len > 0) readWideString(buffer, len) else null
  }

  private fun fetchSid(arena: Arena, token: MemorySegment, infoClass: Int): String? {
    val info = getTokenInfo(arena, token, infoClass) ?: return null
    return sidToString(arena, info.get(C_POINTER, 0))
  }

  private fun fetchGroupSids(arena: Arena, token: MemorySegment): List<String> {
    val info = getTokenInfo(arena, token, TOKEN_GROUPS) ?: return emptyList()
    val count = info.get(C_INT, 0)
    return (0 until count).mapNotNull { i ->
      sidToString(arena, info.get(C_POINTER, POINTER_SIZE + i * SID_ATTR_SIZE))
    }
  }

  private fun getTokenInfo(arena: Arena, token: MemorySegment, infoClass: Int): MemorySegment? {
    val sizePtr = arena.allocate(C_INT)
    GetTokenInformation.invokeExact(token, infoClass, MemorySegment.NULL, 0, sizePtr)
    val size = sizePtr.get(C_INT, 0)
    if (size <= 0) return null

    val buffer = arena.allocate(size.toLong())
    return if (
        (GetTokenInformation.invokeExact(token, infoClass, buffer, size, sizePtr) as Int) != 0
    )
        buffer
    else null
  }

  private fun sidToString(arena: Arena, sid: MemorySegment): String? {
    if (sid.address() == 0L) return null
    val strPtr = arena.allocate(C_POINTER)
    if ((ConvertSidToStringSidW.invokeExact(sid, strPtr) as Int) == 0) return null

    val strSeg = strPtr.get(C_POINTER, 0)
    val result = readWideStringNullTerminated(strSeg)
    LocalFree.invokeExact(strSeg)
    return result
  }

  private fun readWideString(seg: MemorySegment, len: Int) =
      CharArray(len) { seg.get(WCHAR, it * 2L) }.concatToString()

  private fun readWideStringNullTerminated(seg: MemorySegment) = buildString {
    var pos = 0L
    while (true) {
      val c = seg.get(WCHAR, pos)
      if (c == '\u0000') break
      append(c)
      pos += 2
    }
  }
}
