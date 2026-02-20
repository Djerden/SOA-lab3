plugins {
    java
    id("ear")
}

dependencies {
    // EJB module
    deploy(project(":ejb"))

    // WAR module
    deploy(project(path = ":web", configuration = "archives"))
}

tasks.ear {
    archiveBaseName.set("genocide-service")
    
    // Имя EJB JAR в EAR
    deploymentDescriptor {
        webModule("genocide-service-web-0.0.1-SNAPSHOT.war", "/")
    }
}
