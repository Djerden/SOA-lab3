plugins {
    java
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.djeno"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
