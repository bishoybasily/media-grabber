package com.fidelyo.mediagrabber.model.entity

import fidelyo.com.recyclerview.RecyclerViewAdapter

/**
 * Created by bishoy on 12/27/17.
 */

class Image : RecyclerViewAdapter.Item {
    var id: String? = null
    var path: String? = null
    var thumbnailPath: String? = null
}
