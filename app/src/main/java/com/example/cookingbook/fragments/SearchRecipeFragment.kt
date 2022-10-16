package com.example.cookingbook.fragments


import android.R
import android.R.id.*
import android.R.menu.*
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.ButtonRemoveClickListener
import com.example.cookingbook.MainActivity
import com.example.cookingbook.adapters.IngAutoCompleteAdapter
import com.example.cookingbook.adapters.IngredientListAdapter
import com.example.cookingbook.dao.DBCategoriesHelper
import com.example.cookingbook.dao.DBSearchHelper
import com.example.cookingbook.pojo.Category
import com.example.cookingbook.pojo.Ingredient
import com.google.android.material.snackbar.Snackbar


class SearchRecipeFragment : Fragment(), View.OnClickListener,
    ButtonRemoveClickListener {
    var adapter: IngredientListAdapter? = null
    var etIng: AutoCompleteTextView? = null
    var expandableLayout: ExpandableRelativeLayout? = null
    var imgChevron: ImageView? = null
    var rg1: RadioGroup? = null
    var rg2: RadioGroup? = null
    var tvSelectedIng: TextView? = null
    var etDish: EditText? = null
    var spCategories: Spinner? = null
    var categories: List<Category>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity()?.setTitle("Найти рецепт")
        setHasOptionsMenu(true)
        categories = DBCategoriesHelper(getContext()).all
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(layout.fragment_search_recipe, container, false)
        initControls(view)
        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.additionalParamsLayout || v.id == R.id.ivChevron) {
            toggleAdditionalParams()
        } else if (v.id == R.id.btnSearch) performSearch(expandableLayout.isExpanded())
    }

    private fun toggleAdditionalParams() {
        if (expandableLayout.isExpanded()) {
            expandableLayout.collapse()
            imgChevron!!.setImageResource(drawable.ic_expand_more)
        } else {
            expandableLayout.expand()
            imgChevron!!.setImageResource(drawable.ic_expand_less)
        }
    }

    private fun addIngredientToList(ing: Ingredient) {
        if (adapter!!.contains(ing)) {
            DBSearchHelper.Builder(getContext())
                .setMessage(ing.toString() + " уже есть в списке!")
                .setPositiveButton("Ок",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }).show()
        } else {
            adapter!!.add(ing)
        }
        etIng!!.setText("")
        tvSelectedIng!!.visibility = View.VISIBLE
    }

    private fun initControls(view: View) {

        //Инициализация автодополнения по ингредиентам
        etIng = view.findViewById<View>(R.id.etIngredient) as AutoCompleteTextView
        etIng!!.setAdapter(IngAutoCompleteAdapter(getContext()))
        etIng!!.threshold = 3
        etIng!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, id ->
                val i: Ingredient = adapterView.getItemAtPosition(position) as Ingredient
                etIng.setText(i.caption)
                addIngredientToList(i)
            }
        etDish = view.findViewById<View>(R.id.etDish) as EditText

        //Инициализация экспандера с доп. параметрами поиска
        view.findViewById<View>(R.id.additionalParamsLayout).setOnClickListener(this)
        expandableLayout =
            view.findViewById<View>(R.id.expandableLayout) as ExpandableRelativeLayout
        expandableLayout.collapse()
        imgChevron = view.findViewById<View>(R.id.ivChevron) as ImageView
        imgChevron!!.setOnClickListener(this)
        tvSelectedIng = view.findViewById<View>(R.id.tvSelectedIng) as TextView
        adapter = IngredientListAdapter(ArrayList<Ingredient>(), this)
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.adapter = adapter
        val btnSearch = view.findViewById<View>(R.id.btnSearch) as Button
        btnSearch.setOnClickListener(this)
        spCategories = view.findViewById<View>(R.id.spinnerCategories) as Spinner
        val categoriesCaptions = ArrayList<String?>()
        categoriesCaptions.add("Все категории")
        for (c in categories) {
            categoriesCaptions.add(c.name)
        }
        val spinnerArrayAdapter: ArrayAdapter<String?> =
            ArrayAdapter<Any?>(getContext(), layout.simple_spinner_item, categoriesCaptions)
        spinnerArrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        spCategories!!.adapter = spinnerArrayAdapter
        initRadioButtons(view)
    }

    private fun performSearch(withAdditionalParams: Boolean) {
        val imm = getActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0)
        val ingredients: List<Ingredient> = adapter!!.getIngredients()
        val dish = etDish!!.text.toString().trim { it <= ' ' }
        if (dish.isEmpty() && ingredients.isEmpty()) {
            showErrorDialog()
            return
        }
        val builder = getBuilder(withAdditionalParams, dish, ingredients)
        val (exactlyFound, recipesIds) = builder.search()
        if (recipesIds.size != 0) {
            if (!exactlyFound) Snackbar.make(
                getView(),
                "Отображаются рецепты, содержащие указанные ингредиенты",
                Snackbar.LENGTH_SHORT
            ).show()
            val recList: RecipesListFragment =
                RecipesListFragment.newInstance("Результаты поиска", recipesIds)
            val activity = getActivity() as MainActivity
            activity.setFragment(recList, true)
        } else if (withAdditionalParams) { // рецепты найти не удалсоь, но поиск производился с учетом доп. параметров. Предлагаем повторить поиск без их учета
            DBSearchHelper.Builder(getContext())
                .setTitle("Таких рецептов не нашлось")
                .setMessage("Не удалось найти ни одного рецепта, подходящего под заданные критерии поиска.\nПовторить поиск без учета дополнительных параметров?")
                .setPositiveButton("Повторить",
                    DialogInterface.OnClickListener { dialog, which ->
                        expandableLayout.collapse()
                        performSearch(false)
                    })
                .setNegativeButton("Отмена", null)
                .show()
        } else { // рецепты не найдены. Выводим диалог
            DBSearchHelper.Builder(getContext())
                .setTitle("Таких рецептов не нашлось")
                .setMessage("Не удалось найти ни одного рецепта с таким названием.\nУкажите другое название блюда, либо заполните ингредиенты")
                .setPositiveButton("Ок", null)
                .show()
        }
    }

    private fun getBuilder(
        withAdditionalParams: Boolean,
        caption: String,
        ingredients: List<Ingredient>
    ): DBSearchHelper.Builder {
        val builder = DBSearchHelper.Builder(getContext())
        if (!ingredients.isEmpty()) {
            builder.withIngredients(ingredients)
        }
        if (!caption.isEmpty()) {
            builder.withCaption(caption)
        }
        if (withAdditionalParams && expandableLayout.isExpanded()) { // учитывать доп. параметры
            val selectedIndex = spCategories!!.selectedItemPosition
            if (selectedIndex != 0) {
                val category: Category = categories!![selectedIndex - 1]
                builder.inCategory(category.id)
            }
            val rId = checkedRadioButtonId
            if (rId == R.id.rbLight) builder.withCookingTime(15) else if (rId == R.id.rbMedium) builder.withCookingTime(
                30
            ) else if (rId == R.id.rbNourishing) builder.withCookingTime(45)
        }
        return builder
    }

    private fun showErrorDialog() {
        DBSearchHelper.Builder(getActivity())
            .setTitle("Невозможно начать поиск")
            .setMessage("Введите название блюда, которое хотите найти, либо выбирите ингредиенты, по которым хотите подобрать блюдо")
            .setPositiveButton("Ok", null)
            .show()
    }

    override fun onClick(position: Int) {
        adapter!!.remove(position)
        if (adapter!!.itemCount == 0) {
            tvSelectedIng!!.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.empty_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //region Radio buttons
    private val listener1 =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                rg2!!.setOnCheckedChangeListener(null)
                rg2!!.clearCheck()
                rg2!!.setOnCheckedChangeListener(listener2)
            }
        }
    private val listener2 =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                rg1!!.setOnCheckedChangeListener(null)
                rg1!!.clearCheck()
                rg1!!.setOnCheckedChangeListener(listener1)
            }
        }

    private fun initRadioButtons(view: View) {
        rg1 = view.findViewById<View>(R.id.rg1) as RadioGroup
        rg2 = view.findViewById<View>(R.id.rg2) as RadioGroup
        rg1!!.clearCheck()
        rg2!!.clearCheck()
        rg1!!.setOnCheckedChangeListener(listener1)
        rg2!!.setOnCheckedChangeListener(listener2)
        rg2!!.check(R.id.rbAny)
    }

    //endregion
    private val checkedRadioButtonId: Int
        private get() = if (rg1!!.checkedRadioButtonId != -1) rg1!!.checkedRadioButtonId else rg2!!.checkedRadioButtonId

    companion object {
        private val LOG_TAG = SearchRecipeFragment::class.java.name
    }
}