package com.azure.notifications.api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

internal fun InputStream.parseRegistrations(): List<String> =
  use { stream ->
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(stream, null)
    parser.nextTag()

    val registrations = mutableListOf<String>()

    parser.use {
      find("entry") {
        find("content") {
          when (name) {
            "GcmRegistrationDescription",
            "FcmV1RegistrationDescription" ->
              use {
                when (name) {
                  "RegistrationId" -> {
                    val value = text()
                    if (!value.isNullOrEmpty()) {
                      registrations.add(value)
                    }
                  }
                  else -> skip()
                }
              }
            else -> skip()
          }
        }
      }
    }

    registrations
  }

internal fun InputStream.parseError(): String? =
  use { stream ->
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(stream, null)
    parser.nextTag()

    parser.use {
      when (name) {
        "Detail" -> return text()
        else -> skip()
      }
    }

    null
  }

private inline fun XmlPullParser.use(action: XmlPullParser.() -> Unit) {
  while (next() != XmlPullParser.END_TAG) {
    if (eventType != XmlPullParser.START_TAG) continue
    action(this)
  }
}

private inline fun XmlPullParser.find(element: String, action: XmlPullParser.() -> Unit) {
  when (name) {
    element -> use(action)
    else -> skip()
  }
}

private fun XmlPullParser.skip() {
  if (eventType != XmlPullParser.START_TAG) {
    throw IllegalStateException()
  }
  var depth = 1
  while (depth != 0) {
    when (next()) {
      XmlPullParser.END_TAG -> depth--
      XmlPullParser.START_TAG -> depth++
    }
  }
}

private fun XmlPullParser.text(): String? {
  var result: String? = null
  if (next() == XmlPullParser.TEXT) {
    result = text
    nextTag()
  }
  return result
}
