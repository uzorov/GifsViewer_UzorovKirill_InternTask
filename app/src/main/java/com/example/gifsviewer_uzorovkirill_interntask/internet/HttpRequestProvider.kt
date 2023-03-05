package com.example.gifsviewer_uzorovkirill_interntask.internet

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.gifsviewer_uzorovkirill_interntask.MainActivity
import com.example.gifsviewer_uzorovkirill_interntask.R
import com.example.gifsviewer_uzorovkirill_interntask.model.Gif
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.ref.WeakReference


class HttpRequestProvider(mainActivity: Activity, whatToSearch: String) :
    AsyncTask<Void, Void, String>() {

    private val weakActivity: WeakReference<Activity>
    private val whatToSearch: String
    private var isNetworkAvailable = false

    init {
        weakActivity = WeakReference(mainActivity)
        this.whatToSearch = whatToSearch
    }



    fun doRequest(whatToSearch: String): String {

        val url =
            "https://api.giphy.com/v1/gifs/search?api_key=2Ead55RzM5QZsE6649FegCaXArgjPc6d&q=${whatToSearch}&limit=25&offset=0&rating=g&lang=ru"
        var responseBody = ""
        if (url.isNotEmpty()) {
            val gifsDatabaseFetch = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            responseBody = gifsDatabaseFetch.newCall(request).execute().body?.string().toString()

        }
        return responseBody
    }


    override fun doInBackground(vararg params: Void?): String {
        isNetworkAvailable = isNetworkAvailable(weakActivity.get()?.applicationContext)

        return if (isNetworkAvailable) doRequest(whatToSearch) else ""
    }

    override fun onPostExecute(result: String?) {
        if (isNetworkAvailable) {
            MainActivity.Gifs.gifs.clear()
            processJson(result)
            MainActivity.GifRecycleView.recyclerView.adapter?.notifyDataSetChanged()

            if (MainActivity.Gifs.gifs.isEmpty()) {
                weakActivity.get()?.applicationContext?.let {
                    weakActivity.get()?.findViewById<ImageView>(R.id.imageView2)?.let { it1 ->
                        Glide
                            .with(it)
                            .load(R.drawable.ic_broken_image)
                            .placeholder(R.drawable.loading_animation)
                            //override(2048, 2048)
                            .into(it1)
                    }
                }

                weakActivity.get()?.findViewById<ImageView>(R.id.imageView2)?.visibility = View.VISIBLE
            }
            else {
                weakActivity.get()?.findViewById<ImageView>(R.id.imageView2)?.visibility = View.GONE
            }

        }
        else
        {

            weakActivity.get()?.applicationContext?.let {
                weakActivity.get()?.findViewById<ImageView>(R.id.imageView2)?.let { it1 ->
                    Glide
                        .with(it)
                        .load(R.drawable.ic_connection_error)
                        .placeholder(R.drawable.loading_animation)
                     //   .override(, 512)
                        .into(it1)
                }
            }

            weakActivity.get()?.findViewById<ImageView>(R.id.imageView2)?.visibility = View.VISIBLE
        }
        super.onPostExecute(result)
    }

    private fun processJson(result: String?) {
        try {
            val jsonObject = JSONTokener(result).nextValue() as JSONObject

            val jsonArray = jsonObject.getJSONArray("data")

            for (i in 0 until jsonArray.length()) {
                val gifItem =  Gif(
                    jsonArray.getJSONObject(i).getString("username"),
                    jsonArray.getJSONObject(i).getString("source"),
                    jsonArray.getJSONObject(i).getString("title"),
                    jsonArray.getJSONObject(i).getString("import_datetime"),
                    jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("original").getString("url")
                )

                MainActivity.Gifs.gifs.add(gifItem)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}