# Azure Notification Hubs Lite

[![Download](https://api.bintray.com/packages/minyushov/azure/azure-notificationhubs-lite/images/download.svg)](https://bintray.com/minyushov/azure/azure-notificationhubs-lite/_latestVersion)

## Getting Started

```groovy
repositories {
  maven { url  "https://dl.bintray.com/minyushov/azure" }
}

dependencies {
  implementation "com.minyushov.azure:azure-notificationhubs-lite:1.0.0"
}
```

## Usage

```kotlin
val hub = NotificationHub(hub = "<hub name>", connectionString = "<connection string>")

// Register token
hub.register("fcm token", listOf("tag1", "tag2"))

// Unregister token
hub.unregister("fcm token")

```