package com.example.cookingbook.fragments


import android.R
import android.R.layout.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.MainActivity
import com.example.cookingbook.adapters.RecipeListAdapter
import com.example.cookingbook.dao.DBRecipesHelper
import com.example.cookingbook.helper.FavoritesHelper
import com.example.cookingbook.helper.SearchHelper
import com.example.cookingbook.pojo.Recipe
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.primitives.Longs


class RecipesListFragment : Fragment(), Recipe.RecipeClickListener {
    var recipes: List<Recipe>? = null
    var recyclerView: RecyclerView? = null
    var searchView: SearchView? = null
    var screenName: String? = null
    var favs: FavoritesHelper? = null
    var showFavs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRecipes()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recipes_list, container, false)
    }

    private fun loadRecipes() {
        if (getArguments() != null) {
            screenName = getArguments().getString(ARG_SCREEN_NAME)
            val dbRecipesHelper = DBRecipesHelper(getContext())
            val catId: Long = getArguments().getLong(ARG_CATEGORY_ID, -1)
            showFavs = getArguments().getBoolean(ARG_SHOW_FAVORITES, false)
            if (catId != -1L) {
                recipes = dbRecipesHelper.getByCategory(catId)
            } else if (!showFavs) {
                val recIds: List<Long?> = Longs.asList(getArguments().getLongArray(ARG_RECIPES_ID))
                recipes = dbRecipesHelper.getById(recIds)
            }
        } else {
            Log.e(LOG_TAG, "Создан фрагмент RecipesList без аргументов!")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.recipes_list_menu, menu)
        try {
            val searchItem = menu.findItem(R.id.action_search)
            searchView = SearchHelper.getSearchView(getActivity(), searchItem)
            if (searchView != null) {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener() {
                    fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    fun onQueryTextChange(s: String?): Boolean {
                        val filterData: List<Recipe> = SearchHelper.filterData(
                            recipes, s
                        )
                        val adapter = RecipeListAdapter(
                            this@RecipesListFragment.getContext(),
                            filterData,
                            this@RecipesListFragment
                        )
                        recyclerView!!.adapter = adapter
                        return true
                    }
                })
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Не удалось произвести поиск", ex)
        }
    }

    override fun onStart() {
        super.onStart()
        getActivity().setTitle(screenName)
        favs = FavoritesHelper(getContext())
        if (showFavs) {
            val ids: List<Long?> = favs.getFavoriteRecipeIds()
            recipes = DBRecipesHelper(getContext()).getById(ids)
        }
        initRecycleView()
    }

    override fun onPause() {
        super.onPause()
        if (searchView != null) searchView!!.clearFocus() // закрываем поиск и прячем клавиатуру
    }

    private fun initRecycleView() {
        recyclerView = getView().findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        val adapter = RecipeListAdapter(getContext(), recipes!!, this)
        recyclerView!!.adapter = adapter
        val tvEmptyList: View = getView().findViewById(R.id.tvEmptyList)
        if (showFavs && (recipes == null || recipes!!.size == 0)) {
            tvEmptyList.visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
        } else {
            tvEmptyList.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(recipe: Recipe) {
        val fragment: RecipeFragment = RecipeFragment.newInstance(recipe.id)
        val mainActivity = getActivity() as MainActivity
        mainActivity.setFragment(fragment, true)
    }

    companion object {
        private const val ARG_SCREEN_NAME = "screenName"
        private const val ARG_RECIPES_ID = "recs_id"
        private const val ARG_CATEGORY_ID = "cat_id"
        private const val ARG_SHOW_FAVORITES = "show_favs"
        private const val LOG_TAG = "CookBook"

        /**
         * Создать фрагмент, отображающий рецепты определенной категории
         *
         * @param screenCapture заголовок экрана
         * @param catId         id категории, рецепты которой будут отображены
         */
        fun newInstance(screenCapture: String?, catId: Long): RecipesListFragment {
            val fragment = RecipesListFragment()
            val args = Bundle()
            args.putLong(ARG_CATEGORY_ID, catId)
            args.putString(ARG_SCREEN_NAME, screenCapture)
            fragment.setArguments(args)
            return fragment
        }

        /**
         * Создать фрагмент, отображающий избранные рецепты
         *
         * @param screenCapture заголовок экрана
         */
        fun newInstance(screenCapture: String?): RecipesListFragment {
            val fragment = RecipesListFragment()
            val args = Bundle()
            args.putString(ARG_SCREEN_NAME, screenCapture)
            args.putBoolean(ARG_SHOW_FAVORITES, true)
            fragment.setArguments(args)
            return fragment
        }

        /**
         * Создать фрагмент, отображающий набор произвольныех рецептов
         *
         * @param screenCapture заголовок экрана
         * @param recIds        id рецептов, которые необходимо отобразить
         */
        fun newInstance(screenCapture: String?, recIds: List<Long?>?): RecipesListFragment {
            val fragment = RecipesListFragment()
            val args = Bundle()
            args.putString(ARG_SCREEN_NAME, screenCapture)
            args.putLongArray(ARG_RECIPES_ID, Longs.toArray(recIds))
            fragment.setArguments(args)
            return fragment
        }
    }
}