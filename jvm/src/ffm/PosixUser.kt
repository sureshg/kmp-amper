package ffm

import dev.suresh.ffm.*
import java.lang.foreign.*
import java.lang.foreign.MemoryLayout.PathElement.groupElement

/** Retrieves Unix UID/GID/groups information for the current user. */
object PosixUser {

  /** Current user's login name, or `null` if unavailable. */
  val username: String?

  /** Current user's UID. */
  val uid: Long

  /** Current user's primary GID. */
  val gid: Long

  /** Current user's supplementary group IDs. */
  val groups: List<Long>

  private val getGroups =
      downcallHandle("getgroups", FunctionDescriptor.of(C_INT, C_INT, C_POINTER))
  private val getuid = downcallHandle("getuid", FunctionDescriptor.of(C_INT))
  private val getgid = downcallHandle("getgid", FunctionDescriptor.of(C_INT))

  private val getpwuid_r =
      downcallHandle(
          "getpwuid_r",
          FunctionDescriptor.of(C_INT, C_INT, C_POINTER, C_POINTER, C_LONG, C_POINTER),
      )

  private val PASSWD_LAYOUT: GroupLayout =
      MemoryLayout.structLayout(
          C_POINTER.withName("pw_name"),
          C_POINTER.withName("pw_passwd"),
          C_INT.withName("pw_uid"),
          C_INT.withName("pw_gid"),
          MemoryLayout.sequenceLayout(100, C_CHAR).withName("dummy"),
      )

  private val PW_UID_LAYOUT = PASSWD_LAYOUT.select(groupElement("pw_uid")) as ValueLayout.OfInt
  private val PW_UID_OFFSET = PASSWD_LAYOUT.byteOffset(groupElement("pw_uid"))
  private val PW_GID_LAYOUT = PASSWD_LAYOUT.select(groupElement("pw_gid")) as ValueLayout.OfInt
  private val PW_GID_OFFSET = PASSWD_LAYOUT.byteOffset(groupElement("pw_gid"))
  private val PW_NAME_LAYOUT = PASSWD_LAYOUT.select(groupElement("pw_name")) as AddressLayout
  private val PW_NAME_OFFSET = PASSWD_LAYOUT.byteOffset(groupElement("pw_name"))

  private const val GETPW_R_SIZE_MAX = 4096L

  init {
    Arena.ofConfined().use { arena ->
      var groupNum = getGroups?.invokeExact(0, MemorySegment.NULL) as Int
      require(groupNum != -1) { "getgroups returns $groupNum" }

      val gs = arena.allocate(C_INT, groupNum.toLong())
      groupNum = getGroups.invokeExact(groupNum, gs) as Int
      require(groupNum != -1) { "getgroups returns $groupNum" }

      groups = List(groupNum) { gs.getAtIndex(C_INT, it.toLong()).toLong() }

      val resBuf = arena.allocate(PASSWD_LAYOUT)
      val pwd = arena.allocate(C_POINTER)
      val pwdBuf = arena.allocate(GETPW_R_SIZE_MAX)
      val tmpUid = getuid?.invokeExact() as Int

      val result = getpwuid_r?.invokeExact(tmpUid, resBuf, pwdBuf, GETPW_R_SIZE_MAX, pwd) as Int

      if (result != 0 || pwd.get(ValueLayout.ADDRESS, 0) == MemorySegment.NULL) {
        uid = tmpUid.toLong()
        gid = (getgid?.invokeExact() as Int).toLong()
        username = null
      } else {
        uid = resBuf.get(PW_UID_LAYOUT, PW_UID_OFFSET).toLong()
        gid = resBuf.get(PW_GID_LAYOUT, PW_GID_OFFSET).toLong()
        username = resBuf.get(PW_NAME_LAYOUT, PW_NAME_OFFSET).getString(0)
      }
    }
  }
}
