package com.sarthak.takenotes.extra

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sarthak.takenotes.R

class MyToast {
    companion object {
        fun showCustomToast(context: Context, message: String, sucess:Boolean) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val customToastLayout: View = inflater.inflate(R.layout.toast_layout, null)

            val toastText: TextView = customToastLayout.findViewById(R.id.toast_msg)
            toastText.text = message

            val toastImage: ImageView = customToastLayout.findViewById(R.id.toast_img)
            if(sucess){
                toastImage.setImageResource(R.drawable.tick)
            }else{
                toastImage.setImageResource(R.drawable.cross)
            }

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = customToastLayout
            toast.show()
        }
    }
}