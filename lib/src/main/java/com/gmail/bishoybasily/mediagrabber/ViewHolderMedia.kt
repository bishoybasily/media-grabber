package com.gmail.bishoybasily.mediagrabber

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

}