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

        // BottomNavigationView 초기화 및 현재 페이지 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_chat

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, PostListViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_chat -> {
                    // 현재 페이지, 아무 작업도 하지 않음
                    true
                }
                R.id.navigation_my -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // FloatingActionButton 이벤트 설정
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // 새 채팅 시작 (MatchingActivity로 이동)
            val intent = Intent(this, MatchingActivity::class.java)
            startActivity(intent)
        }

        // 샘플 데이터 설정
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

        val chatList = listOf(
            ChatItem(
                roomId = UUID.fromString("a0021efa-9b7f-4db4-9d4f-394471a04da5"),
                user = user1,
                school = schools1,
                created_at = "2024-11-05 10:00:00"
            ),
            ChatItem(UUID.randomUUID(), user1, schools1, "2024-11-05 10:00:00"),
            ChatItem(UUID.randomUUID(), user1, schools1, "2024-11-05 10:00:00"),
            ChatItem(UUID.randomUUID(), user1, schools1, "2024-11-05 10:00:00"),
            ChatItem(UUID.randomUUID(), user1, schools1, "2024-11-05 10:00:00")
        )

        recyclerView.adapter = ChatAdapter(chatList)
    }
}