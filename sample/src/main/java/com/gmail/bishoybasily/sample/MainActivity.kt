package com.gmail.bishoybasily.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bishoybasily.mediagrabber.MediaGrabber
import com.gmail.bishoybasily.permissionsrequester.PermissionsRequester
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val mediaGrabber = MediaGrabber()
        val permissionsRequester = PermissionsRequester()

        fab.setOnClickListener { view ->

//            mediaGrabber
//                    .with(this@MainActivity)
//                    .image()
//                    .subscribe({ Picasso.with(this).load(File(it)).into(image) }, { it.printStackTrace() })

            permissionsRequester.with(this)
                    .request(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .toList()
                    .filter {
                        var res = true
                        it.forEach {
                            if (!it) {
                                res = false
                            }
                        }
                        return@filter res
                    }
                    .flatMapSingle { mediaGrabber.with(this@MainActivity).image() }
                    .subscribe({ Log.i("##", it) }, { it.printStackTrace() })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
