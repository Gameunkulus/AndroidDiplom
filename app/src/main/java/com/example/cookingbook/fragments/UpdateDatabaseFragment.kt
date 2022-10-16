package com.example.cookingbook.fragments


import android.R.*
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.example.cookingbook.MainActivity
import com.example.cookingbook.R
import com.example.cookingbook.dao.DBCategoriesHelper
import com.example.cookingbook.dao.DBIngredientsHelper
import com.example.cookingbook.dao.DBRecipesHelper
import com.example.cookingbook.dao.DBSearchHelper
import com.example.cookingbook.helper.UpdatingHelper
import com.example.cookingbook.pojo.Recipe
import com.example.cookingbook.rest.DeltaResponse
import com.example.cookingbook.rest.IRestApi
import com.example.cookingbook.rest.RestClient
import com.example.cookingbook.rest.UpdateResponse
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class UpdateDatabaseFragment : Fragment(), View.OnClickListener {
    var btnUpdate: Button? = null
    var tvUpdatingStatus: TextView? = null
    var tvLastUpdate: TextView? = null
    var tvRecipesInBase: TextView? = null
    var layoutMain: View? = null
    var layoutUpdating: View? = null
    var updatingHelper: UpdatingHelper? = null
    var df = SimpleDateFormat("dd MMMM yyyy")
    var client: IRestApi? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updatingHelper = UpdatingHelper(getContext())
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_update_db, container, false)
        btnUpdate = view.findViewById<View>(R.id.btnUpdate) as Button
        tvUpdatingStatus = view.findViewById<View>(R.id.tvUpdateStatus) as TextView
        tvRecipesInBase = view.findViewById<View>(R.id.tvRecipesInBase) as TextView
        tvLastUpdate = view.findViewById<View>(R.id.tvLastUpdateTime) as TextView
        layoutMain = view.findViewById(R.id.layoutInfo)
        layoutUpdating = view.findViewById(R.id.layoutUpdateProgress)
        val lastUpdatingTime: Long = updatingHelper.getLastUpdatingTime()
        if (lastUpdatingTime == 0L) {
            tvLastUpdate!!.text = "не производилось"
        } else {
            tvLastUpdate!!.text = df.format(lastUpdatingTime)
        }
        val recipesCount = DBRecipesHelper(getContext()).count
        tvRecipesInBase!!.text = java.lang.Long.toString(recipesCount)
        btnUpdate!!.setOnClickListener(this)
        return view
    }

    override fun onStart() {
        super.onStart()
        getActivity()?.setTitle("Обновление данных")
        client = RestClient.getClient(getContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.empty_menu, menu)
    }

    override fun onClick(v: View) {
        tvUpdatingStatus!!.text = "Запрос обновлений..."
        swithVisibility(true)
        client.getDelta(updatingHelper.getLastUpdatingTime() / 1000)
            .enqueue(object : Callback<DeltaResponse?>() {
                override fun onResponse(call: Call<DeltaResponse?>?, response: Response<DeltaResponse?>) {
                    swithVisibility(false)
                    if (response.code() !== 200) {
                        showErrorDialog("Сервер временно недоступен. Повторите попытку позже")
                        return
                    }
                    showCheckDialog(response.body().delta)
                }

                override fun onFailure(call: Call<DeltaResponse?>?, t: Throwable?) {
                    swithVisibility(false)
                    showErrorDialog("Проверьте подключение к сети и повторите попытку позже")
                }
            })
    }

    private fun showCheckDialog(delta: Double) {
        val eps = 0.01
        if (delta > eps) {
            Builder(getActivity())
                .setTitle(R.string.text_check_dialog_caption)
                .setMessage(
                    java.lang.String.format(
                        getString(R.string.text_confirm_download),
                        delta
                    )
                )
                .setPositiveButton("Загрузить",
                    DialogInterface.OnClickListener { dialog, which -> downloadContent() })
                .setNegativeButton("Отмена", null)
                .show()
        } else {
            Builder(getActivity())
                .setTitle(R.string.text_check_dialog_caption)
                .setMessage(R.string.text_download_not_need)
                .setPositiveButton("Ок", null)
                .show()
        }
    }

    private fun downloadContent() {
        tvUpdatingStatus!!.text = "Загрузка данных"
        swithVisibility(true)
        client.update(updatingHelper.getLastUpdatingTime() / 1000)
            .enqueue(object : Callback<UpdateResponse?>() {
                fun onResponse(call: Call<UpdateResponse?>?, response: Response<UpdateResponse?>) {
                    try {
                        if (response.code() !== 200) {
                            Log.e(
                                LOG_TAG,
                                java.lang.String.format(
                                    "Server error! Code: %d, Message: %s",
                                    response.code(),
                                    response.message()
                                )
                            )
                            swithVisibility(false)
                            return
                        }
                        val content: UpdateResponse = response.body()
                        Log.d(
                            LOG_TAG,
                            java.lang.String.format(
                                "Получение данных успешно завершено! Получено категорий: %d, рецептов: %d, ингредиентов: %d",
                                content.categories.size(),
                                content.recipes.size(),
                                content.ingredients.size()
                            )
                        )
                        tvUpdatingStatus!!.text = "Обновление базы"
                        val onResult: UpdateDatabaseTask.TaskResult =
                            object : UpdateDatabaseTask.TaskResult {
                                override fun onComplete(success: Boolean) {
                                    swithVisibility(false)
                                    if (success) {
                                        val activity = getActivity() as MainActivity
                                        activity.setFragment(CategoriesFragment(), false)
                                    }
                                }
                            }
                        val task = UpdateDatabaseTask(getContext(), onResult, content)
                        task.execute()
                    } catch (ex: Exception) {
                        Log.e(LOG_TAG, "Ошибка при обновлении данных:", ex)
                    }
                }

                fun onFailure(call: Call<UpdateResponse?>?, t: Throwable?) {
                    Log.e(LOG_TAG, "Не удалось получить данные", t)
                    swithVisibility(false)
                }
            })
    }

    private fun showErrorDialog(message: String) {
        DBSearchHelper.Builder(getActivity())
            .setTitle("Не удалось получить данные")
            .setMessage(message)
            .setPositiveButton("Ок", null)
            .show()
    }

    private fun swithVisibility(updating: Boolean) {
        if (updating) {
            layoutMain!!.visibility = View.GONE
            layoutUpdating!!.visibility = View.VISIBLE
        } else {
            layoutMain!!.visibility = View.VISIBLE
            layoutUpdating!!.visibility = View.GONE
        }
    }

    private class UpdateDatabaseTask(
        private val context: Context,
        private val taskResult: TaskResult,
        content: UpdateResponse
    ) :
        AsyncTask<Void?, Void?, Boolean>() {
        internal interface TaskResult {
            fun onComplete(result: Boolean?)
        }

        private val content: UpdateResponse
        protected override fun doInBackground(vararg params: Void?): Boolean {
            Log.d(LOG_TAG, "Сохранение данных")
            return try {
                val updatingHelper = UpdatingHelper(context)
                updatingHelper.setLastUpdatingTime(content.newUpdated * 1000)
                DBCategoriesHelper(context).addOrUpdate(content.categories)
                val dbIngredientsHelper = DBIngredientsHelper(context)
                dbIngredientsHelper.addOrReplace(content.ingredients)
                dbIngredientsHelper.addOrReplacePairs(content.recIng)
                DBRecipesHelper(context).addOrUpdate(content.recipes)
                updateCategoriesIcon()
                Log.d(LOG_TAG, "Сохранение завершено!")
                true
            } catch (ex: Exception) {
                Log.e(ContentValues.TAG, "doInBackground: ", ex)
                false
            }
        }

        private fun updateCategoriesIcon() {
            val dbCategoriesHelper = DBCategoriesHelper(context)
            val dbRecipesHelper = DBRecipesHelper(context)
            for (c in dbCategoriesHelper.all) {
                val recipes: List<Recipe>? = dbRecipesHelper.getByCategory(c.id)
                if (recipes != null && recipes.size != 0) {
                    c.icon = recipes[0].icon
                    dbCategoriesHelper.addOrUpdate(c)
                }
            }
        }

        override fun onPostExecute(success: Boolean) {
            taskResult.onComplete(success)
        }

        init {
            this.content = content
        }
    }

    companion object {
        private val LOG_TAG = UpdateDatabaseFragment::class.java.simpleName
    }
}