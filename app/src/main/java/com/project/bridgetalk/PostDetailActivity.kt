package com.project.bridgetalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.CommentAdapter
import com.project.bridgetalk.databinding.PostDetailBinding
import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.Schools
import com.project.bridgetalk.model.vo.User
import java.util.UUID

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: PostDetailBinding
    private lateinit var commentAdapter: CommentAdapter

    // 예시 데이터, 실제 데이터로 교체해야 함
    private val comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // RecyclerView 설정
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(comments)
        binding.commentRecyclerView.adapter = commentAdapter

        // 댓글 등록 버튼 클릭 리스너
        binding.commentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotBlank()) {
                // 댓글 추가 및 RecyclerView 갱신
                addComment(commentText)
                binding.commentEditText.text.clear()
            }
        }

        // 예시 데이터 추가
        loadSampleData()
    }

    private fun loadSampleData() {
        // 예시 Schools 객체
        val schools = Schools(schoolId = "1", schoolName = "한신대") // Schools 예시 객체
        val post = Post(
            postId = UUID.randomUUID(),
            user = User(
                userId = "1",
                username = "사용자1",
                email = "user1@example.com",
                password = "password123",
                schools = schools,
                role = "학생",
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            schools = schools,
            title = "게시물 제목",
            content = "게시물 내용",
            like_count = 5,
            createdAt = "2023-10-01T00:00:00",
            updatedAt = "2023-10-05",
            type = "general"
        )

        val user1 = User(
            userId = "1",
            username = "사용자1",
            email = "user1@example.com",
            password = "password123",
            schools = schools,
            role = "학생",
            createdAt = "2023-01-01",
            updatedAt = "2023-01-01"
        )
        val user2 = User(
            userId = "2",
            username = "사용자2",
            email = "user2@example.com",
            password = "password123",
            schools = schools,
            role = "학생",
            createdAt = "2023-02-01",
            updatedAt = "2023-02-01"
        )

        // 예시 댓글 추가
        comments.add(
            Comment(
                commentId = UUID.randomUUID(),
                post = post,
                user = user1,
                content = "첫 번째 댓글입니다.",
                updatedAt = "2023.10.31",
                createdAt = "2023.10.31"
            )
        )
        comments.add(
            Comment(
                commentId = UUID.randomUUID(),
                post = post,
                user = user2,
                content = "두 번째 댓글입니다.",
                updatedAt = "2023.10.31",
                createdAt = "2023.10.31"
            )
        )
        commentAdapter.notifyDataSetChanged() // 데이터 갱신
    }

    private fun addComment(content: String) {
        // 예시로 현재 사용자와 날짜 설정
        val schools = Schools(schoolId = "2", schoolName = "경기대")
        val currentUser = User(
            userId = "3",
            username = "현재 사용자",
            email = "currentuser@example.com",
            password = "password123",
            schools = schools,
            role = "학생",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        val post = Post(
            postId = UUID.randomUUID(),
            user = currentUser,
            schools = schools,
            title = "새 게시물",
            content = content,
            like_count = 0,
            createdAt = "2023-10-01T00:00:00",
            updatedAt = "2023-10-05",
            type = "general"
        )

        // 새 댓글 추가
        val newComment = Comment(
            commentId = UUID.randomUUID(),
            post = post,
            user = currentUser,
            content = content,
            updatedAt = "2023.10.31",
            createdAt = "2023.10.31"
        )
        comments.add(newComment)
        commentAdapter.notifyItemInserted(comments.size - 1)
        binding.commentRecyclerView.scrollToPosition(comments.size - 1) // 새 댓글로 스크롤
    }
}
