package com.example.a04_module_c

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TourDetailsFragment: Fragment() {
    private var activityId: Int? = null
    private var isActive: String? = "0"
    private var activityName: String? = null
    private var activityDate: Date? = null
    private var activityType: String? = null
    private var activityDescription: String? = null

    private val agent = OkHttpClient()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tour_details_fragment, container, false)

        activityId = arguments?.getInt("activityId")
        isActive = arguments?.getString("isActive")

        activityName = arguments?.getString("activityName")
        view?.findViewById<TextView>(R.id.activityName)?.text = activityName

        val time = arguments?.getLong("activityDate")
        activityDate = time?.let { Date(it) }
        val pattern = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(pattern)
        val dateStr = formatter.format(activityDate)
        view?.findViewById<TextView>(R.id.activityDate)?.text = dateStr

        activityType = arguments?.getString("activityType")
        view?.findViewById<TextView>(R.id.activityType)?.text = activityType

        activityDescription = arguments?.getString("activityDescription")
        view?.findViewById<TextView>(R.id.activityDescription)?.text = activityDescription


        val btn = view.findViewById<Button>(R.id.toggleTour)
        btn.setOnClickListener {
            activityId?.let { it1 -> toggleTourActive(it1) }
        }


        return view
    }

    private fun toggleTourActive(activityId: Int) {
        var targetStatus = "1"
        if (isActive == "1") {
            targetStatus = "0"
        }
        val url = getString(R.string.api_base) + "/tour/" + activityId + "?isActive=" + targetStatus

        // the data that will post to server
        val requestBody = FormBody.Builder()
//            .add("isActive", "1")
            .build()

        // wrap the data to a request package
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        agent.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("-- something wrong")
                }

                override fun onResponse(call: Call, response: Response) {
                    println(response?.body?.string())
                    isActive = targetStatus
                }
            })
    }


}