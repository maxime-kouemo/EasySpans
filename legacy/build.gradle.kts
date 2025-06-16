plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.mamboa.easyspans.legacy"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testOptions.targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    publishing {
        singleVariant("release") {}
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.9.0"
    }
}

dependencies {
    implementation(libs.material)
    //testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
}


// Publishing configuration
afterEvaluate { // Using afterEvaluate is common for publishing Android components
    publishing {
        publications {
            create<MavenPublication>("release") { // It's good practice to explicitly name the publication type
                groupId = "com.mamboa.easyspans"
                artifactId = "legacy"
                version = "1.0"

                // This tells Gradle to publish the outputs of the 'release' component
                // (typically the AAR file for an Android library)
                from(components["release"])

                // Optional: Add sources and Javadoc JARs
                // pom.withXml {
                //     val dependenciesNode = asNode().appendNode("dependencies")
                //     configurations.implementation.get().allDependencies.forEach {
                //         if (it.group != null && it.name != null && it.version != null) {
                //             val dependencyNode = dependenciesNode.appendNode("dependency")
                //             dependencyNode.appendNode("groupId", it.group)
                //             dependencyNode.appendNode("artifactId", it.name)
                //             dependencyNode.appendNode("version", it.version)
                //         }
                //     }
                // }
            }
        }
        // Optional: Configure repositories to publish to
        // repositories {
        //     maven {
        //         name = "MyRepo"
        //         url = uri("file://${buildDir}/repo") // Example: local repository
        //     }
        // }
    }
}