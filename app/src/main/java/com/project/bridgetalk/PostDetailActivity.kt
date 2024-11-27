package com.project.bridgetalk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.project.bridgetalk.Adapter.CommentAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.PostDetailBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.LikeRequest
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.ChatRoom
import com.project.bridgetalk.model.vo.dto.PostCommentDTO
import com.project.bridgetalk.model.vo.dto.request.CommentRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: PostDetailBinding
    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>() // 예시 데이터, 실제 데이터로 교체해야 함
    private lateinit var postId: UUID // 게시물 ID를 저장할 변수
    private lateinit var postComment: PostCommentDTO // 전역 변수로 게시물_댓글 객체 선언
    private lateinit var recentPost: Post // 전역 변수로 게시물 객체 선언

    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    var originalData = mutableListOf<Comment>()//원본 데이터로 만들기 위한 list
    var originalPostData: Post? = null

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

        // 댓글 등록 버튼 클릭 리스너
        binding.commentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotBlank()) {
                val user = UserManager.user
                val post = recentPost
                if (user != null) {
                    // originalPostData가 null이 아닐 경우에만 addComment 호출
                    addComment(post, user, commentText)
                } else {
                    // 사용자정보가 없을 때 처리
                    val errorMessage = "사용자 정보가 없습니다."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 채팅시작 버튼 클릭 리스너
        binding.sendButton.setOnClickListener {
            val user = UserManager.user
            val post = recentPost
            if (user != null) {
                makeChat(post, user)
            } else {
                // 사용자정보가 없을 때 처리
                val errorMessage = "사용자 정보가 없습니다."
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Intent로부터 게시물 ID를 받기
        postId = UUID.fromString(intent.getStringExtra("POST_ID"))

        // 게시물 상세 정보 로드
        loadPostDetails(postId)

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

    private fun loadPostDetails(postId: UUID) {
        // API 호출을 통해 해당 게시물의 상세 정보를 가져오는 로직을 추가
        val call = MyApplication.networkService.getPost(postId)
        call.enqueue(object : Callback<PostCommentDTO> {
            override fun onResponse(
                call: Call<PostCommentDTO>,
                response: Response<PostCommentDTO>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { postCommentDTO ->
                        // 게시물 정보 업데이트
                        postComment = postCommentDTO
                        updatePostDetails(postComment.post)
                        originalPostData = postComment.post
                        recentPost = postComment.post
                        // 댓글 리스트 업데이트 (null 체크 추가)
                        comments.clear()
                        postCommentDTO.commentList?.let {
                            comments.addAll(it) // 댓글 리스트가 null이 아닐 경우 추가
                        } ?: run {
                            Log.w("PostDetailActivity", "Comment list is null, using empty list.")
                        }
                        commentAdapter.notifyDataSetChanged() // 어댑터 갱신
                    }
                    originalData = comments.map { it.copy() }.toMutableList()
                } else {
                    Toast.makeText(
                        this@PostDetailActivity,
                        "게시물 정보를 가져오는데 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PostCommentDTO>, t: Throwable) {
                Toast.makeText(
                    this@PostDetailActivity,
                    "서버 요청 실패: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

    // 댓글 추가 함수
    private fun addComment(post: Post, user: User, commentContent: String) {
        // 요청 객체 생성
        val request = CommentRequest(
            post = post,
            user = user,
            content = commentContent
        )

        request.post.createdAt = null
        request.post.user = null
        request.post.updatedAt = null
        request.user.createdAt = null
        request.user.updatedAt = null

        // API 호출
        val call = MyApplication.networkService.addComment(request)
        call.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    // 댓글 추가 성공 시 RecyclerView 업데이트
                    val newComment = response.body()
                    updateRecyclerViewWithNewComment(newComment)
                    // 입력란 비우기
                    binding.commentEditText.text.clear()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "댓글 추가 실패"
                    Toast.makeText(this@PostDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostDetailActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerViewWithNewComment(newComment: Comment?) {
        newComment?.let {
            // 새 댓글을 목록에 추가
            comments.add(it)
            originalData.clear()
            originalData = comments.map{it.copy()}.toMutableList()

            // RecyclerView 어댑터에 데이터 변경 알림
            commentAdapter.notifyItemInserted(comments.size - 1) // 새로 추가된 댓글의 위치
            binding.commentRecyclerView.scrollToPosition(comments.size - 1) // 새 댓글 추가 후 스크롤

            // 댓글 수 업데이트
            binding.commentCount.text = comments.size.toString() // 현재 comments 리스트의 크기로 업데이트

        } ?: run {
            // 새 댓글이 null인 경우 처리 (예: 로그 출력)
            Log.w("PostDetailActivity", "새 댓글이 null입니다.")
        }
    }

    // 댓글 수 업데이트 함수
    fun updateCommentCount() {
        binding.commentCount.text = comments.size.toString() // 댓글 수 업데이트
        updateOriginal()
    }

    fun updateOriginal() {
        originalData.clear()
        originalData.addAll(comments.map { it.copy() })
    }
    // 번역 수행 함수
    private fun performTranslation(binding: PostDetailBinding) {
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
                comments.forEachIndexed { index, post ->
                    val sourceText = post.content
//                    val titleText = post.title
                    if (sourceText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("test", translatedText)
                                post.content = translatedText
                                comments[index] = post
                                binding.commentRecyclerView.adapter?.notifyItemChanged(index)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                translator.translate(binding.postTitle.text.toString())
                    .addOnSuccessListener { translatedText ->
                        Log.v("test", translatedText)
                        binding.postTitle.text = translatedText
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                    }

                translator.translate(binding.postContent.text.toString())
                    .addOnSuccessListener { translatedText ->
                        Log.v("test", translatedText)
                        binding.postContent.text = translatedText
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
            }
    }

    // 번역 비활성화 시 원본 콘텐츠로 되돌리기
    private fun revertTranslation(binding: PostDetailBinding) {
        comments.clear()
        comments.addAll(originalData.map { it.copy() })
        comments.forEachIndexed { index, comment ->
            Log.v("test", comments[index].content)
            binding.commentRecyclerView.adapter?.notifyItemChanged(index)
        }
        binding.postTitle.text = originalPostData!!.title  // XML의 postTitle에 맞게 수정
        binding.postContent.text = originalPostData!!.content // XML의 postContent에 맞게 수정
    }

    // 채팅 걸기 함수
    private fun makeChat(post: Post, user: User) {
        // 요청 객체 생성
        val request = LikeRequest(
            post = post,
            user = user
        )

        request.post.createdAt = null
        request.post.user = null
        request.post.updatedAt = null
        request.user.createdAt = null
        request.user.updatedAt = null

        // API 호출
        val call = MyApplication.networkService.makeChat(request)
        call.enqueue(object : Callback<ChatRoom> {
            override fun onResponse(call: Call<ChatRoom>, response: Response<ChatRoom>) {
                if (response.isSuccessful) {
                    val chatRoom = response.body()
                    if (chatRoom != null) {
                        chatRoom.user.createdAt = null
                        chatRoom.user.updatedAt = null
                        chatRoom.user1.createdAt = null
                        chatRoom.user1.updatedAt = null
                        chatRoom.createdAt = null
                        chatRoom.updatedAt = null

                        // roomId를 채팅 페이지로 전송
                        val intent = Intent(this@PostDetailActivity, ChatActivity::class.java)
                        intent.putExtra("roomId", chatRoom.roomId.toString()) // roomId 전달
                        startActivity(intent) // 채팅 페이지로 이동
                    } else {
                        Toast.makeText(this@PostDetailActivity, "채팅방 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "채팅방 생성 실패"
                    Toast.makeText(this@PostDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChatRoom>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostDetailActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
