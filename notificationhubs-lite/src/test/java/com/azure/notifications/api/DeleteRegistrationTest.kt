package com.azure.notifications.api

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeleteRegistrationTest {
  @Test
  fun success() = apiTest { server ->
    server.enqueue(MockResponse().setResponseCode(200))
    deleteRegistration("registrationId")
  }

  @Test
  fun `error with description`() = `error with description` {
    deleteRegistration("registrationId")
  }

  @Test
  fun `error with malformed description`() = `error with malformed description` {
    deleteRegistration("registrationId")
  }

  @Test
  fun `error without description`() = `error without description` {
    deleteRegistration("registrationId")
  }
}