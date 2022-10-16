package com.example.cookingbook.fragments


import android.R
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.ButtonRemoveClickListener
import com.example.cookingbook.adapters.IngAutoCompleteAdapter
import com.example.cookingbook.adapters.ShopListAdapter
import com.example.cookingbook.dao.DBSearchHelper
import com.example.cookingbook.dao.DBShopListHelper
import com.example.cookingbook.pojo.Ingredient


class ShopingListFragment : Fragment(), View.OnClickListener,
    ButtonRemoveClickListener {
    var recyclerView: RecyclerView? = null
    var btnAdd: Button? = null
    var etIng: AutoCompleteTextView? = null
    var adapter: ShopListAdapter? = null
    var dbShopListHelper: DBShopListHelper? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity().setTitle("Список покупок")
        setHasOptionsMenu(true)
        dbShopListHelper = DBShopListHelper(getContext())
    }

    override fun onStart() {
        super.onStart()
        recyclerView = getView().findViewById(R.id.recyclerView)
        btnAdd = getView().findViewById(R.id.btnAdd)

        //Инициализация автодополнения по списку ингредиентов
        etIng = getView().findViewById(R.id.etIngredient)
        etIng!!.setAdapter(IngAutoCompleteAdapter(getContext()))
        etIng!!.threshold = 3
        etIng!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, id ->
                val i: Ingredient = adapterView.getItemAtPosition(position) as Ingredient
                etIng!!.setText(i.caption)
                addToList()
            }
        val lines = dbShopListHelper!!.all
        adapter = ShopListAdapter(lines, this)
        recyclerView!!.adapter = adapter
        btnAdd!!.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.empty_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_shoping_list, container, false)
    }

    override fun onClick(v: View) {
        addToList()
    }

    private fun addToList() {
        val ing = etIng!!.text.toString()
        if (ing.trim { it <= ' ' } != "") {
            if (adapter!!.contains(ing)) {
                DBSearchHelper.Builder(getContext())
                    .setMessage("$ing уже есть в списке!")
                    .setPositiveButton("Ок",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    .show()
            } else {
                adapter!!.add(ing)
                dbShopListHelper!!.add(ing)
            }
        }
        etIng!!.setText("")
    }

    override fun onClick(position: Int) {
        dbShopListHelper!!.remove(adapter!![position])
        adapter!!.remove(position)
    }
}