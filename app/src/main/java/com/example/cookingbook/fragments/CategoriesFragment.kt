package com.example.cookingbook.fragments


import android.R.menu
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.MainActivity
import com.example.cookingbook.R
import com.example.cookingbook.adapters.CategoriesGridAdapter
import com.example.cookingbook.dao.DBCategoriesHelper
import com.example.cookingbook.pojo.Category



class CategoriesFragment : Fragment(), Category.CategoryClickListener {
    var recyclerView: RecyclerView? = null
    var layoutEmptyBase: View? = null
    var btnUpdate: Button? = null
    var categories: List<Category>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_categories, container, false)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        layoutEmptyBase = view.findViewById(R.id.layoutEmptyBase)
        btnUpdate = view.findViewById<View>(R.id.btnUpdate) as Button
        return view
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val dbCategoriesHelper = DBCategoriesHelper(getContext())
        categories = dbCategoriesHelper.all
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle("Категории")
        if (categories!!.size > 0) {
            initRecycleView()
        } else {
            showEmptyBase()
        }
    }

    private fun showEmptyBase() {
        recyclerView!!.visibility = View.GONE
        layoutEmptyBase!!.visibility = View.VISIBLE
        btnUpdate!!.setOnClickListener {
            val activity = getActivity() as MainActivity
            activity.setFragment(UpdateDatabaseFragment(), false)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.categories_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val activity = getActivity() as MainActivity
            activity.setFragment(SearchRecipeFragment(), false)
        }
        return true
    }

    private fun initRecycleView() {
        val columns: Int = getResources().getInteger(R.integer.category_columns)
        recyclerView!!.visibility = View.VISIBLE
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = GridLayoutManager(getContext(), columns)
        val adapter = getContext()?.let {
            CategoriesGridAdapter(
                it,
                categories!!, this
            )
        }
        recyclerView!!.adapter = adapter
    }

    override fun onClick(category: Category?) {
        val mainActivity = getActivity() as MainActivity
        val fragment: RecipesListFragment =
            RecipesListFragment.newInstance(category!!.name, category.id)
        mainActivity.setFragment(fragment, true)
    }
}