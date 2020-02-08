package com.azure.notifications.api

import kotlin.reflect.KClass

internal suspend inline fun <reified T : Throwable> assertThrows(crossinline executable: suspend () -> Unit): T {
  try {
    executable()
  } catch (throwable: Throwable) {
    if (throwable is T) {
      return throwable
    } else {
      throw AssertionError("Unexpected exception type thrown: Expected '${T::class.canonicalName}' but was '${throwable::class.canonicalName}'")
    }
  }
  throw AssertionError("Expected ${T::class.canonicalName} to be thrown, but nothing was thrown")
}

internal val KClass<*>.canonicalName: String
  get() = java.let { it.canonicalName ?: it.name }