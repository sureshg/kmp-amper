package dev.suresh.json

import kotlinx.schema.*

/** A postal address for deliveries and billing. */
@Schema
data class Address(
    @Description("Street address") val street: String,
    @Description("City name") val city: String? = null,
    @Description("State or province") val zip: String,
)
