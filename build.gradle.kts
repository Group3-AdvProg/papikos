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
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.h2database:h2")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Selenium and JUnit dependencies
	testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
	testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
	testImplementation("io.github.bonigarcia:webdrivermanager:$webdriverManagerVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
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

// ----- Key part: Exclude FunctionalTest from the built-in `test` task and finalize with Jacoco -----

tasks.test {
	// 1) Exclude functional tests (they will NOT be run by `./gradlew test`)
	filter {
		excludeTestsMatching("*FunctionalTest")
	}
	// 2) Ensure code coverage report (jacocoTestReport) runs right after `test`
	finalizedBy(tasks.jacocoTestReport)
}

// The jacocoTestReport task depends on the results of `test`
tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

// ----- Ensure JUnit Platform is used for ALL test tasks -----
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