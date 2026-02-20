plugins {
    java
}

dependencies {
    // Jakarta EE API (provided by WildFly)
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    
    // JSON processing
    compileOnly("jakarta.json.bind:jakarta.json.bind-api:3.0.0")
}

tasks.jar {
    archiveBaseName.set("genocide-service-ejb")
}
