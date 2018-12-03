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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bishoybasily.mediagrabber.model.interactor.InteractorFiles
import com.gmail.bishoybasily.mediagrabber.model.interactor.InteractorImages
import com.gmail.bishoybasily.recyclerview.LinearHorizontalSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_image_grabber.*
import java.util.*

class ActivityImageGrabber : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var cameraDevice: CameraDevice
    private lateinit var interactorImages: InteractorImages
    private lateinit var interactorFiles: InteractorFiles

    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundHandler = Handler(Looper.myLooper())

    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_grabber)

        supportActionBar?.hide()

        initializeCamera()
        initializeGallery()

    }

    @SuppressLint("MissingPermission")
    private fun initializeCamera() {

        interactorFiles = InteractorFiles(this@ActivityImageGrabber)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.first()

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

    @SuppressLint("MissingPermission")
    private fun initializeGallery() {

        interactorImages = InteractorImages(this)

        val adapterMedia = AdapterMedia()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val spacing = LinearHorizontalSpacingItemDecoration(26)

        recyclerGallery.adapter = adapterMedia
        recyclerGallery.layoutManager = layoutManager
        recyclerGallery.addItemDecoration(spacing)

        adapterMedia.onClick { image, view ->
            interactorImages.findOne(image.id!!).subscribe { publishResult(it.path!!) }
        }

        interactorImages.findAll().subscribe { adapterMedia.showAll(it) }

    }

    private fun captureSession(width: Int, height: Int, surfaceTexture: SurfaceTexture) {

        val surface = Surface(surfaceTexture)

        val imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener({

            val image = imageReader.acquireLatestImage()
            val buffer = image.planes.first().buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            interactorFiles.temporary(bytes, "CAPTURED_AT_${System.currentTimeMillis()}", ".jpg").subscribe {
                publishResult(it)
            }

            image.close()
            it.close()

            previewSession(surfaceTexture)

        }, backgroundHandler)

        cameraDevice.createCaptureSession(Arrays.asList(imageReader.surface, surface), object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) {
                val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(imageReader.surface)
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS[windowManager.defaultDisplay.rotation])

                val captureRequest = captureRequestBuilder.build()

                session.capture(captureRequest, object : CameraCaptureSession.CaptureCallback() {

                    override fun onCaptureFailed(session: CameraCaptureSession?, request: CaptureRequest, failure: CaptureFailure) {
                        super.onCaptureFailed(session, request, failure)
                        Log.e(TAG, "onCaptureFailed")
                    }

                }, backgroundHandler)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed")
            }

        }, backgroundHandler)
    }

    private fun previewSession(surfaceTexture: SurfaceTexture) {

        val surface = Surface(surfaceTexture)

        cameraDevice.createCaptureSession(Collections.singletonList(surface), object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) {

                val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder.addTarget(surface)
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
                val captureRequest = captureRequestBuilder.build()

                session.setRepeatingRequest(captureRequest, object : CameraCaptureSession.CaptureCallback() {

                    override fun onCaptureFailed(session: CameraCaptureSession?, request: CaptureRequest, failure: CaptureFailure) {
                        super.onCaptureFailed(session, request, failure)
                        Log.e(TAG, "onCaptureFailed")
                    }

                }, backgroundHandler)

            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed")
            }

        }, mainHandler)
    }

    private fun publishResult(it: String) {
        setResult(RESULT_OK, intent.putExtra(MediaGrabber.IMAGE_EXTRA, it)); finish()
    }

}