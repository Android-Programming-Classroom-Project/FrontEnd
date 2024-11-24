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
    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    private val comments = mutableListOf<Comment>() // 예시 데이터, 실제 데이터로 교체해야 함
    private lateinit var postId: UUID // 게시물 ID를 저장할 변수
    private lateinit var postComment: PostCommentDTO // 전역 변수로 게시물 객체 선언
    // 번역 위한 배열
    var originalData = mutableListOf<Comment>()

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

        // Intent로부터 게시물 ID를 받기
        postId = UUID.fromString(intent.getStringExtra("POST_ID"))

        // 게시물 상세 정보 로드
        loadPostDetails(postId)

        originalData = comments.map { it.copy() }.toMutableList()
//        Log.v("test", originalData[0].content)
        // 번역 설정 이벤트 처리

        val settingTranslateButton = binding.settingTranslate
        settingTranslateButton.setOnClickListener {
            val intent = Intent(this, SettingTranslateActivity::class.java)
            startActivity(intent)
        }

//        translate button
        val translateButton: ImageButton = binding.translate
        // 번역 버튼 클릭 리스너
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

//        // 댓글 등록 버튼 클릭 리스너
//        binding.commentButton.setOnClickListener {
//            val commentText = binding.commentEditText.text.toString()
//            if (commentText.isNotBlank()) {
//                // 댓글 추가 및 RecyclerView 갱신
//                addComment(commentText)
//                binding.commentEditText.text.clear()
//            }
//        }
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
                        // 댓글 리스트 업데이트 (null 체크 추가)
                        comments.clear()
                        originalData.clear()

                        postCommentDTO.commentList?.let {
                            comments.addAll(it) // 댓글 리스트가 null이 아닐 경우 추가
                        } ?: run {
                            Log.w("PostDetailActivity", "Comment list is null, using empty list.")
                        }
                        originalData = comments.map { it.copy()}.toMutableList()
                        commentAdapter.notifyDataSetChanged() // 어댑터 갱신
                    }
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
                comments.forEachIndexed { index, comment ->
                    val sourceText = comment.content
//                    val titleText = post.title
                    if (sourceText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("번역기", translatedText)
                                comment.content = translatedText
                                comments[index] = comment
//                                binding.commentRecyclerView.adapter?.notifyItemChanged(index)
                                // 제목 번역
//                                translator.translate(titleText)
//                                    .addOnSuccessListener { translatedText1 ->
//                                        post.title = translatedText1
//                                        data[index] = post
//                                        binding.commentRecyclerView.adapter?.notifyItemChanged(index)
//                                    }
//                                    .addOnFailureListener {
//                                        Toast.makeText(this, "제목 번역 실패", Toast.LENGTH_SHORT).show()
//                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                            }

//                        // 제목 번역
//                        translator.translate(titleText)
//                            .addOnSuccessListener { translatedText ->
//                                data[index] = post.copy(title = translatedText)
//                                binding.postView.adapter?.notifyItemChanged(index)
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this, "제목 번역 실패", Toast.LENGTH_SHORT).show()
//                            }
                    }
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
    }
}
