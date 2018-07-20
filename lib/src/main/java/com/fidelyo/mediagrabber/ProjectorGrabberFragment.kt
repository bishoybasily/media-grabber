package com.fidelyo.mediagrabber

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.DisplayMetrics
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ProjectorGrabberFragment : Fragment() {

    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private val STATE_RESULT_CODE = "result_code"
    private val STATE_RESULT_DATA = "result_data"

    private var mMediaProjectionManager: MediaProjectionManager? = null

    private var mScreenDensity: Int = 0
    private var mMediaProjection: MediaProjection? = null

    private var emitter: ObservableEmitter<MediaProjection>? = null

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

        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mMediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

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
            if (requestCode == Grabber.PROJECTOR_CODE) {
                if (data != null) {
                    if (emitter != null) {

                        mResultCode = resultCode
                        mResultData = data

                        mMediaProjection = mMediaProjectionManager?.getMediaProjection(mResultCode, mResultData)

                        emitter?.onNext(mMediaProjection!!)
                        emitter?.onComplete()
                        return
                    }
                }
            }
        }
        emitter?.onError(Throwable("User cancelled"))
    }

    fun grap(): Observable<MediaProjection> {
        return Observable
                .create<MediaProjection> {

                    emitter = it

                    if (mMediaProjection == null) {
                        startActivityForResult(mMediaProjectionManager?.createScreenCaptureIntent(), Grabber.PROJECTOR_CODE)
                    } else {
                        emitter?.onNext(mMediaProjection!!)
                        emitter?.onComplete()
                    }

                }
                .doOnDispose {
                    mMediaProjection?.stop()
                    mMediaProjection = null
                }
                .doOnError {
                    mMediaProjection?.stop()
                    mMediaProjection = null
                }
    }

}