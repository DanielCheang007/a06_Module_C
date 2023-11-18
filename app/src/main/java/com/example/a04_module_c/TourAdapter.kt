package com.example.a04_module_c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class TourAdapter(private val mTours: ArrayList<Tour>): RecyclerView.Adapter<TourAdapter.ViewHolder>() {
    private var sortAsc: Boolean = false

    // save a list copy
    private var oriTours: ArrayList<Tour> = ArrayList<Tour>()

    init {
        oriTours.clear()
        oriTours.addAll(mTours)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tourName)
        val dateTextView = itemView.findViewById<TextView>(R.id.tourDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val tourView = inflater.inflate(R.layout.item_tour, parent, false)
        return ViewHolder(tourView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tour: Tour = mTours.get(position)

        holder.nameTextView.setText(tour.activityName)

        val pattern = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(pattern)
        val dateStr = formatter.format(tour.activityDate)
        holder.dateTextView.setText(dateStr)

        holder.itemView.setOnClickListener {
            println("-- here: " + tour.activityName)

            val activity = it!!.context as AppCompatActivity
            val frag = TourDetailsFragment()

            val bundle = Bundle()
            bundle.putInt("activityId", tour.activityId)
            bundle.putString("isActive", tour.isActive)
            bundle.putString("activityName", tour.activityName)
            bundle.putLong("activityDate", tour.activityDate.time)
            bundle.putString("activityType", tour.activityType)
            bundle.putString("activityDescription", tour.activityDescription)
            frag.arguments = bundle

            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.rightLayout, frag)

            // allow back to original fragment
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return mTours.size
    }

    public fun search(kw: String) {
        mTours.clear()
        if (kw == "") {
            mTours.addAll(oriTours)
        } else {
            val res = oriTours.filter{ it -> it.activityName.contains(kw, ignoreCase = true)}
            mTours.addAll(res)
        }
        
        notifyDataSetChanged()
    }

    public fun sort(){
        mTours.sortWith(compareBy{ it.activityDate })
        if (sortAsc) {
            mTours.reverse()
            sortAsc = false
        } else {
            sortAsc = true
        }
        notifyDataSetChanged()
    }

    public fun setData(tours: ArrayList<Tour>) {
        println("-- set data")
        mTours.clear()
        mTours.addAll(tours)
        mTours.addAll(tours)
        mTours.addAll(tours)

        oriTours.clear()
        oriTours.addAll(mTours)

        println(mTours.size)
        println("-- notify the change")
        notifyDataSetChanged()
    }
}