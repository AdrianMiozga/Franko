# Franko

> [!NOTE]
> Application made for educational purposes only.

Franko is a fitness and social platform for Android that allows users to seamlessly log their physical activities, such as cycling, walking, and running.

## Screenshots

<p align="middle">
  <img src="/assets/Screenshots/20231214_220759.png" width="320"/>
  <img src="/assets/Screenshots/20231214_220009.png" width="320"/>
</p>

<p align="middle">
  <img src="/assets/Screenshots/20231214_220029.png" width="320"/>
  <img src="/assets/Screenshots/20231219_190338.png" width="320"/>
</p>

<p align="middle">
  <img src="/assets/Screenshots/20231214_220050.png" width="320"/>
  <img src="/assets/Screenshots/20231214_220118.png" width="320"/>
</p>

## Features

- Login via Google account
- Record three different activity types: cycling, walking, and running
- Filter recorded activities by type
- Set activity name, type, and visibility (public or private)
- Set profile visibility (public or private)
- Search for people using the app
- Follow people
- See who is following your profile
- Edit profile: first and last name, profile picture, bio, and city
- Set profile picture directly from camera or gallery
- English and Polish language

## Technology Stack

- [Firebase](https://firebase.google.com/)
  - [Authentication](https://firebase.google.com/products/auth)
  - [Cloud Firestore](https://firebase.google.com/products/realtime-database)
  - [Cloud Storage](https://firebase.google.com/products/storage)
- [Google Maps API](https://developers.google.com/maps)
- [Android Views](https://developer.android.com/develop/ui/views/layout/declaring-layout)
- [Android Jetpack](https://developer.android.com/jetpack)
  - [Navigation](https://developer.android.com/guide/navigation) - navigation
    in application
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - state holder
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - lifecycle-aware observable data holder
  - [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - performing actions in response to a change in the lifecycle status of another component
  - [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - data storage for user preferences
  - [View Binding](https://developer.android.com/topic/libraries/view-binding) - interacting with views from code
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html)
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

### Architecture

- Single activity
- MVVM

### Libraries

- [Coil](https://github.com/coil-kt/coil) - image loading library
- [Compressor](https://github.com/zetbaitsu/Compressor) - image compression
  library
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - dependency injection

### Testing

- [JUnit 5](https://junit.org/junit5/) - unit tests

### Static analysis tools

- [Android Linter](https://developer.android.com/studio/write/lint)

## Building

### 1. Google Maps API Key

Create file `secrets.properties` in root project directory with the following contents. Replace `YOUR_API_KEY` with your Google Maps API key.

```properties
MAPS_API_KEY=YOUR_API_KEY
```

### 2. Firebase

Download `google-services.json` from [Firebase Console](https://console.firebase.google.com/) and put it in the `app/` directory.

### 3. (Optional) Signing

For signing the release build create `keystore.properties` in root project directory with the following contents and your signing information.

```properties
storePassword=myStorePassword
keyPassword=mykeyPassword
keyAlias=myKeyAlias
storeFile=myStoreFileLocation
```
