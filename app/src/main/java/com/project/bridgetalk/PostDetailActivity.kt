package com.project.bridgetalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.CommentAdapter
import com.project.bridgetalk.databinding.PostDetailBinding
import com.project.bridgetalk.manage.UserManager.user
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: PostDetailBinding
    private lateinit var commentAdapter: CommentAdapter

    // 샘플 데이터
//    private val user = User(
//        userId = "18b4bf0e-f275-4797-8ccc-f47eb1fb3ca0",
//        username = "josdf",
//        email = "admin@example.com",
//        schools = null,
//        role = "student",
//        createdAt = "2024-11-01",
//        updatedAt = null
//    )
//    private val post = Post(
//        postId = UUID.randomUUID(),
//        title = "샘플 게시글",
//        content = "게시글 내용",
//        user = user,
//        createdAt = "2024-11-01",
//        updatedAt = null
//    )
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
                addComment(commentText)
                binding.commentEditText.text.clear()
            }
        }

        // 샘플 데이터 추가
        loadSampleData()
    }

    private fun loadSampleData() {
        // 예시 댓글 데이터
        comments.add(
            Comment(
                commentId = UUID.randomUUID(),
                post = post,
                user = user,
                content = "첫 번째 댓글입니다.",
                updatedAt = null,
                createdAt = "2024-11-01"
            )
        )
        comments.add(
            Comment(
                commentId = UUID.randomUUID(),
                post = post,
                user = user,
                content = "두 번째 댓글입니다.",
                updatedAt = null,
                createdAt = "2024-11-01"
            )
        )
        commentAdapter.notifyDataSetChanged()
    }

    private fun addComment(content: String) {
        val newComment = Comment(
            commentId = UUID.randomUUID(),
            post = post,
            user = user,
            content = content,
            updatedAt = null,
            createdAt = "2024-11-19"
        )
        comments.add(newComment)
        commentAdapter.notifyItemInserted(comments.size - 1)
        binding.commentRecyclerView.scrollToPosition(comments.size - 1)
    }
}

//class PostDetailActivity : AppCompatActivity() {
//    private lateinit var binding: PostDetailBinding
//    private lateinit var commentAdapter: CommentAdapter
//    val schools1 = Schools("ac391ffd-4990-47e3-a868-ae57a45d291d", schoolName = "한신대학교")
//    val user = User(
//        userId = "18b4bf0e-f275-4797-8ccc-f47eb1fb3ca0",
//        username = "josdf",
//        email = "admi",
//        password = "password123",
//        schools = schools1,
//        role = "student",
//        createdAt = null,
//        updatedAt = null
//    )
//
//    // 예시 데이터, 실제 데이터로 교체해야 함
//    private val comments = mutableListOf<Comment>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = PostDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // 툴바 설정
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // RecyclerView 설정
//        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
//        commentAdapter = CommentAdapter(comments) { comment ->
//            // 대댓글 버튼 클릭 시 처리
//            toggleReplyInput(comment)
//        }
//        binding.commentRecyclerView.adapter = commentAdapter
//
//        // 댓글 등록 버튼 클릭 리스너
//        binding.commentButton.setOnClickListener {
//            val commentText = binding.commentEditText.text.toString()
//            if (commentText.isNotBlank()) {
//                // 댓글 추가 및 RecyclerView 갱신
//                addComment(commentText)
//                binding.commentEditText.text.clear()
//            }
//        }
//
//        // 예시 데이터 추가
//        loadSampleData()
//    }
//
//    private fun loadSampleData() {
//        // 예시 댓글 데이터 (나중에 실제 데이터로 대체해야 함)
//        comments.add(Comment("사용자1", "2023.10.31", "첫 번째 댓글입니다.", 0, emptyList()))
//        comments.add(Comment("사용자2", "2023.10.31", "두 번째 댓글입니다.", 0, emptyList()))
//        commentAdapter.notifyDataSetChanged()
//    }
//
//    private fun addComment(content: String) {
//        // 새 댓글 추가
//        val newComment = Comment("현재 사용자", "2023.10.31", content, 0, emptyList())
//        comments.add(newComment)
//        commentAdapter.notifyItemInserted(comments.size - 1)
//        binding.commentRecyclerView.scrollToPosition(comments.size - 1) // 새 댓글로 스크롤
//    }
//
//    private fun toggleReplyInput(comment: Comment) {
//        // 대댓글 입력 레이아웃을 보여주는 로직 (구현 필요)
//        //예) replyInputLayout.visibility = View.VISIBLE
//    }
//}