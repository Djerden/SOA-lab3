plugins {
	java
	war
    id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.djeno"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

configurations.all {
    exclude(group = "ch.qos.logback")
    exclude(group = "org.apache.logging.log4j")
}

repositories {
	mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    // Consul Discovery
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery:4.1.1")

    // Postgres
    implementation("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP")
    implementation("org.hibernate.orm:hibernate-hikaricp")

    // Логи для WildFly
    implementation("org.jboss.slf4j:slf4j-jboss-logmanager:2.0.1.Final")

    // Metrics
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Lombok
    compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

    // Запуск из Tomcat только локально, иначе создастся конфликт с WildFly
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
