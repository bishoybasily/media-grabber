package com.gmail.bishoybasily.mediagrabber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.Single
import io.reactivex.SingleEmitter

class FragmentImageGrabber : Fragment() {

    private lateinit var emitter: SingleEmitter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == MediaGrabber.CODE)
                intent?.let { it.getStringExtra(MediaGrabber.EXTRA)?.let { emitter.onSuccess(it) } }

    }

    fun grap(): Single<String> {
        return Single.create {
            emitter = it
            startActivityForResult(Intent(activity, ActivityImageGrabber::class.java), MediaGrabber.CODE)
        }
    }

}