package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.Gson
import com.project.bridgetalk.Adapter.MessageAdapter
import com.project.bridgetalk.model.vo.ChatItem
import com.project.bridgetalk.model.vo.ChatMessage
import com.project.bridgetalk.model.vo.Schools
import com.project.bridgetalk.model.vo.User
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.util.UUID
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {
    //User 정보 불러오기
    //실제에서는 주석 풀어주기
//    val user = UserManager.user
    val schools1 = Schools("9790779a-3a29-4b4a-9e66-9974411e662e", schoolName = "한신대학교")
    val user = User(
        userId = "cc3abbc7-8471-475b-b553-7c01fac23578",
        username = "josdf",
        email = "admin",
        password = "password123",
        schools = schools1,
        role = "student",
        createdAt = null,
        updatedAt = null
    )

    //Websocket 연결
    //변수명 수정하면안되요 url로 할 시 인식을 못해요... 이유 모르겠습니다..
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

    private lateinit var messageAdapter: MessageAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    lateinit var chatItem: ChatItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 뒤로가기 버튼 클릭 시 동작
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        messageAdapter = MessageAdapter(chatMessages)
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)
        //특정 채팅방 정보 불러오기
        val roomId = UUID.fromString(intent.getStringExtra("roomId"))
//        val roomId = UUID.fromString("a0021efa-9b7f-4db4-9d4f-394471a04da5")

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                val chatMessage: ChatMessage = ChatMessage(messageText, isSent = true)
                // 서버로 메시지 전송 및 성공 시 list에 저장
                if(sendMessage(chatMessage,roomId)){
                    chatMessages.add(chatMessage)
                    messageAdapter.notifyItemInserted(chatMessages.size - 1)
                    chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                    messageEditText.text.clear()
                }
            }
        }


        //웹소켓 연결
        if (roomId != null) {
            stompConnection = stomp.connect()
                .subscribe {
                    when (it.type) {
                        Event.Type.OPENED -> {
                            println("WebSocket 연결이 성공적으로 열렸습니다.")
                            topicSubscription = stomp.join("/sub/$roomId")
                                .subscribe { message ->
                                    Log.v("test", Gson().fromJson(message,ChatMessage::class.java).content.toString())
                                    chatMessages.add(
                                        Gson().fromJson(
                                            message,
                                            ChatMessage::class.java
                                        )
                                    )
                                    messageAdapter.notifyItemInserted(chatMessages.size - 1)
                                    chatRecyclerView.scrollToPosition(chatMessages.size - 1)
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
        }
    }


    // 서버 요청 채팅 메세지 과거내역 조회
    fun getMessageList(user: User) {

    }

    override fun onDestroy() {
        super.onDestroy()
        topicSubscription?.dispose()
        stompConnection?.dispose()
    }

    // 메세지 전송 성공 시 true 아니면 false
    fun sendMessage(message: ChatMessage, roomId: UUID): Boolean {
        val gson = Gson()
        val messageJson = gson.toJson(message) // Mes 객체를 JSON 문자열로 변환
        var check = false
        stomp.send("/pub/$roomId", messageJson).subscribe { success ->
            if (success) {
                check = true
                Log.v("websocket", "메세지 전송 성공")
            } else {
                Log.v("websocket", "메세지 전송 실패")
            }
        }
        return check
    }

}

