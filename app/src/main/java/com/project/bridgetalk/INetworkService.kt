package com.project.bridgetalk

import com.project.bridgetalk.model.vo.ChatItem
import com.project.bridgetalk.model.vo.ChatMessage
import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.LikeRequest
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.ChatRoom
import com.project.bridgetalk.model.vo.dto.PostCommentDTO
import com.project.bridgetalk.model.vo.dto.request.CommentRequest
import com.project.bridgetalk.model.vo.dto.request.JoinRequest
import com.project.bridgetalk.model.vo.dto.request.LoginRequest
import com.project.bridgetalk.model.vo.dto.request.UserChatroomRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
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
        @Path("schoolId") schoolId: String, @Header("Authorization") authorization : String
    ): Call<List<Post>>

    @PUT("post/addLiked")
    fun addLikedPost(
        @Header("Authorization") authorization : String,
        @Body request: LikeRequest // 요청 본문을 담는 데이터 클래스
    ): Call<Post>

    @PUT("/post/deleteLiked")
    fun deleteLikedPost(
        @Header("Authorization") authorization : String,
        @Body request: LikeRequest // 요청 본문을 담는 데이터 클래스
    ): Call<Post>

    @GET("post/{id}")
    fun getPost(@Path("id") postId: UUID,@Header("Authorization") authorization : String,): Call<PostCommentDTO>

    @DELETE("/post/delete/{id}")
    fun deletePost(@Path("id") postId: UUID,@Header("Authorization") authorization : String): Call<Void> // 성공적으로 삭제되면 Void를 반환

    @POST("/post/postMake")
    fun makePost(@Header("Authorization") authorization : String, @Body request: LikeRequest): Call<Post>

    @POST("/chat/")
    fun selectChatList(@Header("Authorization") authorization : String,@Body user: User): Call<List<ChatItem>>

    @POST("/chat/message")
    fun getChatMessage(@Header("Authorization") authorization : String, @Body request: UserChatroomRequest): Call<List<ChatMessage>>

    @POST("/post/addComment")
    fun addComment(@Header("Authorization") authorization : String, @Body request: CommentRequest): Call<Comment>

    @HTTP(method = "DELETE", path = "/post/deleteComment/{id}", hasBody = true)
    fun deleteComment(
        @Path("id") commentId: UUID,  // 댓글 ID
        @Header("Authorization") authorization : String,
        @Body request: User // 요청 본문
    ): Call<Void>

    @POST("chat/makeChatroom")
    fun makeChat(@Body request: LikeRequest): Call<ChatRoom>

    @PUT("/post/update")
    fun editPost(
        @Body request: Post // 요청 본문을 담는 데이터 클래스
    ): Call<Post>

    @DELETE("/chat/delete/{roomId}")
    fun deleteChat(@Path("roomId") roomId: UUID): Call<Void> // 성공적으로 삭제되면 Void를 반환

//    @Post("/post/postMake")
//
//    @Get("/post/{id}")
//    fun postSelect(@Path("id") id : Int) : Call<List<Call>>

}