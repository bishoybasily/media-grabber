package com.gmail.bishoybasily.mediagrabber

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.Single
import io.reactivex.SingleEmitter

class FragmentProjectorGrabber : Fragment() {

    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private val STATE_RESULT_CODE = "result_code"
    private val STATE_RESULT_DATA = "result_data"

    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mMediaProjection: MediaProjection
    private lateinit var emitter: SingleEmitter<MediaProjection>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE)
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mMediaProjectionManager = activity?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode)
            outState.putParcelable(STATE_RESULT_DATA, mResultData)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MediaGrabber.PROJECTOR_CODE) {
                if (data != null) {
                    mResultCode = resultCode
                    mResultData = data

                    mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)

                    emitter.onSuccess(mMediaProjection)

                    return
                }
            }
        }
        emitter.onError(Throwable("User cancelled"))
    }

    fun grap(): Single<MediaProjection> {
        return Single.create<MediaProjection> {

            emitter = it

            if (::mMediaProjection.isInitialized) {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), MediaGrabber.PROJECTOR_CODE)
            } else {
                emitter.onSuccess(mMediaProjection)
            }

        }.doOnDispose {
            mMediaProjection.stop()
        }.doOnError {
            mMediaProjection.stop()
        }
    }

}