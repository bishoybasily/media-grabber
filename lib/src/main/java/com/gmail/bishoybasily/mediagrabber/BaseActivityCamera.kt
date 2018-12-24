package com.gmail.bishoybasily.mediagrabber

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_image_grabber.*
import java.util.*

abstract class BaseActivityCamera : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var cameraDevice: CameraDevice

    protected val compositeDisposable = CompositeDisposable()

    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundHandler = Handler(Looper.myLooper())

    private val ORIENTATIONS = SparseIntArray()


    private lateinit var imageReaderPreview: ImageReader
    private lateinit var imageReaderCapture: ImageReader

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getResourceLayout())

        supportActionBar?.hide()

        initialize()

        create(savedInstanceState)

    }

    open fun create(savedInstanceState: Bundle?) {

    }

    @SuppressLint("MissingPermission")
    private fun initialize() {

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.first()

        cameraView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {

                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        previewSession(width, height, surfaceTexture)
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        camera.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        camera.close()
                    }

                }, mainHandler)

                fabCapture.setOnClickListener {
                    captureSession(width, height, surfaceTexture)
                }

            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

        }
    }

    private fun captureSession(width: Int, height: Int, surfaceTexture: SurfaceTexture) {

        val surface = Surface(surfaceTexture)

        imageReaderCapture = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        imageReaderCapture.setOnImageAvailableListener({

            val image = imageReaderCapture.acquireLatestImage()
            val buffer = image.planes.first().buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            onCapture(bytes)

            image.close()
            it.close()

            previewSession(width, height, surfaceTexture)

        }, backgroundHandler)

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReaderCapture.surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS[windowManager.defaultDisplay.rotation])


        cameraDevice.createCaptureSession(Arrays.asList(imageReaderCapture.surface, surface), object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) {

                val captureRequest = captureRequestBuilder.build()

                session.capture(captureRequest, object : CameraCaptureSession.CaptureCallback() {

                    override fun onCaptureFailed(session: CameraCaptureSession?, request: CaptureRequest, failure: CaptureFailure) {
                        super.onCaptureFailed(session, request, failure)
                        Log.e(TAG, "onCaptureFailed: ${failure.reason}")
                    }

                }, backgroundHandler)

            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed")
            }

        }, backgroundHandler)
    }

    private fun previewSession(width: Int, height: Int, surfaceTexture: SurfaceTexture) {

        val surface = Surface(surfaceTexture)

        imageReaderPreview = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        imageReaderPreview.setOnImageAvailableListener({ reader ->

            val image = imageReaderPreview.acquireLatestImage()
            val buffer = image.planes.first().buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            onFrame(bytes)

            image.close()

        }, backgroundHandler)

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)
        captureRequestBuilder.addTarget(imageReaderPreview.surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)


//        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//        captureRequestBuilder.addTarget(surface)
//        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)


        cameraDevice.createCaptureSession(Arrays.asList(surface, imageReaderPreview.surface), object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) {

                val captureRequest = captureRequestBuilder.build()

                session.setRepeatingRequest(captureRequest, object : CameraCaptureSession.CaptureCallback() {

                    override fun onCaptureFailed(session: CameraCaptureSession?, request: CaptureRequest, failure: CaptureFailure) {
                        super.onCaptureFailed(session, request, failure)
                        Log.e(TAG, "onCaptureFailed: ${failure.reason}")
                    }

                }, backgroundHandler)

            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed")
            }

        }, mainHandler)
    }

    open fun destroy() {
        if (::imageReaderPreview.isInitialized)
            imageReaderPreview.close()
        if (::imageReaderCapture.isInitialized)
            imageReaderCapture.close()
    }

    final override fun onDestroy() {
        destroy()
        compositeDisposable.clear()
        super.onDestroy()
    }

    open fun onCapture(it: ByteArray) {
    }

    open fun onFrame(it: ByteArray) {

        Log.w("##", "frame")

    }

    abstract fun getResourceLayout(): Int

}
