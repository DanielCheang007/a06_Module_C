package com.example.a04_module_c

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable

class MainActivity2 : AppCompatActivity() {
    private val agent = OkHttpClient()
    private val gson = Gson()

    private lateinit var leftFrag: LeftFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // 接收 MainActivity 傳來的 user instance
        val user = intent?.getSerializableExtra("user") as? User
        println(user)

        val transaction = supportFragmentManager.beginTransaction()

        leftFrag = LeftFragment()
        val bundle = Bundle()
        bundle.putSerializable("user", user as Serializable)
        leftFrag?.arguments = bundle
        transaction.replace(R.id.leftLayout, leftFrag)

        transaction.replace(R.id.rightLayout, RightFragment())
        transaction.commit()
    }

    fun tourUpdated() {
        println("-- say hi from main activity2")

        leftFrag.reloadTourList()
    }


}