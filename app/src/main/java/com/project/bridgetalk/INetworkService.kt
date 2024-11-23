package com.project.bridgetalk

import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.request.JoinRequest
import com.project.bridgetalk.model.vo.dto.request.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface INetworkService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("/join")
    fun join(@Body joinRequest: JoinRequest ): Call<ResponseBody>

//    @Post("/post/postMake")
//
//    @Get("/post/{id}")
//    fun postSelect(@Path("id") id : Int) : Call<List<Call>>

}