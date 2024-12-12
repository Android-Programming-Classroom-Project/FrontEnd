package com.project.bridgetalk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.project.bridgetalk.Adapter.MessageAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.ActivityChatBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.ChatItem
import com.project.bridgetalk.model.vo.ChatMessage
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.request.UserChatroomRequest
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    //Websocket 연결
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
    private var chatMessages = mutableListOf<ChatMessage>()
    var originalData = mutableListOf<ChatMessage>()//원본 데이터로 만들기 위한 list
    lateinit var chatItem: ChatItem
    private var token : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        token = SharedPreferencesUtil.getToken(this).toString()
        val user = UserManager.user?.copy()
        //특정 채팅방 정보 불러오기
        val roomId = UUID.fromString(intent.getStringExtra("roomId"))

        val chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        messageAdapter = MessageAdapter(chatMessages)
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                val chatMessage =  ChatMessage(user = user!!, messageText, isSent = true)
                // 서버로 메시지 전송 및 성공 시 list에 저장
                if (sendMessage(chatMessage, roomId)  == true) {
                    chatMessages.add(chatMessage)
                    originalData.clear()
                    originalData = chatMessages.map{ it.copy()}.toMutableList()
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
                                    var result = Gson().fromJson(
                                        message,
                                        ChatMessage::class.java
                                    )
                                    if (!result.user?.userId.equals(user!!.userId)) {
                                        chatMessages.add(result)
                                        originalData.clear()
                                        originalData = chatMessages.map{ it.copy()}.toMutableList()
                                        runOnUiThread {
                                            messageAdapter.notifyItemInserted(chatMessages.size - 1)
                                            chatRecyclerView.scrollToPosition(chatMessages.size - 1)
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
        }
        //채팅목록 과거 내역 불러오기
        if (user != null) {
            getPastMessage(user, roomId, this)
            chatRecyclerView.scrollToPosition(chatMessages.size - 1)
        }

        // 뒤로가기 버튼 클릭 시 동작
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
//            val intent = Intent(this, ChatListActivity::class.java)
//            startActivity(intent)
//            finish()
        }
        // 번역 설정 이벤트 처리
        val settingTranslateButton = binding.settingTranslate
        settingTranslateButton.setOnClickListener {
            val intent = Intent(this, SettingTranslateActivity::class.java)
            startActivity(intent)
        }

        // translate button
        val translateButton: ImageButton = binding.translate
        // 번역 클릭 이벤트처리
        translateButton.setOnClickListener {
            translateState = !translateState
            if (translateState) {
                // 번역 활성화 : 활성화 아이콘 변경
                translateButton.setBackgroundColor(Color.TRANSPARENT)
                translateButton.setImageResource(R.drawable.outline_g_translate_24_blue)
                performTranslation(binding)
                Toast.makeText(this, "번역 활성화", Toast.LENGTH_SHORT).show()
            } else {
                revertTranslation(binding)
                // 번역 비활성화 상태: 기본 아이콘 변경
                translateButton.setBackgroundColor(Color.TRANSPARENT)
                translateButton.setImageResource(R.drawable.outline_g_translate_24)
                Toast.makeText(this, "번역 비활성화", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        topicSubscription?.dispose()
        stompConnection?.dispose()
    }

    // 메세지 전송 성공 시 true 아니면 false
    fun sendMessage(messageData: ChatMessage, roomId: UUID): Boolean {
        val gson = Gson()
        var message = messageData

        if (message.chatRoom == null) {
            message.chatRoom = ChatItem()
        }

        message.chatRoom!!.roomId  = roomId
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

    // 번역 수행 함수
    private fun performTranslation(binding: ActivityChatBinding) {
        val (sourceLanguage, targetLanguage) = SharedPreferencesUtil.loadTranslate(this)
        //세팅이 안되어있을 때 세팅페이지로 이동
        if (sourceLanguage.isNullOrEmpty() || targetLanguage.isNullOrEmpty()) {
            // 번역 설정으로 이동
            val intent = Intent(this, SettingTranslateActivity::class.java)
            startActivity(intent)
            return
        }
        //필요한 번역 모델이 기기에 다운로드되었는지 확인
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                chatMessages.forEachIndexed { index, chatMessage ->
                    val sourceText = chatMessage.content
//                    val titleText = post.title
                    if (sourceText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("test", translatedText)
                                chatMessage.content = translatedText
                                chatMessages[index] = chatMessage
                                binding.chatRecyclerView.adapter?.notifyItemChanged(index)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
            }
    }

    // 번역 비활성화 시 원본 콘텐츠로 되돌리기
    private fun revertTranslation(binding: ActivityChatBinding) {
        chatMessages.clear()
        chatMessages.addAll(originalData.map { it.copy() })
        chatMessages.forEachIndexed { index, chatMessage ->
            binding.chatRecyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun getPastMessage(user: User, roomId: UUID, context: Context){
        var u = user
        if(user == null){
            return
        }
        u.updatedAt = null
        u.createdAt = null
        val chatRoom = ChatItem(roomId = roomId)
        val call = MyApplication.networkService.getChatMessage(token.toString(),UserChatroomRequest(u, chatRoom))

        call.enqueue(object : Callback<List<ChatMessage>> {
            override fun onResponse(call: Call<List<ChatMessage>>, response: Response<List<ChatMessage>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        chatMessages.addAll(data)
                    }
                    for(index in chatMessages.indices){
                        if(chatMessages[index].user!!.userId == user.userId){
                            chatMessages[index].isSent= true
                        }
                    }

                    originalData.clear()
                    originalData = chatMessages.map { it.copy() }.toMutableList()
                    Log.v("test", data.toString())
                    messageAdapter.notifyDataSetChanged()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@ChatActivity, "서버 요청 실패: ${errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@ChatActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Exception: ${t.message}", t)
            }
        })

    }
}

