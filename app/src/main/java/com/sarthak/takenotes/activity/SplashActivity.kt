package com.sarthak.takenotes.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sarthak.takenotes.databinding.SplashActivityBinding

class SplashActivity: AppCompatActivity(){
    val binding by lazy{
        SplashActivityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler.createAsync(Looper.getMainLooper()).postDelayed({
                if(FirebaseAuth.getInstance().uid != null){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            },2000
        )
    }
}