package com.example.book.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.book.R
import com.example.book.adapter.DashbordRecyclerAdapter
import com.example.book.model.Book
import com.example.book.util.ConnectionManagement
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class DashbordFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashbordRecyclerAdapter

    //progress bar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    //comparator func
    var ratingComparator = Comparator<Book> { book1, book2 ->

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    //functionaloty for net button
    //lateinit var btnCheckInternet: Button

    val bookInfoList = arrayListOf<Book>()
    //empty list for json array


    /*
          Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
          Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
          Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
          Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
          Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
          Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
          Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
          Book(
              "The Adventures of Huckleberry Finn",
              "Mark Twain",
              "Rs. 699",
              "4.5",
              R.drawable.adventures_finn
          ),
          Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
          Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
     */


    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashbord, container, false)

        //to set that there is a menu option
        setHasOptionsMenu(true)
        //net button
        //btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        //progress bar
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.rlProgress)

        progressLayout.visibility = View.VISIBLE

/*
CHECK INTERNET
        //always implement this before a network request
        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                //net is connected
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection found")
                dialog.setPositiveButton("OK") {
                    //adding click listener to text
                        text, listener ->
                    //do nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    //do nothing
                }
                dialog.create()
                dialog.show()
            } else {
                //net not connected
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Failure")
                dialog.setMessage("No Connection found")
                dialog.setPositiveButton("OK") {
                    //adding click listener to text
                        text, listener ->
                    //do nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    //do nothing
                }
                dialog.create()
                dialog.show()
            }
        }
*/

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        //we don;t use this keyword for fragments
        layoutManager = LinearLayoutManager(activity)


        //GET request for the list of books
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManagement().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {
                    //handle the response
                    //code for parsing json
                    try {

                        //removes the bar when it loads

                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            //extract data
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJSON = data.getJSONObject(i)
                                val book = Book(
                                    //extracting element wise data
                                    bookJSON.getString("book_id"),
                                    bookJSON.getString("name"),
                                    bookJSON.getString("author"),
                                    bookJSON.getString("rating"),
                                    bookJSON.getString("price"),
                                    bookJSON.getString("image")
                                )
                                bookInfoList.add(book)
                                recyclerAdapter =
                                    DashbordRecyclerAdapter(activity as Context, bookInfoList)
                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                                //add lines to segregate
                                /*recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                                 */

                            }
                        } else {
                            //failure in getting data
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    "Error Occurred!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }

                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {
                    //handle the error
                    Toast.makeText(
                        activity as Context,
                        "Volley Error Occurred!",
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
            //add the request to the queue
            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure")
            dialog.setMessage("No Connection found")
            dialog.setPositiveButton("Open Settings") {
                //adding click listener to text
                    text, listener ->
                //open setting
                val openSetting = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSetting)
                activity?.finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                //exit
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }
        return view
    }

    //for the sort option menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menudashbord, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            //will return in decreasing order of rating
            Collections.sort(bookInfoList, ratingComparator)
            //we want increasing,hence reverse
            bookInfoList.reverse()
        }
        //notfy adapter of changes made
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }


}