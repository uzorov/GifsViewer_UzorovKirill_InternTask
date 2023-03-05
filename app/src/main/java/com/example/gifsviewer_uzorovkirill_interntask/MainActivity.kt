package com.example.gifsviewer_uzorovkirill_interntask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.example.gifsviewer_uzorovkirill_interntask.internet.HttpRequestProvider
import com.example.gifsviewer_uzorovkirill_interntask.model.Gif
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener, CellClickListener {


    lateinit var inputField: TextInputEditText
    lateinit var searchButton: Button
    lateinit var requestProvider: HttpRequestProvider

    object GifRecycleView {
        lateinit var recyclerView: RecyclerView
    }


    object Gifs {
        val gifs: MutableList<Gif> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "GIPHY SEARCHER"
        supportActionBar?.subtitle = "Узоров Кирилл"

        inputField = findViewById(R.id.inputField)
        searchButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener(this)
        GifRecycleView.recyclerView = findViewById(R.id.recycleView)

        GifRecycleView.recyclerView.layoutManager = LinearLayoutManager(this)
        GifRecycleView.recyclerView.adapter = GifRecyclerAdapter(Gifs.gifs, this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchButton -> {

                requestProvider = HttpRequestProvider(this, inputField.text.toString())
                GlobalScope.launch {
                    requestProvider.execute()
                }
            }
        }
    }

    override fun onCellClickListener(position: Int) {
        val intent = Intent(this@MainActivity, GifsDetailsActivity::class.java)
        intent.putExtra("pos", position)
        startActivity(intent)
    }


}


class GifRecyclerAdapter(
    private val gifs: List<Gif>,
    private val cellClickListener: CellClickListener,
) :
    RecyclerView.Adapter<GifRecyclerAdapter.GifViewHolder>() {

    class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gif: ImageView = itemView.findViewById(R.id.imageView)
        val text: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.one_item_gif, parent, false)
        return GifViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return gifs.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        Glide
            .with(holder.itemView.context)
            .load(MainActivity.Gifs.gifs[position].url)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_connection_error)
            .override(200, 200)
            .into(holder.gif)

        holder.text.text =
            "${MainActivity.Gifs.gifs[position].title} \n\n Автор: ${MainActivity.Gifs.gifs[position].username}" +
                    "\n\n Нажмите, чтобы увидеть больше деталей"

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(position)
        }
    }
}


interface CellClickListener {
    fun onCellClickListener(position: Int)
}