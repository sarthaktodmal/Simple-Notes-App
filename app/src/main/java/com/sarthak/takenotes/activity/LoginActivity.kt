package com.sarthak.takenotes.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sarthak.takenotes.databinding.LoginActivityBinding
import com.sarthak.takenotes.extra.MyToast


class LoginActivity : AppCompatActivity() {
    val binding by lazy{
        LoginActivityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginBut.setOnClickListener{
            Login()
        }
        binding.signupBut.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignActivity::class.java))
        }
        binding.forget.setOnClickListener{
            startActivity(Intent(this@LoginActivity, ForgetPassword::class.java))
        }
    }
    private fun Login() {
        if(binding.email.text.toString().isEmpty() || binding.pass.text.toString().isEmpty()){
            MyToast.showCustomToast(this,"Please Enter All Fields",false)
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.email.text.toString(),binding.pass.text.toString())
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    onSuccess()
                }else{
                    MyToast.showCustomToast(applicationContext,"Login Failed",false)
                }
            }
    }

    fun onSuccess(){
        MyToast.showCustomToast(applicationContext,"Login Sucessful",true)
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}