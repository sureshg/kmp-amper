package dev.suresh.spi

import com.google.auto.service.AutoService
import java.util.concurrent.Callable

@AutoService(Callable::class)
class Processor : Callable<String> {
  override fun call(): String = KotlinVersion.CURRENT.toString()
}
