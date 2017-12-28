package com.fidelyo.mediagrabber.model.entity

import com.fidelyo.mediagrabber.AdapterBase

/**
 * Created by bishoy on 12/27/17.
 */

data class Image(var id: String? = null,
                 var path: String? = null,
                 var thumbnailPath: String? = null) : AdapterBase.Item
