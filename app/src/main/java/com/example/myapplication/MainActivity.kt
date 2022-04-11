package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch


class MainActivity : AppCompatActivity(){
    var usersDBHelper = UsersDBHelper(this)
    var client = OkHttpClient();
    var ALL_USERS = ArrayList<DtoUrlData>();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnAddUser_Click(v:View) {
        this.addUser()
    }
    fun btnDeleteUser_Click(v:View) {
        this.deleteUser()
    }
    fun doApiCall_Click(v:View) {
        this.doApiCall()
    }
    fun btnDropDB_Click(v:View){
        this.dropDB()
    }
    fun btnShowAllUsersFromDB_Click(v:View){
        this.showAllUsersFromDB()
    }

    fun fetchUsersFromApi(){
        val url  ="https://reqres.in/api/users?page=2"
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        textViewResult.setText("Fetching data from "+ url)
        val request = Request.Builder().url(url).build()
        val call =  client.newCall(request).enqueue(responseCallback = object : Callback {
            var mainHandler = Handler(this@MainActivity.mainLooper)
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(call: Call, response: Response) {
                mainHandler.post {

                    val postBody = response.body?.string()
                    if (postBody == null) return@post
                    val gson = Gson()
                    var urlInformationObject = gson.fromJson(postBody, DtoUrlInfo::class.java)
                    runOnUiThread(Runnable { // (1)
                        val gson = Gson()
                        var urlInformationObject = gson.fromJson(postBody, DtoUrlInfo::class.java)
                        ALL_USERS = urlInformationObject.data
                        textViewResult.setText("Success")
                        this@MainActivity.SaveUsersToDB()
                    })
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("API execute failed")
            }
        })
    }

    fun RefreshUserInterface(msg:String){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)
        linearLayoutView.removeAllViews()
        this.showAllUsersFromDB()
        textViewResult.setText(msg)
    }

    fun SaveUsersToDB(){
        ALL_USERS.forEach {
            var result =this.usersDBHelper.insertUser(UserModel(userid = it.id.toString() ,name = it.firstName.toString(),age = it.lastName.toString()))
            RefreshUserInterface("Added user : "+result.toString())
        }
    }

    fun addUser(){
        val userid = findViewById<EditText>(R.id.edittext_userid).getText().toString()
        val name = findViewById<EditText>(R.id.edittext_name).getText().toString()
        val age = findViewById<EditText>(R.id.edittext_age).getText().toString()

        var result =this.usersDBHelper.insertUser(UserModel(userid = userid,name = name,age = age))
        RefreshUserInterface("Added user : "+result.toString())
    }

    fun doApiCall(){
        fetchUsersFromApi()
        SaveUsersToDB()
    }

    fun deleteUser() {
        val userid = findViewById<EditText>(R.id.edittext_userid).getText().toString()
        val result = usersDBHelper.deleteUser(userid)
        RefreshUserInterface("Deleted user : " + result)
    }

    fun dropDB(){
        val result = usersDBHelper.deleteDB()
        RefreshUserInterface("DROPPED DATABASE: "+result)
    }

    fun showAllUsersFromDB(){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)
        var users = usersDBHelper.readAllUsers()

        linearLayoutView.removeAllViews()

        users.forEach {
            var tv_user = TextView(this)
            tv_user.textSize = 30F
            tv_user.text = it.userid.toString() + " - " +it.name.toString() + " - " + it.age.toString()

            linearLayoutView.addView(tv_user)
        }
        textViewResult.setText("Fetched " + users.size + " users")
    }

    fun ShowAllUsersFrom_ALLUSERS_ARRAY(){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)

        var msg = ""
        linearLayoutView.removeAllViews()

        ALL_USERS.forEach {
            var tv_user = TextView(this)
            tv_user.textSize = 30F
            tv_user.text = it.id.toString() + " - " +it.firstName.toString() + " - " + it.lastName.toString()

            linearLayoutView.addView(tv_user)
        }
        textViewResult.setText("Fetched " + ALL_USERS.size + " users")
    }
}