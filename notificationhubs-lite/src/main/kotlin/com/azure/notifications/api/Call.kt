package com.azure.notifications.api

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resumeWithException

internal suspend fun <T : Any> Call.await(action: (Response) -> T): T =
  suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation {
      cancel()
    }
    enqueue(object : Callback {
      override fun onResponse(call: Call, response: Response) {
        if (!continuation.isCancelled) {
          val result = try {
            Result.success(response.use(action))
          } catch (throwable: Throwable) {
            Result.failure(throwable)
          }
          continuation.resumeWith(result)
        }
      }

      override fun onFailure(call: Call, e: IOException) {
        if (!continuation.isCancelled) {
          continuation.resumeWithException(e)
        }
      }
    })
  }