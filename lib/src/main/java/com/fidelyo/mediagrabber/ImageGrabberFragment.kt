package com.fidelyo.mediagrabber

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import io.reactivex.ObservableEmitter

class ImageGrabberFragment : Fragment() {

    private var emitter: ObservableEmitter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun setEmitter(emitter: ObservableEmitter<String>): ImageGrabberFragment {
        this.emitter = emitter
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageGrabber.CODE) {
                if (data != null) {
                    if (emitter != null) {
                        emitter!!.onNext(data.getStringExtra(ImageGrabber.EXTRA))
                        emitter!!.onComplete()
                    }
                }
            }
        }
    }

}