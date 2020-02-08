# Azure Notification Hubs Lite

## Getting Started

```groovy
dependencies {
  implementation "com.minyushov.azure:notificationhubs-lite:1.0.0"
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