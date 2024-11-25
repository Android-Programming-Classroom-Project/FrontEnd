package com.project.bridgetalk

import com.project.bridgetalk.model.vo.ChatMessage
import com.project.bridgetalk.model.vo.LikeRequest
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.PostCommentDTO
import com.project.bridgetalk.model.vo.dto.request.JoinRequest
import com.project.bridgetalk.model.vo.dto.request.LoginRequest
import com.project.bridgetalk.model.vo.dto.request.UserChatroomRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface INetworkService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("/join")
    fun join(@Body joinRequest: JoinRequest ): Call<ResponseBody>

    @GET("post/schoolPost/{schoolId}")
    fun getAllPosts(
        @Path("schoolId") schoolId: String
    ): Call<List<Post>>

    @PUT("post/addLiked")
    fun addLikedPost(
        @Body request: LikeRequest // 요청 본문을 담는 데이터 클래스
    ): Call<Post>

    @PUT("/post/deleteLiked")
    fun deleteLikedPost(
        @Body request: LikeRequest // 요청 본문을 담는 데이터 클래스
    ): Call<Post>

    @GET("post/{id}")
    fun getPost(@Path("id") postId: UUID): Call<PostCommentDTO>

    @DELETE("/post/delete/{id}")
    fun deletePost(@Path("id") postId: UUID): Call<Void> // 성공적으로 삭제되면 Void를 반환

    @POST("/chat/message")
    fun getChatMessage(@Body request: UserChatroomRequest): Call<List<ChatMessage>>

    @POST("/post/postMake")
    fun makePost(@Body request: LikeRequest): Call<Post>

//    @Post("/post/postMake")
//
//    @Get("/post/{id}")
//    fun postSelect(@Path("id") id : Int) : Call<List<Call>>

}