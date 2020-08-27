package com.azure.notifications.api

import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiSettingsTest {
  @Test
  fun `empty arguments`() = withClient { client ->
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "", "", "")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "", "", "https")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "", "string", "")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "", "string", "https")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "", "")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "", "https")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "string", "")
    }
  }

  @Test
  fun `incomplete connection string`() = withClient { client ->
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "SharedAccessKeyName=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "SharedAccessKey=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=1;SharedAccessKeyName=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=1;SharedAccessKey=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "SharedAccessKeyName=1;SharedAccessKey=1")
    }
  }

  @Test
  fun `malformed connection string`() = withClient { client ->
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "=")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", ";")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", ";=")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint")
    }
  }

  @Test
  fun `endpoint in connection string`() = withClient { client ->
    assertDoesNotThrow {
      ApiSettings(client, "hub", "Endpoint=https://host.com;SharedAccessKeyName=1;SharedAccessKey=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=sb:host;SharedAccessKeyName=1;SharedAccessKey=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=https://;SharedAccessKeyName=1;SharedAccessKey=1")
    }
    assertThrows<IllegalArgumentException> {
      ApiSettings(client, "hub", "Endpoint=host.com;SharedAccessKeyName=1;SharedAccessKey=1")
    }
  }

  @Test
  fun `letter case in connection string`() = withClient { client ->
    val settings1 = assertDoesNotThrow {
      ApiSettings(client, "hub", "Endpoint=https://host.com;SharedAccessKeyName=1;SharedAccessKey=1")
    }
    val settings2 = assertDoesNotThrow {
      ApiSettings(client, "hub", "endpoint=https://host.com;sharedaccesskeyname=1;sharedaccesskey=1")
    }

    assertEquals(settings1.registrationsEndpoint, settings2.registrationsEndpoint)
    assertEquals(settings1.sharedAccessKey, settings2.sharedAccessKey)
    assertEquals(settings1.sharedAccessKeyName, settings2.sharedAccessKeyName)
  }

  @Test
  fun `base64 encoding of shared access key`() = withClient { client ->
    val sharedAccessKey = "VGVzdCBCYXNlIDY0IHN0cmluZw=="
    val data = ApiSettings(client, "hub", "Endpoint=https://host.com;SharedAccessKeyName=1;SharedAccessKey=$sharedAccessKey")
    assertEquals(sharedAccessKey, data.sharedAccessKey)
  }

  private inline fun withClient(test: (OkHttpClient) -> Unit) =
    test(OkHttpClient())

}