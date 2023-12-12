package com.azure.notifications.api

import android.net.Uri
import okhttp3.OkHttpClient
import java.util.Locale

internal class ApiSettings(
  val client: OkHttpClient,
  hub: String,
  connectionString: String,
  scheme: String = "https"
) {

  val registrationsEndpoint: Uri
  val sharedAccessKeyName: String
  val sharedAccessKey: String

  init {
    if (hub.isBlank()) throw IllegalArgumentException("hub cannot be blank")
    if (scheme.isBlank()) throw IllegalArgumentException("scheme cannot be blank")

    connectionString
      .split(';')
      .filter { it.isNotBlank() }
      .mapNotNull { param ->
        val parts = param.split('=', limit = 2)
        val key = parts.getOrElse(0) { "" }.trim()
        val value = parts.getOrElse(1) { "" }.trim()
        if (key.isNotEmpty() && value.isNotEmpty()) {
          key.lowercase(Locale.US) to value
        } else {
          null
        }
      }
      .associate { it }
      .let { params ->
        val endpoint = params["endpoint"]?.let { Uri.parse(it) } ?: throw IllegalArgumentException("Endpoint is missing in connection string '$connectionString'")
        if (endpoint.scheme.isNullOrEmpty()) throw IllegalArgumentException("Scheme is missing in endpoint '$endpoint'")
        if (endpoint.host.isNullOrEmpty()) throw IllegalArgumentException("Host is missing in endpoint '$endpoint'")
        val sharedAccessKeyName = params["sharedaccesskeyname"] ?: throw IllegalArgumentException("SharedAccessKeyName is missing in connection string '$connectionString'")
        val sharedAccessKey = params["sharedaccesskey"] ?: throw IllegalArgumentException("SharedAccessKey is missing in connection string '$connectionString'")

        this.sharedAccessKeyName = sharedAccessKeyName
        this.sharedAccessKey = sharedAccessKey
        this.registrationsEndpoint = endpoint
          .buildUpon()
          .scheme(scheme)
          .appendPath(hub)
          .appendPath("registrations")
          .build()
      }
  }
}