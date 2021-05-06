package com.example.labise.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.labise.View.Fragment.LoginFragment
import com.example.labise.R

class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction().replace(R.id.login_activity_fragment_container, LoginFragment()).commit()
    }




}