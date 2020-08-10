package com.gmail.bishoybasily.mediagrabber.model.entity

import android.util.Size
import com.gmail.bishoybasily.recyclerview.RecyclerViewAdapter

/**
 * Created by bishoy on 12/27/17.
 */

class Image : RecyclerViewAdapter.Item {

    lateinit var id: String
    lateinit var path: String
    lateinit var size: Size

    companion object {

        fun from(i: String, path: String): Image {
            return Image().apply {
                this@apply.id = i
                this@apply.path = path
            }
        }

        fun from(i: String, width: Int, height: Int, path: String): Image {
            return Image().apply {
                this@apply.id = i
                this@apply.path = path
                this@apply.size = Size(width, height)
            }
        }

    }

}
