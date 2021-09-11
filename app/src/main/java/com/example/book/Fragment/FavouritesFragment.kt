

package com.example.book.Fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.room.Room
import com.example.book.R
import com.example.book.adapter.FavouritesRecyclerAdapter
import com.example.book.database.BookDatabase
import com.example.book.database.BookEntites


@Suppress("DEPRECATION")
class FavouritesFragment : Fragment() {


    lateinit var recyclerFavourite: RecyclerView
    lateinit var recyclerAdapter: FavouritesRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: LinearLayoutManager

    //for book data
    var bookList = listOf<BookEntites>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressFavLayout)
        progressBar = view.findViewById(R.id.progressFavBar)

        layoutManager = GridLayoutManager(activity as Context, 2)
        bookList = RetrieveFavourites(activity as Context).execute().get()

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouritesRecyclerAdapter(activity as Context, bookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }


        return view
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntites>>(){


        override fun doInBackground(vararg p0: Void?): List<BookEntites> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

            return db.bookDao().getAllBooks()
        }


    }


}