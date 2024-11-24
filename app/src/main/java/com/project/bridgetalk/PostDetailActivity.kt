package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.CommentAdapter
import com.project.bridgetalk.databinding.PostDetailBinding
import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.dto.PostCommentDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: PostDetailBinding
    private lateinit var commentAdapter: CommentAdapter

    private val comments = mutableListOf<Comment>() // 예시 데이터, 실제 데이터로 교체해야 함
    private lateinit var postId: UUID // 게시물 ID를 저장할 변수
    private lateinit var postComment: PostCommentDTO // 전역 변수로 게시물 객체 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 뒤로가기 버튼 클릭 시 동작
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, PostListViewActivity::class.java)
            startActivity(intent)
            finish()
        }

        // RecyclerView 설정
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(comments)
        binding.commentRecyclerView.adapter = commentAdapter

//        // 댓글 등록 버튼 클릭 리스너
//        binding.commentButton.setOnClickListener {
//            val commentText = binding.commentEditText.text.toString()
//            if (commentText.isNotBlank()) {
//                // 댓글 추가 및 RecyclerView 갱신
//                addComment(commentText)
//                binding.commentEditText.text.clear()
//            }
//        }

        // Intent로부터 게시물 ID를 받기
        postId = UUID.fromString(intent.getStringExtra("POST_ID"))

        // 게시물 상세 정보 로드
        loadPostDetails(postId)
//        // 예시 데이터 추가
//        loadSampleData()
    }

    private fun loadPostDetails(postId: UUID) {
        // API 호출을 통해 해당 게시물의 상세 정보를 가져오는 로직을 추가
        val call = MyApplication.networkService.getPost(postId)
        call.enqueue(object : Callback<PostCommentDTO> {
            override fun onResponse(call: Call<PostCommentDTO>, response: Response<PostCommentDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { postCommentDTO ->
                        // 게시물 정보 업데이트
                        postComment = postCommentDTO
                        updatePostDetails(postComment.post)
                        // 댓글 리스트 업데이트 (null 체크 추가)
                        comments.clear()
                        postCommentDTO.commentList?.let {
                            comments.addAll(it) // 댓글 리스트가 null이 아닐 경우 추가
                        } ?: run {
                            Log.w("PostDetailActivity", "Comment list is null, using empty list.")
                        }

                        commentAdapter.notifyDataSetChanged() // 어댑터 갱신

                    }
                }  else {
                    Toast.makeText(this@PostDetailActivity, "게시물 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostCommentDTO>, t: Throwable) {
                Toast.makeText(this@PostDetailActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Exception: ${t.message}", t)
            }
        })
    }

    private fun updatePostDetails(post: Post) {
        // UI에 게시물 정보 업데이트
        binding.postTitle.text = post.title // XML의 postTitle에 맞게 수정
        binding.postContent.text = post.content // XML의 postContent에 맞게 수정
        binding.likeCount.text = post.like_count.toString() // XML의 likeCount에 맞게 수정
        binding.createdAt.text = post.createdAt.toString()
        // 댓글 수 업데이트 (null 체크 추가)
        if (postComment.commentList != null) {
            binding.commentCount.text = postComment.commentList?.size.toString()
        } else {
            binding.commentCount.text = "0" // 기본값 설정
            Log.w("PostDetailActivity", "Comment list is null")
        }
    }

//    private fun loadSampleData() {
//        // 예시 Schools 객체
//        val schools = Schools(schoolId = "1", schoolName = "한신대") // Schools 예시 객체
//        val post = Post(
//            postId = UUID.randomUUID(),
//            user = User(
//                userId = "1",
//                username = "사용자1",
//                email = "user1@example.com",
//                password = "password123",
//                schools = schools,
//                role = "학생",
//                createdAt = "2023-01-01",
//                updatedAt = "2023-01-01"
//            ),
//            schools = schools,
//            title = "게시물 제목",
//            content = "게시물 내용",
//            like_count = 5,
//            createdAt = "2023-10-01T00:00:00",
//            updatedAt = "2023-10-05",
//            type = "general"
//        )
//
//        val user1 = User(
//            userId = "1",
//            username = "사용자1",
//            email = "user1@example.com",
//            password = "password123",
//            schools = schools,
//            role = "학생",
//            createdAt = "2023-01-01",
//            updatedAt = "2023-01-01"
//        )
//        val user2 = User(
//            userId = "2",
//            username = "사용자2",
//            email = "user2@example.com",
//            password = "password123",
//            schools = schools,
//            role = "학생",
//            createdAt = "2023-02-01",
//            updatedAt = "2023-02-01"
//        )
//
//        // 예시 댓글 추가
//        comments.add(
//            Comment(
//                commentId = UUID.randomUUID(),
//                post = post,
//                user = user1,
//                content = "첫 번째 댓글입니다.",
//                updatedAt = "2023.10.31",
//                createdAt = "2023.10.31"
//            )
//        )
//        comments.add(
//            Comment(
//                commentId = UUID.randomUUID(),
//                post = post,
//                user = user2,
//                content = "두 번째 댓글입니다.",
//                updatedAt = "2023.10.31",
//                createdAt = "2023.10.31"
//            )
//        )
//        commentAdapter.notifyDataSetChanged() // 데이터 갱신
//    }
//
//    private fun addComment(content: String) {
//        // 예시로 현재 사용자와 날짜 설정
//        val schools = Schools(schoolId = "2", schoolName = "경기대")
//        val currentUser = User(
//            userId = "3",
//            username = "현재 사용자",
//            email = "currentuser@example.com",
//            password = "password123",
//            schools = schools,
//            role = "학생",
//            createdAt = "2024-01-01",
//            updatedAt = "2024-01-01"
//        )
//        val post = Post(
//            postId = UUID.randomUUID(),
//            user = currentUser,
//            schools = schools,
//            title = "새 게시물",
//            content = content,
//            like_count = 0,
//            createdAt = "2023-10-01T00:00:00",
//            updatedAt = "2023-10-05",
//            type = "general"
//        )
//
//        // 새 댓글 추가
//        val newComment = Comment(
//            commentId = UUID.randomUUID(),
//            post = post,
//            user = currentUser,
//            content = content,
//            updatedAt = "2023.10.31",
//            createdAt = "2023.10.31"
//        )
//        comments.add(newComment)
//        commentAdapter.notifyItemInserted(comments.size - 1)
//        binding.commentRecyclerView.scrollToPosition(comments.size - 1) // 새 댓글로 스크롤
//    }
}
