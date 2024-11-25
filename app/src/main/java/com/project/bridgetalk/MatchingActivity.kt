package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.Gson
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.Matching
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MatchingActivity : AppCompatActivity() {
    private lateinit var binding: MatchingActivity
    val url1 = "ws://129.154.54.25:8888/bridgeTalkMessaging"  // ws://로 WebSocket 연결

    // WebSocket 재연결 간격 설정
    val intervalMillis = 5000L

    // OkHttpClient 설정
    val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    // StompClient 생성
    val stomp = StompClient(client, intervalMillis).apply { this@apply.url = url1 }
    var stompConnection: Disposable? = null
    var topicSubscription: Disposable? = null

    // 타임아웃을 설정하기 위한 Handler
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)
        var userInfo = com.project.bridgetalk.manage.UserManager.user?.copy() ?: null
        if(userInfo == null){
            Toast.makeText(this, "User정보가 존재하지 않습니다 : System 에러", Toast.LENGTH_SHORT).show()
            finish()
        }
        // 툴바 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.title = "매칭 화면" // 툴바 제목 설정

        // 웹소켓 연결 제한 시간 60초 설정
        timeoutRunnable = Runnable {
            if (userInfo != null) {
                EndMessage(userInfo)
            }
            Toast.makeText(this, "매칭 시간이 초과되어 이전 페이지로 이동합니다.", Toast.LENGTH_SHORT).show()
            topicSubscription?.dispose()
            stompConnection?.dispose()
            finish()
        }
        handler.postDelayed(timeoutRunnable, 60000) // 60,000 밀리초 = 60초

        //웹소켓 연결
        stompConnection = stomp.connect()
            .subscribe {
                when (it.type) {
                    Event.Type.OPENED -> {
                        println("WebSocket 연결이 성공적으로 열렸습니다.")
                        if (userInfo != null) {
                            sendMessage(userInfo)
                        }
                        topicSubscription = stomp.join("/sub/matching")
                            .subscribe { message ->
                                var result = Gson().fromJson(
                                    message,
                                    Matching::class.java
                                )
                                // null 체크 및 타입 확인
                                if (result != null && result.type == "matching") {
                                    val userId = userInfo?.userId // userInfo가 null일 경우 대비
                                    if (userId != null && result.users?.contains(userId) == true) {
                                        // ChatActivity 페이지 이동
                                         startActivity(Intent(this, ChatActivity::class.java)) // 액티비티 이동
                                        intent.putExtra("roomId", result.chatRoom)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                }

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

        // 취소 버튼 이벤트 설정
        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener{
            if (userInfo != null) {
                EndMessage(userInfo)
            }
            topicSubscription?.dispose()
            stompConnection?.dispose()
            finish() // 액티비티 종료로 매칭 취소
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish() // 툴바의 뒤로가기 버튼 클릭 시 액티비티 종료
        return true
    }
    // 첫 연결
    fun sendMessage(user: User) {
        val gson = Gson()
        var message = Matching("", user.userId, null,null)

        val messageJson = gson.toJson(message)
        stomp.send("/pub/matching", messageJson).subscribe { success ->
            if (success) {
                Log.v("websocket", "메세지 전송 성공")
            } else {
                Log.v("websocket", "메세지 전송 실패")
            }
        }
    }

    //연결 끊기
    // 메세지 전송 성공 시 true 아니면 false
    fun EndMessage(user: User) {
        val gson = Gson()
        var message = Matching("cancel", user.userId, null,null)

        val messageJson = gson.toJson(message)
        stomp.send("/pub/matching", messageJson).subscribe { success ->
            if (success) {
                Log.v("websocket", "메세지 전송 성공")
            } else {
                Log.v("websocket", "메세지 전송 실패")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        topicSubscription?.dispose()
        stompConnection?.dispose()
    }
}
