package com.gmail.bishoybasily.mediagrabber.model.interactor

import android.content.Context
import io.reactivex.Observable
import java.io.File

/**
 * Created by bishoy on 12/28/17.
 */

class InteractorFiles(val context: Context) {

    fun temporary(bytes: ByteArray, prefix: String, suffix: String): Observable<String> {
        return Observable.create {
            val file = File.createTempFile(prefix, suffix)
            file.outputStream().use { it.write(bytes) }
            it.onNext(file.absolutePath)
            it.onComplete()
        }
    }

}
