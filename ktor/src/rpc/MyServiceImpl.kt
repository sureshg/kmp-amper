package rpc

import dev.suresh.rpc.MyService
import kotlinx.coroutines.flow.*

data class MyServiceParam(val name: String)

class MyServiceImpl(val params: MyServiceParam) : MyService {
  override fun data(input: String) = flow {
    emit("Hello $input")
    emit("Param: ${params.name}")
  }

  override suspend fun ping() = "Pong"
}
