package com.gmail.bishoybasily.mediagrabber

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
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

    private val MAX_PREVIEW_WIDTH = 1920
    private val MAX_PREVIEW_HEIGHT = 1080

    lateinit var previewSize: Size

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


        val characteristics = cameraManager.getCameraCharacteristics(cameraId)

        val displayRotation = windowManager.defaultDisplay.rotation

        var mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                swappedDimensions = true
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                swappedDimensions = true
            }
            else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
        }

        val displaySize = Point()
        windowManager.defaultDisplay.getSize(displaySize)

        var maxPreviewWidth = displaySize.x
        var maxPreviewHeight = displaySize.y

        if (swappedDimensions) {
            maxPreviewWidth = displaySize.y
            maxPreviewHeight = displaySize.x
        }

        if (maxPreviewWidth > MAX_PREVIEW_WIDTH)
            maxPreviewWidth = MAX_PREVIEW_WIDTH
        if (maxPreviewHeight > MAX_PREVIEW_HEIGHT)
            maxPreviewHeight = MAX_PREVIEW_HEIGHT

        previewSize = Size(maxPreviewWidth, maxPreviewHeight)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cameraView.setAspectRatio(previewSize.width, previewSize.height)
        } else {
            cameraView.setAspectRatio(previewSize.height, previewSize.width)
        }

        cameraView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {

                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        previewSession(surfaceTexture)
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        camera.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        camera.close()
                    }

                }, mainHandler)

                fabCapture.setOnClickListener { captureSession(surfaceTexture) }

            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
//                Log.w("##", "onSurfaceTextureSizeChanged")
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
//                Log.w("##", "onSurfaceTextureUpdated")
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
//                Log.w("##", "onSurfaceTextureDestroyed")
                return false
            }

        }

    }

    private fun captureSession(surfaceTexture: SurfaceTexture) {

        surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)

        imageReaderCapture = ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
        imageReaderCapture.setOnImageAvailableListener({

            val image = imageReaderCapture.acquireLatestImage()
            val buffer = image.planes.first().buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            onCapture(bytes)

            image.close()
            it.close()

            previewSession(surfaceTexture)

        }, backgroundHandler)

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReaderCapture.surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS[windowManager.defaultDisplay.rotation])

        cameraDevice.createCaptureSession(Arrays.asList(imageReaderCapture.surface), object : CameraCaptureSession.StateCallback() {

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

    private fun previewSession(surfaceTexture: SurfaceTexture) {

        surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)

        val surface = Surface(surfaceTexture)

        imageReaderPreview = ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
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
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS[windowManager.defaultDisplay.rotation])

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

//        Log.w("##", "frame")

    }

    abstract fun getResourceLayout(): Int

}
