package com.project.bridgetalk

import TranslateViewModel
import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.project.bridgetalk.Adapter.PostViewAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.PostRecyclerviewBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.LikeRequest
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class PostListViewActivity : AppCompatActivity(), PostViewAdapter.OnItemClickListener {
    private lateinit var translateViewModel: TranslateViewModel
    private lateinit var binding: PostRecyclerviewBinding
    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    var copyPost: Boolean = false
    var originalData = mutableListOf<Post>()//원본 데이터로 만들기 위한 list
    var posts = mutableListOf<Post>() // 게시물 list
    private var selectedCategory: String? = null // 스피너의 선택된 카테고리 값을 저장할 변수
    private var searchQuery: String = "" // 검색어를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(com.project.bridgetalk.R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = com.project.bridgetalk.R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.project.bridgetalk.R.id.navigation_home -> {
                    // 현재 페이지이므로 아무 작업도 하지 않음
                    true
                }
                com.project.bridgetalk.R.id.navigation_chat -> {
                    val intent = Intent(this, ChatListActivity::class.java)
                    startActivity(intent)
                    true
                }
                com.project.bridgetalk.R.id.navigation_my -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // FloatingActionButton 이벤트 설정
        val fab2 = findViewById<FloatingActionButton>(com.project.bridgetalk.R.id.fab2)  // fab2는 XML에서 정의한 ID
        fab2.setOnClickListener {
            // 새 게시물 작성 (PostDetailActivity로 이동)
            val intent = Intent(this, PostMakeActivity::class.java)
            startActivity(intent)
        }



        // ViewModel 설정
        translateViewModel = ViewModelProvider(this).get(TranslateViewModel::class.java)

        // 스피너 항목 데이터 추가
        val categories = resources.getStringArray(com.project.bridgetalk.R.array.type_post_list)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 연결
        binding.categorySpinner.adapter = adapter

        // 기본값으로 첫 번째 항목 선택
        binding.categorySpinner.setSelection(0)

        // 스피너의 항목 선택 리스너 설정
        binding.categorySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    selectedCategory = categories[position]
                    fetchData() // 카테고리 변경 시 데이터 새로 가져오기
                } else {
                    // view가 null일 경우 기본값으로 처리
                    selectedCategory = categories[0] // 기본값으로 설정
                    fetchData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때의 처리
                selectedCategory = categories[0] // 기본값으로 설정
                fetchData()
            }

        })
        // 검색 아이콘 클릭 리스너 설정
        binding.searchIcon.setOnClickListener {
            // 입력된 검색어로 데이터 필터링
            searchQuery = binding.searchEditText.text.toString().trim()
            fetchData() // 검색어로 데이터 새로 가져오기
        }

        // 데이터를 가져오는 비동기 작업
        fetchData()

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
                translateButton.setImageResource(com.project.bridgetalk.R.drawable.outline_g_translate_24_blue)
                performTranslation(binding)
                Toast.makeText(this, "번역 활성화", Toast.LENGTH_SHORT).show()
            } else {
                revertTranslation(binding)
                // 번역 비활성화 상태: 기본 아이콘 변경
                translateButton.setBackgroundColor(Color.TRANSPARENT)
                translateButton.setImageResource(com.project.bridgetalk.R.drawable.outline_g_translate_24)
                Toast.makeText(this, "번역 비활성화", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 번역 수행 함수
    private fun performTranslation(binding: PostRecyclerviewBinding) {
        originalData.clear()
        originalData.addAll(posts.map { it.copy() })
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
                posts.forEachIndexed { index, post ->
                    val sourceText = post.content
                    val titleText = post.title
                    if (sourceText.isNotEmpty() && titleText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("test", translatedText)
                                post.content = translatedText
                                posts[index] = post
                                // 제목 번역
                                translator.translate(titleText)
                                    .addOnSuccessListener { translatedText1 ->
                                        post.title = translatedText1
                                        posts[index] = post
                                        binding.postView.adapter?.notifyItemChanged(index)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "제목 번역 실패", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
            }
    }


    // 번역 비활성화 시 원본 콘텐츠로 되돌리기
    private fun revertTranslation(binding: PostRecyclerviewBinding) {
        posts.clear()
        posts.addAll(originalData.map { it.copy() })
//        posts.forEachIndexed { index, post ->
//            Log.v("test",posts[index].content)
//            binding.postView.adapter?.notifyItemChanged(index)
//        }

        updateUI(posts)
    }

    private fun fetchData() {
        // UserManager.user에서 schoolId를 안전하게 가져오기
        val schoolId = UserManager.user?.copy()?.schools?.schoolId // nullable 타입

        // schoolId가 null인지 확인
        if (schoolId != null) {
        // API 호출
        val call = MyApplication.networkService.getAllPosts(schoolId) // API 호출
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    posts = response.body()?.toMutableList() ?: mutableListOf()
//                        (response.body()?.asSequence() ?: emptySequence()) as MutableList<Post> // Lazy Sequence

                    // 선택된 카테고리에 따라 필터링
                    val filteredPosts = when (selectedCategory) {
                        "홍보", "Promotion" -> posts.filter { it.type == "홍보" || it.type == "Promotion" }.toList().toMutableList()
                        "자유", "Free" -> posts.filter { it.type == "자유" || it.type == "Free" }.toList().toMutableList()
                        "인기", "Hot" -> posts.filter { it.like_count >= 10 }.toList().toMutableList()
                        else -> posts.toList().toMutableList() // "전체" 또는 "All"인 경우 모든 게시물
                    }
                    // 검색어가 있다면 추가적으로 필터링
                    val finalFilteredPosts = if (searchQuery.isNotBlank()) {
                        filteredPosts.filter { post ->
                            post.title.contains(searchQuery, ignoreCase = true) ||
                                    post.content.contains(searchQuery, ignoreCase = true)
                        }.toMutableList()
                    } else {
                        filteredPosts // 검색어가 없으면 필터링된 게시물 그대로 사용
                    }
                    updateUI(finalFilteredPosts.toMutableList()) // UI에 필터링된 게시물 전달

                    originalData = posts.map { it.copy() }.toMutableList()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@PostListViewActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostListViewActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        }
    }

    private fun updateUI(posts: MutableList<Post>) {
        // 레이아웃 매니저 설정 (한 번만 설정할 수 있습니다)
        if (binding.postView.layoutManager == null) {
            binding.postView.layoutManager = LinearLayoutManager(this)
        }

        // 어댑터가 이미 설정되어 있는 경우 데이터 업데이트
        val postAdapter = binding.postView.adapter as? PostViewAdapter
        if (postAdapter != null) {
            postAdapter.updateData(posts) // 기존 어댑터의 데이터 업데이트 메서드 호출
        } else {
            // 어댑터 설정 (처음 설정하는 경우)
            binding.postView.adapter = PostViewAdapter(posts, this)
        }
    }


    override fun onItemClick(postId: UUID) {
//        // 클릭된 게시물의 ID를 사용하여 다음 작업을 수행
//        Toast.makeText(this, "Clicked post ID: $postId", Toast.LENGTH_SHORT).show()
        // 상세 페이지로 이동하는 코드 추가
        val intent = Intent(this, PostDetailActivity::class.java)
        intent.putExtra("POST_ID", postId.toString()) // 게시물 ID를 Intent에 추가
        startActivity(intent) // 새로운 액티비티 시작
    }

    // 버튼 클릭 리스너 구현
    override fun onButtonClick(post: Post) {
        // UserManager.user에서 schoolId를 안전하게 가져오기
        val user = UserManager.user?.copy()

        if (user != null) {
            addLikedPost(post, user) // userId가 null이 아닐 때만 호출
        } else {
            // userId가 null인 경우에 대한 처리
            Toast.makeText(this, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addLikedPost(post: Post, user: User) {
        var user = user
        // LikeRequest 객체 생성
        val likeRequest = LikeRequest(
            post,
            user
        )

        likeRequest.post.createdAt = null
        likeRequest.post.user = null
        likeRequest.post.updatedAt = null
        likeRequest.user.createdAt = null
        likeRequest.user.updatedAt = null

        val call = MyApplication.networkService.addLikedPost(likeRequest) // POST API 호출
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    // 성공적으로 좋아요가 추가된 경우 처리
                    val updatedPost = response.body() // 서버로부터 변경된 Post 객체 받기
                    updatedPost?.let {
                        // 어댑터에서 현재 데이터를 가져옴
                        val postAdapter = binding.postView.adapter as? PostViewAdapter
                        val currentPosts = postAdapter?.getPosts() ?: return
                        // 원본 데이터 리스트에서 해당 포지션 찾기
                        val position = currentPosts.indexOfFirst { it.postId == post.postId }
                        if (position != -1) {
                            // 어댑터를 통해 특정 게시물 업데이트
                            postAdapter.updatePost(it, position)
                        }
                    }
                    Toast.makeText(this@PostListViewActivity, "좋아요가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    if (errorMessage.contains("이미 좋아요 누름")) {
                        // 이미 좋아요가 눌려있는 경우 좋아요 취소 API 호출
                        deleteLikedPost(post, user)
                    } else {
                        Toast.makeText(this@PostListViewActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostListViewActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Exception: ${t.message}", t)
            }
        })
    }

    private fun deleteLikedPost(post: Post, user: User) {
        // LikeRequest 객체 생성
        val likeRequest = LikeRequest(post, user)

        // 좋아요 취소 API 호출
        val deleteCall = MyApplication.networkService.deleteLikedPost(likeRequest) // DELETE API 호출
        deleteCall.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    // 좋아요가 성공적으로 취소된 경우
                    val updatedPost = response.body()
                    updatedPost?.let {
                        // 어댑터에서 현재 데이터를 가져옴
                        val postAdapter = binding.postView.adapter as? PostViewAdapter
                        val currentPosts = postAdapter?.getPosts() ?: return
                        // 원본 데이터 리스트에서 해당 포지션 찾기
                        val position = currentPosts.indexOfFirst { it.postId == post.postId }
                        if (position != -1) {
                            // 어댑터를 통해 특정 게시물 업데이트
                            postAdapter.updatePost(it, position)
                        }
                    }
                    Toast.makeText(this@PostListViewActivity, "좋아요가 취소되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@PostListViewActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostListViewActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}