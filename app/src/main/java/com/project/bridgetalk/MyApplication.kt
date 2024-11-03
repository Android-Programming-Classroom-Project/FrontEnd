package com.project.bridgetalk

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {
    companion object{
        var networkService: INetworkService
        val retrofit: Retrofit
            get() =  Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8888/") // 서버의 베이스 URL
                .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
                .build()
        init{
            networkService = retrofit.create(INetworkService::class.java)
        }
    }
}