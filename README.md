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
    implementation 'com.github.bishoybasily:media-grabber:4.1.0'
}
```

### Usage

##### Enable Camera & Storage Permissions

##### Instruct the tool how to draw the image in the bottom recycler-view, you can use picasso, glide, or even drawing it manually

```kotlin

MediaGrabber.drawImage = { path, imageView -> Picasso.with(this).load(File(path)).into(imageView) }

```

##### Create a new instance of MediaGrabber

```kotlin

// a new instance of media grabber, it can be injected with dagger
val mediaGrabber = MediaGrabber()

mediaGrabber
    .with(context)
    // image will start the camera and will read the images form local storage in a whatsapp-like view
    // you can also call file instead of images if you want to select from the local storage only
    .image() 
    .subscribe(
            {
                // it > holds a reference to the selected file 
                Log.i("##", it)
            },
            {
                it.printStackTrace()
            }
    )

```
