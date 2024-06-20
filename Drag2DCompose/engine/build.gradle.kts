plugins {
    id("java-library")
}

sourceSets.getByName("main") {
    java.srcDir("../../Drag2DCompose/engine/src/main/java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}