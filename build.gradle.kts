plugins {
	java
	id("org.springframework.boot") version "3.3.8"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
	id("org.sonarqube") version "6.0.1.5171"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

// Define dependency versions
val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdriverManagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

dependencies {
	// Security
	testImplementation("org.springframework.security:spring-security-test")

	// Spring Boot starters
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// ✅ WebSocket & Messaging support
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework:spring-messaging")

	// ─── JPA & Hibernate ─────────────────────────────────
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// ─── Database (H2 for dev; switch to Postgres/MySQL in prod) ──
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql:42.7.3")

	// Lombok
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")


	// Devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Spring Boot Test Starter
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Selenium and JUnit dependencies
	testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
	testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
	testImplementation("io.github.bonigarcia:webdrivermanager:$webdriverManagerVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-config")
	implementation("org.springframework.security:spring-security-web")
	implementation("org.springframework.security:spring-security-core")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // for Jackson serializers


}

// Optional: custom test tasks
tasks.register<Test>("unitTest") {
	description = "Runs unit tests."
	group = "verification"
	filter {
		excludeTestsMatching("*FunctionalTest")
	}
}

tasks.register<Test>("functionalTest") {
	description = "Runs functional tests."
	group = "verification"
	filter {
		includeTestsMatching("*FunctionalTest")
	}
}

// Exclude FunctionalTest from the built-in `test` task and finalize with Jacoco
tasks.test {
	filter {
		excludeTestsMatching("*FunctionalTest")
	}
	finalizedBy(tasks.jacocoTestReport)
}

// The jacocoTestReport task depends on the results of `test`
tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

// Ensure JUnit Platform is used for ALL test tasks
tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

sonar {
	properties {
		property("sonar.projectKey", "Group3-AdvProg_papikos")
		property("sonar.organization", "group3-advprog")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

