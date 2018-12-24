package com.gmail.bishoybasily.mediagrabber

import androidx.appcompat.app.AppCompatActivity


/**
 * Created by bishoy on 12/26/17.
 */
class MediaGrabber {

    fun with(activity: AppCompatActivity): Grabber {
        return Grabber(activity)
    }

    open class Grabber(val activity: AppCompatActivity) {

        val TAG = javaClass.simpleName

        fun grabImage() = getImageGrabberFragment(activity).grap()

        fun grabProjector() = getProjectorGrabberFragment(activity).grap()

        private fun getImageGrabberFragment(activity: AppCompatActivity): FragmentImageGrabber {
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

        private fun getProjectorGrabberFragment(activity: AppCompatActivity): FragmentProjectorGrabber {
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

    }

}
