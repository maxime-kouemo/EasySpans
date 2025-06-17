//@file:Suppress("suppressKotlinVersionCompatibilityCheck")

plugins {
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.maven.publish)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/maxime-kouemo/easyspans")
            credentials {
                username = (project.findProperty("githubUsername")
                    ?: System.getenv("githubUsername")).toString()
                password =
                    (project.findProperty("githubToken") ?: System.getenv("githubToken")).toString()
            }
        }
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
            credentials {
                username = ((project.findProperty("jitpackUsername")
                    ?: System.getenv("jitpackUsername")).toString())
                password = (project.findProperty("jitpackToken")
                    ?: System.getenv("jitpackToken")).toString()
            }
        }
    }
}