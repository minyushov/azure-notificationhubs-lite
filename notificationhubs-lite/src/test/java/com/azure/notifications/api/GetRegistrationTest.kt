package com.azure.notifications.api

import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class GetRegistrationTest {
  @Test
  fun success() = apiTest { server ->
    val registrations = listOf(
      UUID.randomUUID().toString(),
      UUID.randomUUID().toString(),
      UUID.randomUUID().toString()
    )

    server.enqueue(
      MockResponse().setBody(
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <feed>
          <entry>
            <content>
              <GcmRegistrationDescription>
                <RegistrationId>${registrations[0]}</RegistrationId>
              </GcmRegistrationDescription>
            </content>
          </entry>
          <entry>
            <content>
              <GcmRegistrationDescription>
                <RegistrationId>${registrations[1]}</RegistrationId>
              </GcmRegistrationDescription>
            </content>
          </entry>
          <entry>
            <content>
              <GcmRegistrationDescription>
                <RegistrationId>${registrations[2]}</RegistrationId>
              </GcmRegistrationDescription>
            </content>
          </entry>
        </feed>
        """.trimIndent()
      )
    )

    assertEquals(
      registrations,
      getRegistrations("token")
    )
  }

  @Test
  fun empty() = apiTest { server ->
    server.enqueue(
      MockResponse().setBody(
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <feed>
        </feed>
        """.trimIndent()
      )
    )

    assertEquals(
      emptyList<String>(),
      getRegistrations("token")
    )
  }

  @Test
  fun `unsupported types`() = apiTest { server ->
    server.enqueue(
      MockResponse().setBody(
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <feed>
          <entry>
            <content>
              <WindowsRegistrationDescription>
                <RegistrationId>1</RegistrationId>
              </WindowsRegistrationDescription>
            </content>
          </entry>
          <entry>
            <content>
              <AppleRegistrationDescription>
                <RegistrationId>2</RegistrationId>
              </AppleRegistrationDescription>
            </content>
          </entry>
          <entry>
            <content>
              <MpnsRegistrationDescription>
                <RegistrationId>3</RegistrationId>
              </MpnsRegistrationDescription>
            </content>
          </entry>
        </feed>
        """.trimIndent()
      )
    )

    assertEquals(
      emptyList<String>(),
      getRegistrations("token")
    )
  }

  @Test
  fun `error with description`() = `error with description` {
    getRegistrations("token")
  }

  @Test
  fun `error with malformed description`() = `error with malformed description` {
    getRegistrations("token")
  }

  @Test
  fun `error without description`() = `error without description` {
    getRegistrations("token")
  }
}