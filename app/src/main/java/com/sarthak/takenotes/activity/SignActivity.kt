package com.sarthak.takenotes.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sarthak.takenotes.databinding.SignupActivityBinding
import com.sarthak.takenotes.extra.MyToast
import com.sarthak.takenotes.utils.Utils


class SignActivity : AppCompatActivity() {
    val binding by lazy{
        SignupActivityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginBut.setOnClickListener{
            finish()
        }
        binding.signBut.setOnClickListener{
            SignUp()
        }
    }
    fun SignUp(){
        if(binding.email.text.toString().isEmpty() ||
            binding.pass.text.toString().isEmpty() ||
            binding.repass.text.toString().isEmpty()){
            MyToast.showCustomToast(this,"Please Enter All Fields",false)
            return
        }else if(!Utils.isValidEmail(binding.email.text.toString())){
            MyToast.showCustomToast(this,"Invalid Email",false)
            return
        }else if(binding.pass.text.toString() != binding.repass.text.toString()){
            MyToast.showCustomToast(this,"Password does not match",false)
            return
        }else if (binding.pass.text.toString().length < 6) {
            MyToast.showCustomToast(this,"Password must be at least 6 characters",false)
            return
        }
        else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.email.text.toString(),binding.pass.text.toString())
                .addOnCompleteListener(this) { task->
                    if(task.isSuccessful){
                        onSuccess()
                    }else{
                        val errorMessage = task.exception?.message.toString()
                        MyToast.showCustomToast(this,"Error: $errorMessage",false)
                    }
                }
        }
    }

    fun onSuccess(){
        MyToast.showCustomToast(this,"Sign up Successful",true)
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}