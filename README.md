# EasySpans Library

EasySpans is a Kotlin library for simplifying text styling in Android applications. It provides two sub-libraries:
- **Legacy**: For traditional Android View-based text styling.
- **Compose**: For Jetpack Compose-based text styling.

## Setup
Add the following to your project’s `build.gradle`:
```kotlin
dependencies {
    // see the repo's app's settings.gradle for the moment, the library versions are coming soon
    implementation(project(":legacy"))
    implementation(project(":compose"))
}
```

## Modules
- [Legacy Module](./legacy/README.md): For use with Android’s legacy View system.
- [Compose Module](./compose/README.md): For use with Jetpack Compose.

## License
```
   Copyright 2025 mamboa

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