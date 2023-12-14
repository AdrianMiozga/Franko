> [!NOTE]
> Application made for educational purposes only.

## Screenshots

<p align="middle">
  <img src="/assets/Screenshots/20231214_220759.png" width="320"/>
  <img src="/assets/Screenshots/20231214_220009.png" width="320"/>
</p>

<p align="middle">
  <img src="/assets/Screenshots/20231214_220029.png" width="320"/>
  <img src="/assets/Screenshots/Screenshot4.png" width="320"/>
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
- Polish and English language

## Technology Stack

- [Firebase](https://firebase.google.com/): [Cloud Firestore](https://firebase.google.com/products/realtime-database), [Authentication](https://firebase.google.com/products/auth), [Cloud Storage](https://firebase.google.com/products/storage)
- [Google Maps API](https://developers.google.com/maps)
- [Android Views](https://developer.android.com/develop/ui/views/layout/declaring-layout)
- [Android Jetpack](https://developer.android.com/jetpack)
  - [Navigation](https://developer.android.com/guide/navigation) - navigation
    in application
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - state holder
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - lifecycle-aware observable data holder
  - [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - data storage for user preferences
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html)

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
