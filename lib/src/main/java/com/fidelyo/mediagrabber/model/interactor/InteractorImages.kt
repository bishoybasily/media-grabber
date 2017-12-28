package com.fidelyo.mediagrabber.model.interactor

import android.content.Context
import android.provider.MediaStore
import com.fidelyo.mediagrabber.model.entity.Image
import io.reactivex.Observable

/**
 * Created by bishoy on 12/28/17.
 */

class InteractorImages(val context: Context) {

    fun findAll(): Observable<ArrayList<Image>> {
        val projection = arrayOf(MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA)
        val selection = ""
        val selectionArgs = arrayOf<String>()
        val sortOrder = MediaStore.Images.Thumbnails._ID + " DESC"

        return Observable.create {

            val result = ArrayList<Image>()

            val cursor = context.contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(projection[1]))
                val thumbnailPath = cursor.getString(cursor.getColumnIndex(projection[2]))

                val image = Image(id)
                image.thumbnailPath = thumbnailPath

                result.add(image)
            }
            cursor.close()

            it.onNext(result)
            it.onComplete()
        }
    }

    fun findOne(imageId: String): Observable<Image> {
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media._ID + "=?"
        val selectionArgs = arrayOf(imageId)
        val sortOrder = ""

        return Observable.create {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(projection[0]))
                val path = cursor.getString(cursor.getColumnIndex(projection[1]))

                val image = Image(id)
                image.path = path

                it.onNext(image)
            }
            cursor.close()
            it.onComplete()
        }

    }
}
