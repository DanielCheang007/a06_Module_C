package com.example.a04_module_c

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.Serializable

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // 接收 MainActivity 傳來的 user instance
        val user = intent?.getSerializableExtra("user") as? User
        println(user)

        val transaction = supportFragmentManager.beginTransaction()

        val leftFrag = LeftFragment()
        val bundle = Bundle()
        bundle.putSerializable("user", user as Serializable)
        leftFrag?.arguments = bundle
        transaction.replace(R.id.leftLayout, leftFrag)

        transaction.replace(R.id.rightLayout, RightFragment())
        transaction.commit()
    }
}