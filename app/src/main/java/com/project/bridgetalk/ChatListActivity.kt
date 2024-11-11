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
import com.project.bridgetalk.model.vo.Schools
import com.project.bridgetalk.model.vo.User
import java.util.UUID

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
        val schools1 = Schools(UUID.randomUUID().toString(), schoolName = "test")
        val user1 = User(
            userId = "user1",
            username = "Alice",
            email = "alice@example.com",
            password = "password123",
            schools = schools1,
            role = "Student",
            createdAt = "2024-10-01 10:00:00",
            updatedAt = "2024-11-05 09:00:00"
        )

        // 샘플 데이터 추가
        val chatList = listOf(
            ChatItem(
                roomId = UUID.fromString("a0021efa-9b7f-4db4-9d4f-394471a04da5"),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            ),ChatItem(
                roomId = UUID.randomUUID(),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            ),ChatItem(
                roomId = UUID.randomUUID(),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            ),ChatItem(
                roomId = UUID.randomUUID(),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            ),ChatItem(
                roomId = UUID.randomUUID(),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            )
        )
        recyclerView.adapter = ChatAdapter(chatList)
    }
}