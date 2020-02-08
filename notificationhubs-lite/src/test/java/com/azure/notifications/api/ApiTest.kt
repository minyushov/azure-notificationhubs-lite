package com.azure.notifications.api

import com.azure.notifications.AzureException
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert

internal fun `error with description`(call: suspend Api.() -> Unit) = apiTest { server ->
  val errorCode = 500
  val errorMessage = "Error Description"

  server.enqueue(
    MockResponse()
      .setResponseCode(errorCode)
      .setBody(
        """
          <?xml version="1.0" encoding="utf-8"?>  
          <Error>
            <Code>$errorCode</Code>  
            <Detail>$errorMessage</Detail>  
          </Error>  
          """.trimIndent()
      )
  )

  val exception = assertThrows<AzureException> { call() }

  Assert.assertEquals(errorCode, exception.code)
  Assert.assertEquals(errorMessage, exception.message)
}

internal fun `error with malformed description`(call: suspend Api.() -> Unit) = apiTest { server ->
  val errorCode = 500
  val errorMessage = "Internal Server Error"

  server.enqueue(
    MockResponse()
      .setResponseCode(errorCode)
      .setStatus("HTTP/1.1 $errorCode $errorMessage")
      .setBody("error body")
  )

  val exception = assertThrows<AzureException> { call() }

  Assert.assertEquals(errorCode, exception.code)
  Assert.assertEquals(errorMessage, exception.message)
}

internal fun `error without description`(call: suspend Api.() -> Unit) = apiTest { server ->
  val errorCode = 500
  val errorMessage = "Internal Server Error"

  server.enqueue(
    MockResponse()
      .setResponseCode(errorCode)
      .setStatus("HTTP/1.1 $errorCode $errorMessage")
  )

  val exception = assertThrows<AzureException> { call() }

  Assert.assertEquals(errorCode, exception.code)
  Assert.assertEquals(errorMessage, exception.message)
}

@Suppress("BlockingMethodInNonBlockingContext")
internal fun apiTest(action: suspend Api.(MockWebServer) -> Unit) = runBlocking {
  val server = MockWebServer()

  server.start()

  val hub = "hub"
  val url = server.url(hub)
  val connectionString = "Endpoint=sb://${url.host}:${url.port}/;SharedAccessKeyName=accessKeyName;SharedAccessKey=accessKey"
  val api = Api(
    settings = ApiSettings(
      hub = hub,
      connectionString = connectionString,
      client = OkHttpClient(),
      scheme = "http"
    )
  )

  action(api, server)

  server.shutdown()
}