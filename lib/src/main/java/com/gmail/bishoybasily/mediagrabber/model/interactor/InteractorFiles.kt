package com.gmail.bishoybasily.mediagrabber.model.interactor

import android.content.Context
import io.reactivex.Single
import java.io.File

/**
 * Created by bishoy on 12/28/17.
 */

class InteractorFiles(val context: Context) {

    fun temporary(bytes: ByteArray, prefix: String, suffix: String): Single<String> {
        return Single.create {
            val file = File.createTempFile(prefix, suffix)
            file.outputStream().use { it.write(bytes) }
            it.onSuccess(file.absolutePath)
        }
    }

}
