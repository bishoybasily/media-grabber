[![](https://jitpack.io/v/bishoybasily/media-grabber.svg)](https://jitpack.io/#bishoybasily/media-grabber)

# Media Grabber
Image selector for android

### Installation

##### Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

##### Add the dependency

```groovy
dependencies {
    implementation 'com.github.bishoybasily:media-grabber:latest_version'
}
```

### Usage

##### Enable Camera & Storage Permissions

##### Get the captured/selected file path

```kotlin

// a new instance of media grabber, it can be injected with dagger
val mediaGrabber = MediaGrabber()

mediaGrabber
    .with(context)
    // image will start the camera and will read the images form local storage in a whatsapp-like view
    // you can also call file instead of images if you want to select from the local storage only without starting the camera
    .image() 
    .subscribe(
            {
                // it is the path for the captured/selected file
                Log.i("##", it)
            },
            {
                it.printStackTrace()
            }
    )

```

##### Get the captured/selected file path - Full example using [Permissions-Requester](https://github.com/bishoybasily/permissions-requester)

```kotlin

// new instances of media grabber & permissions requester

val permissionsRequester = PermissionsRequester()
val mediaGrabber = MediaGrabber()

permissionsRequester.with(this)
    // returns a stream of booleans for each permission representing if it is granted or not 
    .request(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    .toList()
    .filter { // verify that all the requested permissions are granted, or continue if you know what you're doing
        var res = true
        it.forEach { if (!it) res = false }
        return@filter res
    }
    .flatMapSingle { // start working with the media grabber
        mediaGrabber.with(this@MainActivity).image() 
    }
    .subscribe({ Log.i("##", it) }, { it.printStackTrace() })

```
