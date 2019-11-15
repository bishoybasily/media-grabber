package com.gmail.bishoybasily.mediagrabber

import android.widget.ImageView
import androidx.fragment.app.FragmentActivity


/**
 * Created by bishoy on 12/26/17.
 */
class MediaGrabber {

    fun with(activity: FragmentActivity): Grabber {
        return Grabber(activity)
    }

    open class Grabber(val activity: FragmentActivity) {

        val TAG = javaClass.simpleName

        fun image() = getImageGrabberFragment(activity).grap()

        fun file() = getFileGrabberFragment(activity).grap()

        private fun getImageGrabberFragment(activity: FragmentActivity): FragmentImageGrabber {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = FragmentImageGrabber()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as FragmentImageGrabber
        }

        private fun getFileGrabberFragment(activity: FragmentActivity): FragmentFileGrabber {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = FragmentFileGrabber()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as FragmentFileGrabber
        }

    }

    companion object {

        val CODE = 40
        val EXTRA = "media_grabber_extra"

        lateinit var drawImage: (String?, ImageView) -> Unit

    }

}
