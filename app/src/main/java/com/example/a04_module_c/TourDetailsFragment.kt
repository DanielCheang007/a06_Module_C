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
    private var isActive: String? = "0"
    private var tour: Tour ?= null

    private val agent = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tour_details_fragment, container, false)

        tour = arguments?.getSerializable("tour") as Tour

        if (tour?.isActive == "1") {
            isActive = "1"
        } else {
            isActive = "0"
        }

        view?.findViewById<TextView>(R.id.activityName)?.text = tour?.activityName

        view?.findViewById<TextView>(R.id.activityDate)?.text = tour?.dateStr()

        view?.findViewById<TextView>(R.id.activityType)?.text = tour?.activityType

        view?.findViewById<TextView>(R.id.activityDescription)?.text = tour?.activityDescription


        val btn = view.findViewById<Button>(R.id.toggleTour)
        btn.setOnClickListener {
            toggleTourActive(tour)
        }

        return view
    }

    private fun toggleTourActive(tour: Tour?) {
        if (tour == null) {
            return
        }

        var targetStatus = "1"
        if (isActive == "1") {
            targetStatus = "0"
        }
        val url = getString(R.string.api_base) + "/tour/" + tour.activityId + "?isActive=" + targetStatus

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

                    notifyActivity()
                }
            })
    }

    fun notifyActivity() {
        val at = activity as? MainActivity2
        at?.tourUpdated()
    }

}