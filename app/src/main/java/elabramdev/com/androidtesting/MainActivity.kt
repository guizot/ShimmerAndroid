package elabramdev.com.androidtesting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONArray
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.Response
import android.view.View


class MainActivity : AppCompatActivity() {

    private var cartList: ArrayList<Recipe>? = null
    private var mAdapter: RecipeListAdapter? = null
    private val URL = "https://api.androidhive.info/json/shimmer/menu.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cartList = ArrayList()
        mAdapter = RecipeListAdapter(this, cartList)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL), 0)
        recyclerView.adapter = mAdapter

        // making http call and fetching menu json
        fetchRecipes()

    }

    private fun fetchRecipes() {
        val request = JsonArrayRequest(URL,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {
                    if (response == null) {
                        Toast.makeText(
                            applicationContext,
                            "Couldn't fetch the menu! Pleas try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    val recipes =
                        Gson().fromJson<List<Recipe>>(response.toString(), object : TypeToken<List<Recipe>>() {

                        }.type)

                    // adding recipes to cart list
                    cartList?.clear()
                    cartList?.addAll(recipes)

                    // refreshing recycler view
                    mAdapter?.notifyDataSetChanged()

                    // stop animating Shimmer and hide the layout
                    shimmer_view_container.stopShimmerAnimation()
                    shimmer_view_container.setVisibility(View.GONE)
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    // error in getting json
                    Log.e(MainActivity.TAG, "Error: " + error.message)
                    Toast.makeText(applicationContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
                }
            })

        MyApplication.getInstance().addToRequestQueue(request)
    }

    override fun onResume() {
        super.onResume()
        shimmer_view_container.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        shimmer_view_container.stopShimmerAnimation()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
