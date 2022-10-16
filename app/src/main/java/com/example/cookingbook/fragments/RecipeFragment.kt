package com.example.cookingbook.fragments


import android.R
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.adapters.RecipeIngredientAdapter
import com.example.cookingbook.dao.DBIngredientsHelper
import com.example.cookingbook.dao.DBRecipesHelper
import com.example.cookingbook.dao.DBShopListHelper
import com.example.cookingbook.helper.FavoritesHelper
import com.example.cookingbook.pojo.Ingredient
import com.example.cookingbook.pojo.Recipe
import com.google.android.material.snackbar.Snackbar


class RecipeFragment : Fragment(), Ingredient.IngredientClickListener {
    private var isFavorite = false
    private var recipe: Recipe? = null
    private var pairs: List<Pair<Ingredient, String>>? = null
    private var favoritesHelper: FavoritesHelper? = null
    private var dbShopListHelper: DBShopListHelper? = null
    var adapter: RecipeIngredientAdapter? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            val recId: Long = getArguments().getLong(ARG_REC_ID)
            val dbRecipesHelper = DBRecipesHelper(getContext())
            val dbIngredientsHelper = DBIngredientsHelper(getContext())
            dbShopListHelper = DBShopListHelper(getContext())
            recipe = dbRecipesHelper.getById(recId) // получаем рецепт из базы
            pairs =
                dbIngredientsHelper.getByRecipeId(recId) // получаем список его ингредиентов и их количества
            favoritesHelper = FavoritesHelper(getContext())
            isFavorite = favoritesHelper!!.isFavorite(recipe!!.id)
            getActivity()!!.setTitle(recipe!!.name)
        } else {
            Log.e(LOG_TAG, "Создан фрагмент RecipeFragment без аргументов!")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_recipe, container, false)
        val ivIcon = view.findViewById<View>(R.id.ivIcon) as ImageView
        val tvCaption = view.findViewById<View>(R.id.tvCaption) as TextView
        val tvCookingTime = view.findViewById<View>(R.id.tvCookingTime) as TextView
        val tvInstruction = view.findViewById<View>(R.id.tvInstaction) as TextView
        if (recipe!!.icon != null) ivIcon.setImageBitmap(recipe!!.icon)
        tvCaption.setText(recipe!!.name)
        tvCookingTime.setText(recipe!!.cookingTime.toString() + " мин")
        if (recipe!!.instruction != null) tvInstruction.setText(recipe!!.instruction.trim())
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.recipe_menu, menu)
        if (isFavorite) {
            menu.findItem(R.id.menu_include).isVisible = false
        } else menu.findItem(R.id.menu_exculde).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_include || id == R.id.menu_exculde) {
            if (isFavorite) favoritesHelper.removeFromFavorites(recipe.id) else favoritesHelper?.addToFavorite(
                recipe.id
            )
            isFavorite = !isFavorite
            getActivity()?.invalidateOptionsMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        initRecycleView()
    }

    private fun initRecycleView() {
        val recycler = getView()?.findViewById(R.id.recyclerView) as RecyclerView
        recycler.setHasFixedSize(true)
        adapter = RecipeIngredientAdapter(getContext(), pairs!!, this)
        recycler.adapter = adapter
    }

    override fun onClick(i: Ingredient?) {
        val ing: String = i!!.caption.toString()
        val removeIngListener = View.OnClickListener {
            dbShopListHelper!!.remove(ing)
            adapter!!.removeIng(ing)
        }
        if (adapter!!.isInList(ing)) {
            Snackbar.make(
                getActivity().findViewById(R.id.root_layout),
                String.format("%s уже есть в списке покупок", ing),
                Snackbar.LENGTH_SHORT
            )
                .setAction("Удалить", removeIngListener)
                .show()
        } else {
            adapter!!.addInList(ing)
            dbShopListHelper!!.add(ing)
            Snackbar.make(
                getActivity()!!.findViewById(R.id.root_layout),
                String.format("%s добавлен в список покупок", ing),
                Snackbar.LENGTH_SHORT
            )
                .setAction("Отмена", removeIngListener)
                .show()
        }
    }

    companion object {
        private const val ARG_REC_ID = "recipe_id"
        private const val LOG_TAG = "CookBook"
        fun newInstance(recId: Long): RecipeFragment {
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putLong(ARG_REC_ID, recId)
            fragment.setArguments(args)
            return fragment
        }
    }
}