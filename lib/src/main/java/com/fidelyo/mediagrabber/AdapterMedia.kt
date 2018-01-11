package com.fidelyo.mediagrabber

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fidelyo.mediagrabber.model.entity.Image
import com.fidelyo.recyclerview.RecyclerViewAdapter

/**
 * Created by bishoy on 12/27/17.
 */
class AdapterMedia : RecyclerViewAdapter<Image, MediaViewHolder>() {

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(this, LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
    }

}