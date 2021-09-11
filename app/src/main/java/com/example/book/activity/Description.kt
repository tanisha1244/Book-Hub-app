

package com.example.book.activity

import android.content.Context
import android.content.Intent
import android.media.audiofx.EnvironmentalReverb
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.*
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.book.R
import com.example.book.database.BookDatabase
import com.example.book.database.BookEntites
import com.example.book.util.ConnectionManagement
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class Description : AppCompatActivity() {
    lateinit var btnAddtoFavourites: Button
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookCost: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookRating: TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var txtbookDesc: TextView
    lateinit var toolbar: Toolbar

    //give some random value
    var bookId: String? = "100"

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        btnAddtoFavourites = findViewById(R.id.btnAddtoFav)
        txtBookName = findViewById(R.id.txtDescBookName)
        txtBookAuthor = findViewById(R.id.txtDescBookAuthor)
        txtBookRating = findViewById(R.id.txtDescBookRating)
        txtBookCost = findViewById(R.id.txtDescBookPrice)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        imgBookImage = findViewById(R.id.imgDescBookImage)
        txtbookDesc = findViewById(R.id.txtDescBookDescription)
        //set visiblity for progress bar
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE
        //till we get actual data

        toolbar = findViewById(R.id.toolbarDescription)
        setSupportActionBar(toolbar)
        //title for toolbar
        supportActionBar?.title = "Book Details"

        //when any item is clicked..id is sent to this activity via intent
        //book id is also sent to server in post request
        //response is JSONObject
        //parse and display to the views

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this@Description , "Some error occurred!", Toast.LENGTH_SHORT)
                .show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(this@Description, "Some error occurred!", Toast.LENGTH_SHORT)
                .show()
        }

        val queue = Volley.newRequestQueue(this@Description)
        val url = "http://13.235.250.119/v1/book/get_book/"

        //post request
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManagement().checkConnectivity(this@Description)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    //response
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val bookJSONObject = it.getJSONObject("book_data")

                            val imageURL = bookJSONObject.getString("image")

                            Picasso.get().load(bookJSONObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJSONObject.getString("name")
                            txtBookAuthor.text = bookJSONObject.getString("author")
                            txtBookRating.text = bookJSONObject.getString("rating")
                            txtBookCost.text = bookJSONObject.getString("price")
                            txtbookDesc.text = bookJSONObject.getString("description")

                            //BookEntity for favoruites
                            val bookEntity = BookEntites(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookCost.text.toString(),
                                txtBookRating.text.toString(),
                                txtbookDesc.text.toString(),
                                imageURL
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFave = checkFav.get()
                            if (isFave) {
                                btnAddtoFavourites.text = "Remove from favourites"
                                val favcolor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.design_default_color_primary_dark
                                )
                                btnAddtoFavourites.setBackgroundColor(favcolor)
                            } else {
                                btnAddtoFavourites.text = "Add from favourites"
                                val nofavcolor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.design_default_color_primary
                                )
                                btnAddtoFavourites.setBackgroundColor(nofavcolor)
                            }

                            btnAddtoFavourites.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@Description,
                                            "Book added to favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddtoFavourites.text = "Remove from favourites"
                                        val newcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.design_default_color_primary_dark
                                        )
                                        btnAddtoFavourites.setBackgroundColor(newcolor)
                                    } else {
                                        Toast.makeText(
                                            this@Description,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@Description,
                                            "Book removed from favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddtoFavourites.text = "Add to Favourites"
                                        val newcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.design_default_color_primary_dark
                                        )
                                        btnAddtoFavourites.setBackgroundColor(newcolor)
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@Description,
                                "Some error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Description,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@Description,
                        "Volley error occurred",
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "6ad8e26e532c7d"
                        return headers
                    }

                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@Description)
            dialog.setTitle("Failure")
            dialog.setMessage("No Connection found")
            dialog.setPositiveButton("Open Settings") {
                //adding click listener to text
                    text, listener ->
                //open setting
                val openSetting = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSetting)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                //exit
                ActivityCompat.finishAffinity(this@Description)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntites, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        /*
        1-> Check if in favs or not
        2-> Save in favs
        3->Remove from favs
         */

        //building database using the database class
        val db = Room.databaseBuilder(context,BookDatabase::class.java, "books-db").build()


        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntites? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                //check fav
                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                    //as no chance to get a null value
                }
                //add to fav
                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                    //remove from fav
                }
            }

            //for the time being
            return false
        }

    }


}