package com.example.a04_module_c

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.Date

class LeftFragment: Fragment() {
    private val agent = OkHttpClient()
    private val gson = Gson()

    lateinit var adapter: TourAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.left_fragment, container, false)

        val user: User? = arguments?.getSerializable("user", User::class.java)
        println("---- here")
        println(user)

        val username = view?.findViewById<TextView>(R.id.username)
        username?.setText(user?.username)


        val addTourBtn = view?.findViewById<Button>(R.id.addTour)
        addTourBtn?.setOnClickListener {
//            createTour()
            popUp()
        }

        val refreshBtn = view?.findViewById<Button>(R.id.refresh)
        refreshBtn?.setOnClickListener {
            fetchAllTours()
        }

        if (user?.thumbnail != null) {
            val avatarImg = view?.findViewById<ImageView>(R.id.avatarImage)
            avatarImg?.load(getString(R.string.api_base) + "/" + user?.thumbnail)
        }

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
//        tourListView.layoutManager = LinearLayoutManager(activity)
        tourListView.layoutManager = GridLayoutManager(activity, 2)

        fetchAllTours()

        return view
    }

    private fun popUp() {
        val builder = AlertDialog.Builder(this.activity)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog,null)

        val dismissBtn = view.findViewById<Button>(R.id.dismissBtn)

        dismissBtn.setOnClickListener {
            builder.dismiss()
        }

        var activityNameView = view.findViewById<EditText>(R.id.editActivityName)

        val createTourBtn = view.findViewById<Button>(R.id.createTour)
        createTourBtn.setOnClickListener {
            createTour(activityNameView.text.toString())
            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun createTour(name: String = "test" + Date().time) {
        val url = getString(R.string.api_base) + "/tour"

        // the data that will post to server
        val requestBody = FormBody.Builder()
            .add("activityName", name)
            .add("activityDate", Date().toString())
            .add("activityType", "户外活动")
            .add("activityDescription", "blalbalblalba.....")
            .add("maxParticipant", "30")
            .build()

        // wrap the data to a request package
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        agent.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                println(response?.body.toString())

                reloadTourList()
            }

        })
    }

    private fun fetchAllTours() {
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

    fun reloadTourList() {
        println("-- say Hi from left fragment")

        fetchAllTours()
    }
}