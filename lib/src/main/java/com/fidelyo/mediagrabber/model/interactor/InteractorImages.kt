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
        val projection = arrayOf(MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA)
        val sortOrder = MediaStore.Images.Thumbnails._ID + " DESC"
        val selection = ""
        val selectionArgs = arrayOf<String>()

        return Observable.create {

            val result = ArrayList<Image>()

            val cursor = context.contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getString(cursor.getColumnIndex(projection[0]))
                    val thumbnailPath = cursor.getString(cursor.getColumnIndex(projection[1]))
                    result.add(Image().apply { this@apply.id = id }.apply { this@apply.thumbnailPath = thumbnailPath })
                    cursor.moveToNext()
                }
            }
            cursor.close()

            it.onNext(result)
            it.onComplete()
        }
    }

    fun findOne(imageId: String): Observable<Image> {
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val sortOrder = ""
        val selection = MediaStore.Images.Media._ID + "=?"
        val selectionArgs = arrayOf(imageId)

        return Observable.create {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getString(cursor.getColumnIndex(projection[0]))
                    val path = cursor.getString(cursor.getColumnIndex(projection[1]))
                    it.onNext(Image().apply { this@apply.id = id }.apply { this@apply.path = path })
                    cursor.moveToNext()
                }
            }
            cursor.close()
            it.onComplete()
        }

    }
}
