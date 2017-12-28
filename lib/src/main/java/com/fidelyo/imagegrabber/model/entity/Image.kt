package com.fidelyo.imagegrabber.model.entity

import com.fidelyo.imagegrabber.AdapterBase

/**
 * Created by bishoy on 12/27/17.
 */

class Image : AdapterBase.Item {
    var id: String? = null
    var path: String? = null
    var thumbnailPath: String? = null
}
