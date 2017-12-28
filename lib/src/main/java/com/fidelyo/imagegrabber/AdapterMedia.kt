package com.fidelyo.imagegrabber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fidelyo.imagegrabber.model.entity.Image
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_media.view.*
import java.io.File

/**
 * Created by bishoy on 12/27/17.
 */
class AdapterMedia : AdapterBase<Image, AdapterMedia.MediaViewHolder>() {

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
    }

    class MediaViewHolder(val view: View) : ViewHolderBase<Image>(view) {

        override fun onAttached(i: Image) {
            Picasso.with(view.context).load(File(i.thumbnailPath)).into(view.image)
        }

        override fun onDetached(i: Image) {

        }

    }

}