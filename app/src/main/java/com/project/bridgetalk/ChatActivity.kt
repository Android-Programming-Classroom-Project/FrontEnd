package com.project.bridgetalk

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.Adapter.MessageAdapter
import com.project.bridgetalk.model.vo.ChatMessage


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
            finish() // 현재 액티비티 종료
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
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
            }
        }
    }
}

