rootProject.name = "spring-microservice-monorepo"

// Enable type-safe project accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Shared libraries
include("shared-libs:common-dto")
include("shared-libs:common-utils")
include("shared-libs:common-test")

// Greeting service
include("services:greeting-service:domain")
include("services:greeting-service:adapters")
include("services:greeting-service:runtime")

// To add a new service, follow this pattern:
// include("services:[service-name]:domain")
// include("services:[service-name]:adapters")
// include("services:[service-name]:runtime")
