package com.gmail.bishoybasily.mediagrabber

import android.view.View
import com.gmail.bishoybasily.mediagrabber.model.entity.Image
import com.gmail.bishoybasily.recyclerview.RecyclerViewViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_media.view.*
import java.io.File

class ViewHolderMedia(adapter: AdapterMedia,
                      view: View) :
        RecyclerViewViewHolder<Image>(adapter, view) {

    override fun onAttached(i: Image) {
        view.image.setImageResource(0)

        Picasso.with(view.context).load(File(i.thumbnailPath)).into(view.image)

    }

    override fun onDetached(i: Image) {
        view.image.setImageResource(0)
    }

}