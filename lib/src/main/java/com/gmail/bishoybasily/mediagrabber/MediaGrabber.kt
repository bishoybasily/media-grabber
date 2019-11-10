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

        fun projector() = getProjectorGrabberFragment(activity).grap()

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

        private fun getProjectorGrabberFragment(activity: FragmentActivity): FragmentProjectorGrabber {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = FragmentProjectorGrabber()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as FragmentProjectorGrabber
        }
    }

    companion object {

        val IMAGE_CODE = 40
        val IMAGE_EXTRA = "extra_path"

        val PROJECTOR_CODE = 41

        lateinit var drawImage: (String?, ImageView) -> Unit

    }

}
