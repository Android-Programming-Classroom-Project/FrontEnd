package com.project.bridgetalk

import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.request.JoinRequest
import com.project.bridgetalk.model.vo.dto.request.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface INetworkService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("/join")
    fun join(@Body joinRequest: JoinRequest ): Call<ResponseBody>

    @GET("post/schoolPost/{schoolId}")
    fun getAllPosts(
        @Path("schoolId") schoolId: String
    ): Call<List<Post>>


//    @Post("/post/postMake")
//
//    @Get("/post/{id}")
//    fun postSelect(@Path("id") id : Int) : Call<List<Call>>

}