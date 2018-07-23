package com.fidelyo.mediagrabber

import android.app.Activity

/**
 * Created by bishoy on 12/26/17.
 */
class MediaGrabber {

    fun with(activity: Activity): Grabber {
        return Grabber(activity)
    }

    open class Grabber(val activity: Activity) {

        val TAG = javaClass.simpleName

        fun grabImage() = getImageGrabberFragment(activity).grap()

        fun grabProjector() = getProjectorGrabberFragment(activity).grap()

        private fun getImageGrabberFragment(activity: Activity): ImageGrabberFragment {
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

        private fun getProjectorGrabberFragment(activity: Activity): ProjectorGrabberFragment {
            val fragmentManager = activity.fragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = ProjectorGrabberFragment()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as ProjectorGrabberFragment
        }
    }

    companion object {

        val IMAGE_CODE = 40
        val IMAGE_EXTRA = "extra_path"

        val PROJECTOR_CODE = 41

    }
}
