package com.example.a04_module_c

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private val agent = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameField = findViewById<TextView>(R.id.editTextTextPersonName)
        val passwordField = findViewById<TextView>(R.id.editTextTextPassword)
        val loginBtn = findViewById<Button>(R.id.button)

        loginBtn.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            asyncLogin(username, password)
        }
    }

    private fun asyncLogin(username: String, password: String) {
        val url = getString(R.string.api_base) + "/login"

        // the data that will post to server
        val requestBody = FormBody.Builder()
            .add("name", username)
            .add("pwd", password)
            .build()

        // wrap the data to a request package
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        agent.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    println("-- something wrong")
                    Toast.makeText(this@MainActivity, "login failed", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    println(response)
                    if (response.isSuccessful) {
                        // 將 response 的 body 解析出來
                        val jsonData: String = response.body?.string().toString()
                        val resObj = JSONObject(jsonData)
                        val validated = resObj.getBoolean("validated")

                        // 查看解析結果中的 validated 是 true / false， false 表示賬號密碼錯誤
                        if (validated) {
                            // 將 json 裡的 user 物件，轉為 kotlin 裡 User 的 instance
                            val userData = resObj.getJSONObject("user")
                            val user = Gson().fromJson(userData.toString(), User::class.java)

                            val intent = Intent(this@MainActivity, MainActivity2::class.java)
                            intent.putExtra("user", user as Serializable) // 將 user 傳給 工作頁 activity
                            startActivity(intent)
                        } else {
                            // onResponse 裡不能直接使用 Toast，必須使用此方法調用 UI Thread 來執行
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "name or password incorrect",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        println("連不上 server 才會出現在這裡，這時候就要檢查 server 返回的 response status 是否 200 OK， 或者連不上 api server")
                    }
                }
            })
    }
}