package com.azure.notifications

import com.azure.notifications.api.Api
import com.azure.notifications.api.ApiSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

/**
 * Notification Hub client
 *
 * @param hub Notification Hub name
 *
 * @param connectionString Connection string that contains information about
 * the endpoint of your hub, and the security credentials used to access it
 *
 * @param client OkHttp client that executes network requests
 */
class NotificationHub(
  hub: String,
  connectionString: String,
  client: OkHttpClient =
    OkHttpClient
      .Builder()
      .build()
) {

  private val api = Api(
    settings = ApiSettings(
      hub = hub,
      connectionString = connectionString,
      client = client
    )
  )

  /**
   * Register FCM token with given tags.
   * All previous registrations for this token will be removed.
   *
   * @param token FCM token
   * @param tags Notification tags
   * @param dispatcher [CoroutineDispatcher]
   */
  suspend fun register(
    token: String,
    tags: Collection<String>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
  ) = withContext(dispatcher) {
    unregister(token)
    api.createRegistration(token, tags)
  }

  /**
   * Remove all registrations for given FCM token
   *
   * @param token FCM token
   * @param dispatcher [CoroutineDispatcher]
   */
  suspend fun unregister(
    token: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
  ) = withContext(dispatcher) {
    api
      .getRegistrations(token)
      .distinct()
      .forEach { api.deleteRegistration(it) }
  }
}