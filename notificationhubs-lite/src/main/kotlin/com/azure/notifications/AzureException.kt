package com.azure.notifications

class AzureException(
  val code: Int,
  override val message: String
) : Exception(
  message
)