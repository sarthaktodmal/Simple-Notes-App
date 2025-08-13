package com.sarthak.takenotes.utils

class Utils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            return email.matches(emailRegex.toRegex())
        }
    }
}