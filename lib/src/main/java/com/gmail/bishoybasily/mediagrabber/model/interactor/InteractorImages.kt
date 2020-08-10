package com.gmail.bishoybasily.mediagrabber.model.interactor

import android.content.Context
import android.provider.MediaStore
import com.gmail.bishoybasily.mediagrabber.model.entity.Image
import io.reactivex.Observable

/**
 * Created by bishoy on 12/28/17.
 */

class InteractorImages(val context: Context) {

    fun all(): Observable<Image> {

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.DATA)
        val selection = ""
        val selectionArgs = arrayOf<String>()
        val sortOrder = MediaStore.Images.Media._ID + " DESC"

        return Observable.create {

            val res = arrayListOf<Image>()

            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.let { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(projection[0]))
                    val width = cursor.getInt(cursor.getColumnIndex(projection[1]))
                    val height = cursor.getInt(cursor.getColumnIndex(projection[2]))
                    val path = cursor.getString(cursor.getColumnIndex(projection[3]))
                    res.add(Image.from(id, width, height, path))
                }
                cursor.close()
            }

            res.forEach(it::onNext) // cache
            it.onComplete()
        }
    }
}
