package com.fidelyo.mediagrabber

import android.app.Activity
import android.content.Intent
import io.reactivex.Observable

/**
 * Created by bishoy on 12/26/17.
 */

class ImageGrabber {

    fun with(activity: Activity): Grabber {
        return Grabber(activity)
    }

    open class Grabber(val activity: Activity) {

        val TAG = javaClass.simpleName

        fun grab(): Observable<String> {
            return Observable.create { e ->
                getFragment(activity).setEmitter(e).startActivityForResult(Intent(activity, ActivityImageGrabber::class.java), CODE)
            }
        }

        private fun getFragment(activity: Activity): ImageGrabberFragment {
            val fragmentManager = activity.fragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = ImageGrabberFragment()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as ImageGrabberFragment
        }
    }


    companion object {

        val CODE = 40
        val EXTRA = "extra_path"
    }
}
