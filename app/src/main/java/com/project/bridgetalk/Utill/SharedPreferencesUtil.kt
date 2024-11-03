package com.project.bridgetalk.Utill

import android.content.Context
//

object SharedPreferencesUtil {

    //번역 언어 저장
    fun saveTranslate(context: Context, startLang:String, targetLang:String){
        if(startLang !=null && targetLang != null){
            val sharedPref = context.getSharedPreferences("translate", Context.MODE_PRIVATE)
            sharedPref.edit().run{
                putString("startLang",startLang)
                putString("targetLang",targetLang)
                apply()
            }
        }
    }
    //번역 언어 저장한 정보 불러오기
    //val (startLang, targetLang) = SharedPreferencesUtil.loadTranslate(this) 사용방법
    fun loadTranslate(context: Context): Pair<String?, String?> {
        val sharedPref = context.getSharedPreferences("translate", Context.MODE_PRIVATE)
        val startLang = sharedPref.getString("startLang", null)
        val targetLang = sharedPref.getString("targetLang", null)
        return Pair(startLang, targetLang)
    }

    //토큰 저장
    fun saveToken(context: Context, token: String?) {
        if(token != null){
            val sharedPref = context.getSharedPreferences("access", Context.MODE_PRIVATE)
            sharedPref.edit().run {
                putString("token", token)
                commit() // commit() 대신 apply()를 사용 시 비동기로 저장 가능
            }
        }
    }
    //토큰 불러오기
    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("access", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
