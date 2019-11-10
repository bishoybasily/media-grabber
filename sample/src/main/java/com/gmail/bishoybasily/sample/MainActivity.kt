package com.gmail.bishoybasily.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bishoybasily.mediagrabber.MediaGrabber
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        MediaGrabber.drawImage = { path, imageView -> Picasso.with(this).load(File(path)).into(imageView) }

        fab.setOnClickListener { view ->

            val mediaGrabber = MediaGrabber()

            mediaGrabber
                    .with(this@MainActivity)
                    .image()
                    .subscribe({ Picasso.with(this).load(File(it)).into(image) }, { it.printStackTrace() })

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
