package com.sarthak.takenotes.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sarthak.takenotes.databinding.ActivityForgetPasswordBinding
import com.sarthak.takenotes.extra.MyToast

class ForgetPassword : AppCompatActivity() {
    val binding by lazy{
        ActivityForgetPasswordBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.submitBut.setOnClickListener {
            val email = binding.email.text.toString().trim()
            if (email.isEmpty()) {
                MyToast.showCustomToast(this, "Please enter your email", false)
            } else {
                //reset
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            MyToast.showCustomToast(applicationContext, "Unable to Send Reset Email", false)
                        }
                    }
            }
        }
    }
    fun onSuccess(){
        MyToast.showCustomToast(applicationContext,"Reset Email Sent",true)
        finish()
    }
}