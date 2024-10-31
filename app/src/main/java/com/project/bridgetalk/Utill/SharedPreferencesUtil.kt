package com.project.bridgetalk.Utill

import android.content.Context
//

object SharedPreferencesUtil {

    fun saveToken(context: Context, token: String?) {
        if(token != null){
            val sharedPref = context.getSharedPreferences("access", Context.MODE_PRIVATE)
            sharedPref.edit().run {
                putString("token", token)
                commit() // commit() 대신 apply()를 사용 시 비동기로 저장 가능
            }
        }
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("access", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
