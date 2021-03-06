package com.azure.notifications.api

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateRegistrationTest {
  @Test
  fun success() = apiTest { server ->
    server.enqueue(MockResponse().setResponseCode(200))
    createRegistration("token", listOf("tag"))
  }

  @Test
  fun `error with description`() = `error with description` {
    createRegistration("token", listOf("tag"))
  }

  @Test
  fun `error with malformed description`() = `error with malformed description` {
    createRegistration("token", listOf("tag"))
  }

  @Test
  fun `error without description`() = `error without description` {
    createRegistration("token", listOf("tag"))
  }
}