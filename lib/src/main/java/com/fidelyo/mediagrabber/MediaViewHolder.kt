package com.fidelyo.mediagrabber

import android.view.View
import com.fidelyo.mediagrabber.model.entity.Image
import com.squareup.picasso.Picasso
import fidelyo.com.recyclerview.RecyclerViewViewHolder
import kotlinx.android.synthetic.main.item_media.view.*
import java.io.File

class MediaViewHolder(val view: View) : RecyclerViewViewHolder<Image>(view) {

    override fun onAttached(i: Image) {
        Picasso.with(view.context).load(File(i.thumbnailPath)).into(view.image)
    }

    override fun onDetached(i: Image) {

    }

}