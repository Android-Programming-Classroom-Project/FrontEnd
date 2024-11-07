package com.project.bridgetalk.stomp

import android.util.Log
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.Gson
import com.project.bridgetalk.model.vo.ChatMessage
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.util.UUID
import java.util.concurrent.TimeUnit

class Chat(var chatRoomId: UUID) {
    // StompClient의 URL 설정
    val url = "ws://192.168.123.136:8888/bridgeTalkMessaging"  // ws://로 WebSocket 연결

    // WebSocket 재연결 간격 설정
    val intervalMillis = 5000L

    // OkHttpClient 설정
    val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    // StompClient 생성
    val stomp = StompClient(client, intervalMillis).apply { this@apply.url = url }
    var stompConnection: Disposable? = null
    var topicSubscription: Disposable? = null


    fun connectStomp(): Disposable {
        return stomp.connect().subscribe {
            when (it.type) {
                Event.Type.OPENED -> {
                    println("WebSocket 연결이 성공적으로 열렸습니다.")
                }

                Event.Type.CLOSED -> {
                    println("WebSocket 연결이 닫혔습니다.")
                }

                Event.Type.ERROR -> {
                    println("WebSocket 연결 중 오류 발생: \${it.exception}")
                }

                else -> {
                    println("알 수 없는 상태: \${it.type}")
                }
            }
        }
    }

    //메세지 수신자
    fun subscribeToMessages(): Disposable? {
        return stomp.join("/sub/$chatRoomId")
            .subscribe({ message ->
                val chatMessage = Gson().fromJson(message, ChatMessage::class.java)
            }, { error ->
                Log.e("Chat", "메시지 수신 중 오류 발생", error)
            })
    }
    // 메시지 전송(send)
    fun sendMessage(message: ChatMessage) {
        val gson = Gson()
        val messageJson = gson.toJson(message) // Mes 객체를 JSON 문자열로 변환

        stomp.send("/pub/$chatRoomId", messageJson).subscribe { success ->
            if (success) {
                Log.v("websocket", "메세지 전송 성공")
            } else {
                Log.v("websocket", "메세지 전송 실패")
            }
        }
    }

    // 연결 종료 메소드
    fun disconnect() {
        // 리소스 정리 (연결 해제 및 구독 취소)
        topicSubscription?.dispose()
        stompConnection?.dispose()
    }
}