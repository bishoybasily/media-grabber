package com.gmail.bishoybasily.mediagrabber

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Handler
import android.os.Looper
import android.view.View
import com.gmail.bishoybasily.mediagrabber.model.entity.Image
import com.gmail.bishoybasily.recyclerview.RecyclerViewViewHolder
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_media.view.*

class ViewHolderMedia(adapter: AdapterMedia,
                      view: View) :
        RecyclerViewViewHolder<Image>(adapter, view) {

    lateinit var disposable: Disposable

    override fun onAttached(i: Image) {
        view.image.setImageResource(0)

        val backgroundScheduler = Schedulers.computation()
        val mainScheduler = Schedulers.from { runnable -> Handler(Looper.getMainLooper()).post(runnable) }

        disposable = Single.fromCallable { BitmapFactory.decodeFile(i.path) }
                .map { ThumbnailUtils.extractThumbnail(it, view.image.width, view.image.height) }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .unsubscribeOn(backgroundScheduler)
                .subscribe({ view.image.setImageBitmap(it) }, { it.printStackTrace() })

    }

    override fun onDetached(i: Image) {
        view.image.setImageResource(0)

        if (::disposable.isInitialized)
            disposable.dispose()

    }

    fun decodeSampledBitmapFromResource(
            path: String
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            val decodeFile = BitmapFactory.decodeFile(path)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, decodeFile.width / 8, decodeFile.height / 8)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(path)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}