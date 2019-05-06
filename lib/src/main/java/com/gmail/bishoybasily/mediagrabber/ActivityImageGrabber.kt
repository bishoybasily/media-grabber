package com.gmail.bishoybasily.mediagrabber

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bishoybasily.mediagrabber.model.interactor.InteractorFiles
import com.gmail.bishoybasily.mediagrabber.model.interactor.InteractorImages
import com.gmail.bishoybasily.recyclerview.LinearHorizontalSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_image_grabber.*

class ActivityImageGrabber : BaseActivityCamera() {

    private lateinit var interactorImages: InteractorImages
    private lateinit var interactorFiles: InteractorFiles

    override fun create(savedInstanceState: Bundle?) {
        initialize()
    }

    @SuppressLint("MissingPermission")
    private fun initialize() {

        interactorFiles = InteractorFiles(this)
        interactorImages = InteractorImages(this)

        val adapterMedia = AdapterMedia()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val spacing = LinearHorizontalSpacingItemDecoration(26)

        recyclerGallery.adapter = adapterMedia
        recyclerGallery.layoutManager = layoutManager
        recyclerGallery.addItemDecoration(spacing)

        adapterMedia.onClick { image, view ->
            compositeDisposable.add(interactorImages.findOne(image.id).map { it.path }.subscribe(::publishResult))
        }

        compositeDisposable.add(interactorImages.findAll().subscribe(adapterMedia::show))

    }

    override fun onCapture(it: ByteArray) {
        compositeDisposable.add(interactorFiles.temporary(it, "CAPTURED_AT_${System.currentTimeMillis()}", ".jpg").subscribe(::publishResult))
    }

    override fun getResourceLayout() = R.layout.activity_image_grabber

    private fun publishResult(it: String?) {
        setResult(RESULT_OK, intent.putExtra(MediaGrabber.IMAGE_EXTRA, it)); finish()
    }

}
