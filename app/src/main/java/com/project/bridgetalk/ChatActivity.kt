package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.Adapter.MessageAdapter
import com.project.bridgetalk.model.vo.ChatMessage
import com.project.bridgetalk.model.vo.User


class ChatActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

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

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                chatMessages.add(ChatMessage(messageText, isSent = true))
                messageAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                messageEditText.text.clear()

                chatMessages.add(ChatMessage("응답: $messageText", isSent = false))
                messageAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1) // (마지막 리스트로 아래로 출력)
            }
        }
    }

    // 서버 요청 채팅 메세지 과거내역 조회
    fun getMessageList(user: User){

    }

}

