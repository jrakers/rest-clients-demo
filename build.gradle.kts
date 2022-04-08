plugins {
  application
}

repositories {
  mavenCentral()
}

dependencies {
  annotationProcessor( "org.projectlombok:lombok:1.18.22")
  compileOnly("org.projectlombok:lombok:1.18.22")

  implementation("com.google.code.gson:gson:2.9.0")
  implementation("commons-codec:commons-codec:1.15")
  implementation("org.apache.httpcomponents:httpclient:4.5.13")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.slf4j:slf4j-simple:1.7.36")
}

application {
  mainClass.set("io.amalgm.nicehash.MiningRigStats")
}