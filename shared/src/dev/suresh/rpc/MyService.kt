package dev.suresh.rpc

import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.*

@Rpc
interface MyService {
  fun data(input: String): Flow<String>

  suspend fun ping(): String
}
