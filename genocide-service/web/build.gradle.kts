plugins {
    java
    war
}

dependencies {
    // Jakarta EE API (provided by WildFly)
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    
    // EJB module dependency
    implementation(project(":ejb"))
}

tasks.war {
    archiveBaseName.set("genocide-service-web")
}
