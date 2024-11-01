package com.project.bridgetalk

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.PostViewAdapter
import com.project.bridgetalk.databinding.PostRecyclerviewBinding
import com.project.bridgetalk.model.vo.Post
import java.time.LocalDateTime
import java.util.UUID

class PostListViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = PostRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 스피너 항목 데이터 추가
        val categories = listOf("전체", "홍보", "자유")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 연결
        binding.categorySpinner.adapter = adapter

        val data = mutableListOf<Post>()
        data.add(
            Post(
                postId = UUID.fromString("faf2435f-969f-460f-ba7e-471f97570b54"),
                user = null,
                schools = null,
                title = "게시물 제목",
                content = "Content for post 1",
                like_count = 10,
                createdAt = LocalDateTime.parse("2023-10-01T00:00:00"),
                updatedAt = LocalDateTime.parse("2023-10-05T00:00:00"),
                type = "general"
            ))

        data.add(
            Post(
                postId = UUID.fromString("faf2435f-969f-460f-ba7e-471f97570b54"),
                user = null,
                schools = null,
                title = "게시물 제목",
                content = "Content for post 1",
                like_count = 10,
                createdAt = LocalDateTime.parse("2023-10-01T00:00:00"),
                updatedAt = LocalDateTime.parse("2023-10-05T00:00:00"),
                type = "general"
            )
        )

        binding.postView.layoutManager = LinearLayoutManager(this)
        binding.postView.adapter = PostViewAdapter(data)
        binding.postView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
    }
}