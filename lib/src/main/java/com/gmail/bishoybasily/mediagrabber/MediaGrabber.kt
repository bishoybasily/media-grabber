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

        private fun getImageGrabberFragment(activity: AppCompatActivity): ImageGrabberFragment {
            val fragmentManager = activity.supportFragmentManager
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

        private fun getProjectorGrabberFragment(activity: AppCompatActivity): ProjectorGrabberFragment {
            val fragmentManager = activity.supportFragmentManager
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
