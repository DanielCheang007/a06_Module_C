package com.example.a04_module_c

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LeftFragment: Fragment() {
    private val agent = OkHttpClient()
    private val gson = Gson()

    lateinit var adapter: TourAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.left_fragment, container, false)

        val user:User = arguments?.getSerializable("user") as User
        println("---- here")
        println(user)

        val username = view?.findViewById<TextView>(R.id.username)
        username?.setText(user?.username)

        // search tour list
        val searchFiled = view?.findViewById<EditText>(R.id.searchQuery)
        searchFiled?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                println("-- beforeTextChanged " + p0)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("-- onTextChanged " + p0)
                adapter.search(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
//                println("-- afterTextChanged " + p0)
            }
        })


        // sort the tour list
        val btn = view?.findViewById<Button>(R.id.sortList)
        btn?.setOnClickListener {
            adapter.sort()
        }


        // init the tour list
        val tourListView = view?.findViewById<View>(R.id.tourListRecyclerView) as RecyclerView

        adapter = TourAdapter(ArrayList<Tour>())

        tourListView.adapter = adapter
        tourListView.layoutManager = LinearLayoutManager(activity)
        getTours()

        return view
    }

    private fun getTours() {
        val url = getString(R.string.api_base) + "/tour"

        // the data that will post to server
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()

        agent.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("-- something wrong")
                }

                override fun onResponse(call: Call, response: Response) {
                    println(response)
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData: String = response.body?.string().toString()
                    val resObj = JSONObject(jsonData)
                    val tourArr = resObj.getJSONArray("tours")

                    val sType = object: TypeToken<ArrayList<Tour>>(){}.type
                    val tourList = gson.fromJson<ArrayList<Tour>>(tourArr.toString(), sType)

                    activity?.runOnUiThread{
                        println("-- run on ui thread")
                        adapter.setData(tourList)
                    }
                }
            })
    }
}