# Android EasySpans Library

[![JitPack](https://jitpack.io/v/maxime-kouemo/EasySpans.svg)](https://jitpack.io/#maxime-kouemo/EasySpans)
[![Supports Android Views](https://img.shields.io/badge/Platform-Android%20Views-blue.svg)](https://developer.android.com/develop/ui/views/layout/declaring-layout)
[![Supports Jetpack Compose](https://img.shields.io/badge/UI%20Toolkit-Jetpack%20Compose-green.svg)](https://developer.android.com/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

EasySpans is a Kotlin library for simplifying text styling in Android applications. It provides two sub-libraries:
- **Legacy**: For traditional Android View-based text styling.
- **Compose**: For Jetpack Compose-based text styling.

## Setup

Add the following to your project’s `build.gradle`:

```kotlin
dependencies {
    implementation(project("com.github.maxime-kouemo.EasySpans:compose:1.0.9"))
    implementation(project("com.github.maxime-kouemo.EasySpans:legacy:1.0.9"))
}
```

Add the Maven repository to your `settings.gradle`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

## Modules

- [Legacy Module](./legacy/README.md): For use with Android’s legacy View system.
- [Compose Module](./compose/README.md): For use with Jetpack Compose.

## License

```
   Copyright 2025 by Maxime Kouemo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```