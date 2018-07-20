package com.fidelyo.mediagrabber

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ImageGrabberFragment : Fragment() {

    private var emitter: ObservableEmitter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Grabber.IMAGE_CODE) {
                if (data != null) {
                    if (emitter != null) {
                        emitter?.onNext(data.getStringExtra(Grabber.IMAGE_EXTRA))
                        emitter?.onComplete()
                        return
                    }
                }
            }
        }
        emitter?.onError(Throwable("User cancelled"))
    }

    fun grap(): Observable<String> {
        return Observable.create {
            emitter = it
            startActivityForResult(Intent(activity, ActivityImageGrabber::class.java), Grabber.IMAGE_CODE)
        }
    }

}