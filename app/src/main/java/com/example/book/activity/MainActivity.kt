package com.example.book.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import com.example.book.Fragment.AboutappFragment
import com.example.book.Fragment.DashbordFragment
import com.example.book.Fragment.FavouritesFragment
import com.example.book.Fragment.ProfileFragment
import com.example.book.R

class MainActivity : AppCompatActivity() {

    lateinit var dawble: DrawerLayout
    lateinit var cordi: CoordinatorLayout
    lateinit var toolbar:Toolbar
    lateinit var frame:FrameLayout
    lateinit var navigation: NavigationView


    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dawble = findViewById(R.id.drawlayou)
        cordi = findViewById(R.id.layout)
        toolbar = findViewById(R.id.tools)
        frame = findViewById(R.id.frame)
        navigation = findViewById(R.id.navigation)

        setuptoolbar()
        //Open default
        openDashboard()
        //Put action bar toggle

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            dawble,
            R.string.open_drawer,
            R.string.close_drawer
        )

        dawble.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigation.setNavigationItemSelectedListener {
            //checking and unchecking of menu items
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true//What does this do? To check selected item
            previousMenuItem = it


           when(it.itemId)
           {  //To invoke the fragment of the dashboard
               R.id.dashbord ->{

                   Toast.makeText(this@MainActivity, "Clicked Dashboard", Toast.LENGTH_SHORT)
                       .show()
               }
               R.id.profile ->{
                   openDashboard()
                   supportFragmentManager
                       .beginTransaction().replace(
                           R.id.frame,
                           ProfileFragment()
                       )
                       .addToBackStack("Profile").commit()
                   dawble.closeDrawers()
                   supportActionBar?.title = "Profile"

                   Toast.makeText(this@MainActivity, "Clicked  Profile", Toast.LENGTH_SHORT)
                       .show()
               }
               R.id.aboutus ->{
                   supportFragmentManager
                       .beginTransaction().replace(
                           R.id.frame,
                           AboutappFragment()
                       )
                       .addToBackStack("AboutApp").commit()
                   dawble.closeDrawers()
                   supportActionBar?.title = "About App"

                   Toast.makeText(this@MainActivity, "Clicked About App", Toast.LENGTH_SHORT)
                       .show()
               }
               R.id.favourites ->{
                   supportFragmentManager
                       .beginTransaction().replace(
                           R.id.frame,
                           FavouritesFragment()
                       )
                       .addToBackStack("Favourites").commit()
                   dawble.closeDrawers()

                   supportActionBar?.title = "Favourites"
                   Toast.makeText(this@MainActivity, "Clicked Favourites", Toast.LENGTH_SHORT)
                       .show()
               }

           }
            return@setNavigationItemSelectedListener true
        }
    }
    fun openDashboard() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                DashbordFragment()
            )
            .addToBackStack("Dashboard").commit()
        supportActionBar?.title = "Dashboard"
        dawble.closeDrawers()
        navigation.setCheckedItem(R.id.dashbord)
    }

    fun setuptoolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar ?.title="TOOL BAR"
        supportActionBar ?.setHomeButtonEnabled(true)
        supportActionBar ?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id== android.R.id.home)
        {
            with(dawble) { openDrawer(GravityCompat.START) }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is DashbordFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frame,
                        DashbordFragment()
                    ).addToBackStack("Dashboard").commit()
                supportActionBar?.title = "Dashboard"
                dawble.closeDrawers()
                navigation.setCheckedItem(R.id.dashbord)
            }
            else -> finish()
        }
    }
}