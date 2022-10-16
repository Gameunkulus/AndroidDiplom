package com.example.cookingbook


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.cookingbook.fragments.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.view.*


class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener,
    NavigationView.OnNavigationItemSelectedListener {
    var categoriesFragment = CategoriesFragment()
    var searchRecipeFragment = SearchRecipeFragment()
    var shopingListFragment = ShopingListFragment()
    var updateDatabaseFragment = UpdateDatabaseFragment()
    var currentFragment: Fragment? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    var mDrawerLayout: DrawerLayout? = null
    var fragmentManager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar.toolbar)
        fragmentManager = supportFragmentManager
        fragmentManager!!.addOnBackStackChangedListener(this)
        initDrawer(toolbar)
        currentFragment = categoriesFragment
        setFragment(categoriesFragment, false)
    }

    fun setFragment(fragment: Fragment?, backEnabled: Boolean) {
        var fTrans: FragmentTransaction = fragmentManager!!.beginTransaction()
        fTrans = fTrans.replace(R.id.frame_layout, fragment!!, "currentFragment")
        if (backEnabled) {
            fTrans = fTrans.addToBackStack(null)
        } else {
            clearBackStack()
        }
        fTrans.commit()
    }

    private fun clearBackStack() {
        for (i in 0 until fragmentManager!!.getBackStackEntryCount()) {
            fragmentManager!!.popBackStack()
        }
    }

    private fun initDrawer(toolbar: Toolbar) {
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawerToggle = object : ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerStateChanged(newState: Int) {
                val inputMethodManager =
                    this@MainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    this@MainActivity.currentFocus!!.windowToken,
                    0
                )
            }
        }
        mDrawerLayout!!.setDrawerListener(drawerToggle)
        (drawerToggle as ActionBarDrawerToggle).syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_categories)
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_serach) {
            setFragment(searchRecipeFragment, false)
        } else if (id == R.id.nav_categories) {
            setFragment(categoriesFragment, false)
        } else if (id == R.id.nav_favorite) {
            val favRecipes = RecipesListFragment.newInstance("Любимые рецепты")
            setFragment(favRecipes, false)
        } else if (id == R.id.nav_shop_list) {
            setFragment(shopingListFragment, false)
        } else if (id == R.id.nav_update) {
            setFragment(updateDatabaseFragment, false)
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBackStackChanged() {
        //устанавливаем текущим верхий фрагмент из стека
        currentFragment =
            fragmentManager.getFragments().get(fragmentManager!!.getBackStackEntryCount())
        if (fragmentManager.getBackStackEntryCount() !== 0) {
            drawerToggle!!.isDrawerIndicatorEnabled = false
            val drawable = resources.getDrawable(R.drawable.ic_arrow_back)
            drawerToggle!!.setHomeAsUpIndicator(drawable)
            drawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }
        } else {
            drawerToggle!!.isDrawerIndicatorEnabled = true
            drawerToggle!!.toolbarNavigationClickListener = View.OnClickListener {
                if (mDrawerLayout!!.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout!!.closeDrawer(GravityCompat.START)
                } else {
                    mDrawerLayout!!.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "CookBook"
    }
}