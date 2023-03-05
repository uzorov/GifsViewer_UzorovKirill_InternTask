package com.example.gifsviewer_uzorovkirill_interntask

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class GifsDetailsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifs_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val position = intent.getIntExtra("pos", 0)

       Glide
           .with(this)
            .load(MainActivity.Gifs.gifs[position].url)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_connection_error)
            .override(350, 350)
            .into(findViewById<ImageView>(R.id.bigImageView))

        findViewById<TextView>(R.id.author).text = "Автор: " + MainActivity.Gifs.gifs[position].username
        findViewById<TextView>(R.id.source).text = "Источник: " + MainActivity.Gifs.gifs[position].source
        findViewById<TextView>(R.id.title).text =  MainActivity.Gifs.gifs[position].title
        findViewById<TextView>(R.id.datetime).text = "Время загрузки: " +  MainActivity.Gifs.gifs[position].import_datetime



        supportActionBar?.title = "GIPHY SEARCHER"
        supportActionBar?.subtitle = "Узоров Кирилл"


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}