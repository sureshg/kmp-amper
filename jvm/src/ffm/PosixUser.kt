package ffm

import dev.suresh.ffm.*
import java.lang.foreign.*
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.invoke.VarHandle

/**
 * Unix user identity information retrieved via FFM calls to POSIX functions.
 *
 * @see <a href="https://man7.org/linux/man-pages/man3/getpwuid.3.html">getpwuid_r(3)</a>
 */
object PosixUser {

  /** Current user's login name. */
  val username: String

  /** Current user's UID (unsigned). */
  val uid: Long

  /** Current user's primary GID (unsigned). */
  val gid: Long

  /** Current user's supplementary group IDs (unsigned). */
  val groups: List<Long>

  private const val GETPW_R_SIZE_MAX = 4096

  private val mhGetgroups by lazy {
    downcallHandle(
        "getgroups",
        FunctionDescriptor.of(C_INT, C_INT, C_POINTER),
        Linker.Option.captureCallState("errno"),
    )
  }

  private val mhGetuid by lazy { downcallHandle("getuid", FunctionDescriptor.of(C_INT)) }

  private val mhGetpwuidR by lazy {
    downcallHandle(
        "getpwuid_r",
        FunctionDescriptor.of(C_INT, C_INT, C_POINTER, C_POINTER, C_SIZE_T, C_POINTER),
    )
  }

  private val ML_PASSWD: GroupLayout =
      MemoryLayout.structLayout(
          C_POINTER.withName("pw_name"),
          C_POINTER.withName("pw_passwd"),
          C_INT.withName("pw_uid"),
          C_INT.withName("pw_gid"),
          MemoryLayout.paddingLayout(100),
      )

  private val vhPwUid: VarHandle = ML_PASSWD.varHandle(groupElement("pw_uid"))
  private val vhPwGid: VarHandle = ML_PASSWD.varHandle(groupElement("pw_gid"))
  private val vhPwName: VarHandle = ML_PASSWD.varHandle(groupElement("pw_name"))

  init {
    Arena.ofConfined().use { arena ->
      val capturedState = arena.allocate(CAPTURE_STATE_LAYOUT)

      var groupNum = mhGetgroups.invokeExact(capturedState, 0, MemorySegment.NULL) as Int
      if (groupNum == -1) error("getgroups returns $groupNum")

      val gs = arena.allocate(C_INT, groupNum.toLong())
      groupNum = mhGetgroups.invokeExact(capturedState, groupNum, gs) as Int
      if (groupNum == -1) {
        val err = errno(capturedState)
        error("getgroups returns $groupNum. Reason: ${strerror(err)}")
      }

      groups = List(groupNum) { gs.getAtIndex(C_INT, it.toLong()).toUInt().toLong() }

      val pwd = arena.allocate(ML_PASSWD)
      val result = arena.allocate(C_POINTER)
      val buffer = arena.allocate(GETPW_R_SIZE_MAX.toLong())
      val tmpUid = mhGetuid.invokeExact() as Int

      // Do not call invokeExact because the type of buffer_size is not always long in the underlying system.
      val out = mhGetpwuidR.invoke(tmpUid, pwd, buffer, GETPW_R_SIZE_MAX, result) as Int
      when {
        out != 0 -> {
            error(strerror(out))
        }
        result.get(ValueLayout.ADDRESS, 0) == MemorySegment.NULL -> {
            error("user entry not found")
        }
        else -> {
          uid = (vhPwUid.get(pwd, 0L) as Int).toUInt().toLong()
          gid = (vhPwGid.get(pwd, 0L) as Int).toUInt().toLong()
          username = (vhPwName.get(pwd, 0L) as MemorySegment).getString(0)
        }
      }
    }
  }

  override fun toString() = "PosixUser(username=$username, uid=$uid, gid=$gid, groups=$groups)"
}
