package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.bridgetalk.Adapter.ChatAdapter
import com.project.bridgetalk.model.vo.ChatItem

class ChatListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // BottomNavigationView 이벤트 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    
                    true
                }
                R.id.navigation_chat -> {
                    // 채팅 화면으로 이동(현재화면)
                    true
                }
                R.id.navigation_my -> {
                    // 마이페이지 화면으로 이동
                    true
                }
                else -> false
            }
        }

        // FloatingActionButton 이벤트 설정
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // 새 채팅 시작 (액티비티 이동)
            val intent = Intent(this, MatchingActivity::class.java)
            startActivity(intent)
        }


        // 샘플 데이터 추가
        val chatList = listOf(
            ChatItem("User1", "Hello!", R.drawable.logo, "12:00 PM"),
            ChatItem("User2", "How are you?", R.drawable.logo, "12:05 PM"),
            ChatItem("User3", "What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?What's up?", R.drawable.logo, "12:10 PM"),
            ChatItem("User4", "See you later!", R.drawable.logo, "12:15 PM"),
            ChatItem("User5", "Good morning!", R.drawable.logo, "12:20 PM"),
            ChatItem("User1", "Hello!", R.drawable.logo, "12:00 PM"),
            ChatItem("User2", "How are you?", R.drawable.logo, "12:05 PM"),
            ChatItem("User3", "What's up?", R.drawable.logo, "12:10 PM"),
            ChatItem("User4", "See you later!", R.drawable.logo, "12:15 PM"),
            ChatItem("User1", "Hello!", R.drawable.logo, "12:00 PM"),
            ChatItem("User2", "How are you?", R.drawable.logo, "12:05 PM"),
            ChatItem("User3", "What's up?", R.drawable.logo, "12:10 PM"),
            ChatItem("User4", "See you later!", R.drawable.logo, "12:15 PM"),
            ChatItem("User1", "Hello!", R.drawable.logo, "12:00 PM"),
            ChatItem("User2", "How are you?", R.drawable.logo, "12:05 PM"),
            ChatItem("User3", "What's up?", R.drawable.logo, "12:10 PM"),
            ChatItem("User4", "See you later!", R.drawable.logo, "12:15 PM"),
            ChatItem("User1", "Hello!", R.drawable.logo, "12:00 PM"),
            ChatItem("User2", "How are you?", R.drawable.logo, "12:05 PM"),
            ChatItem("User3", "What's up?", R.drawable.logo, "12:10 PM"),
            ChatItem("User4", "See you later!", R.drawable.logo, "12:15 PM")
        )
        recyclerView.adapter = ChatAdapter(chatList)
    }
}