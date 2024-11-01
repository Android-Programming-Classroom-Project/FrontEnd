package com.project.bridgetalk.stomp

import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.util.Scanner
import java.util.concurrent.TimeUnit

fun main() {
    // StompClient의 URL 설정
    val url = "ws://172.18.176.1:8888/bridgeTalkMessaging"  // ws://로 WebSocket 연결

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

    // Stomp 연결
    stompConnection = stomp.connect().subscribe {
        when (it.type) {
            Event.Type.OPENED -> {
                println("WebSocket 연결이 성공적으로 열렸습니다.")
                // 구독(subscribe)
                topicSubscription = stomp.join("/sub/hello")
                    .subscribe { message ->
                        println("수신된 메시지: $message")
                    }

            }
            Event.Type.CLOSED -> {
                println("WebSocket 연결이 닫혔습니다.")
            }
            Event.Type.ERROR -> {
                println("WebSocket 연결 중 오류 발생: ${it.exception}")
            }
            else -> {
                println("알 수 없는 상태: ${it.type}")
            }
        }
    }
    // 메시지 전송(send)
    fun sendMessage(stomp: StompClient, topic: String, message: String) {
        val gson = Gson()
        val messageJson = gson.toJson(Mes(message)) // Mes 객체를 JSON 문자열로 변환

        stomp.send(topic, messageJson).subscribe { success ->
            if (success) {
                println("메시지가 성공적으로 전송되었습니다.")
            } else {
                println("메시지 전송 실패.")
            }
        }
    }



    // 종료를 위한 입력 대기
    val scanner = Scanner(System.`in`)
    println("종료하려면 Enter 키를 누르세요.")
    scanner.nextLine()
    sendMessage(stomp,"/pub/greetings","test")
    // 리소스 정리 (연결 해제 및 구독 취소)
    topicSubscription?.dispose()
    stompConnection?.dispose()
}