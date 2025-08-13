package com.sarthak.takenotes
import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable Firebase Database persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}