package com.azure.notifications.api

import android.net.Uri
import android.os.Build
import com.azure.notifications.AzureException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.ByteString.Companion.encode
import java.net.URLEncoder
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val SDK_VERSION = "2015-01"
private const val API_VERSION = "2015-01"

internal class Api(
  private val settings: ApiSettings
) {

  suspend fun getRegistrations(token: String): List<String> {
    val url = settings
      .registrationsEndpoint
      .toString()
      .let { "$it/?\$filter=${URLEncoder.encode("GcmRegistrationId eq '$token'", "UTF-8")}&api-version=$API_VERSION" }

    val request = Request
      .Builder()
      .get()
      .url(url)
      .addApiHeaders(url)
      .build()

    return settings
      .client
      .newCall(request)
      .await { response ->
        response.validate()
        response.body.byteStream().parseRegistrations()
      }
  }

  suspend fun createRegistration(token: String, tags: Collection<String>) {
    val url = settings
      .registrationsEndpoint
      .buildUpon()
      .appendQueryParameter("api-version", API_VERSION)
      .build()
      .toString()

    val body = """
    |<?xml version="1.0" encoding="utf-8"?>
    |<entry xmlns="http://www.w3.org/2005/Atom">
    |  <content type="application/xml">
    |    <FcmV1RegistrationDescription xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://schemas.microsoft.com/netservices/2010/10/servicebus/connect">
    |      <Tags>${tags.joinToString(separator = ",")}</Tags>
    |      <FcmV1RegistrationId>$token</FcmV1RegistrationId> 
    |    </FcmV1RegistrationDescription>
    |  </content>
    |</entry>
    """.trimMargin()

    val request = Request
      .Builder()
      .post(body.toRequestBody("application/atom+xml;type=entry;charset=utf-8".toMediaType()))
      .url(url)
      .addApiHeaders(url)
      .build()

    settings
      .client
      .newCall(request)
      .await { it.validate() }
  }

  suspend fun deleteRegistration(registrationId: String) {
    val url = settings
      .registrationsEndpoint
      .buildUpon()
      .appendPath(registrationId)
      .appendQueryParameter("api-version", API_VERSION)
      .build()
      .toString()

    val request = Request
      .Builder()
      .delete()
      .url(url)
      .addApiHeaders(url)
      .header("If-Match", "*")
      .build()

    settings
      .client
      .newCall(request)
      .await { it.validate() }
  }

  private fun Request.Builder.addApiHeaders(url: String): Request.Builder =
    this
      .header("User-Agent", getUserAgent())
      .header("Authorization", createAuthToken(url, Date()))

  private fun createAuthToken(path: String, date: Date): String {
    val url = Uri.encode(path).lowercase(Locale.US)

    val expiresIn = Calendar
      .getInstance(TimeZone.getTimeZone("UTC"))
      .apply {
        time = date
        add(Calendar.MINUTE, 5)
      }
      .timeInMillis / 1000

    val key = settings.sharedAccessKey.encode()
    val toSign = "$url\n$expiresIn".encode()
    val hash = Uri.encode(toSign.hmacSha256(key).base64().trim())

    return "SharedAccessSignature sr=$url&sig=$hash&se=$expiresIn&skn=${settings.sharedAccessKeyName}"
  }

  private fun getUserAgent(): String =
    "NOTIFICATIONHUBS/$SDK_VERSION (api-origin=AndroidSdkFcmV1; os=Android; os_version=${Build.VERSION.RELEASE};)"

  private fun Response.validate() {
    if (!isSuccessful) {
      throw AzureException(
        code = code,
        message = try {
          body.byteStream().parseError() ?: message
        } catch (throwable: Throwable) {
          message
        }
      )
    }
  }
}