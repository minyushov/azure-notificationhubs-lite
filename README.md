# Azure Notification Hubs Lite

![Maven Central](https://img.shields.io/maven-central/v/io.github.minyushov/azure-notification-hubs)

## Getting Started

```groovy
dependencies {
  implementation "io.github.minyushov:azure-notification-hubs:$version"
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