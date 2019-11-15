package com.gmail.bishoybasily.mediagrabber

import android.annotation.SuppressLint
import android.os.Bundle

class ActivityLiveGrabber : BaseActivityCamera() {

    override fun create(savedInstanceState: Bundle?) {
        initialize()
    }

    @SuppressLint("MissingPermission")
    private fun initialize() {

    }

    override fun onFrame(it: ByteArray) {
//        compositeDisposable.add(

//                interactorFiles.temporary(it, "CAPTURED_AT_${System.currentTimeMillis()}", ".jpg")
//                        .subscribe {
//                            publishResult(it)
//                        }

//        )
    }

    override fun getResourceLayout() = R.layout.activity_live_grabber

    private fun publishResult(it: String?) {
        setResult(RESULT_OK, intent.putExtra(MediaGrabber.EXTRA, it)); finish()
    }

}
