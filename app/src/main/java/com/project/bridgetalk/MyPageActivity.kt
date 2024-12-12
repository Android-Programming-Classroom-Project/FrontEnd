package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.PostAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.MyPageBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class MyPageActivity : AppCompatActivity(), PostAdapter.OnPostClickListener {

    private lateinit var binding: MyPageBinding // ViewBinding 사용
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>() // 게시물 리스트
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageBinding.inflate(layoutInflater) // ViewBinding 초기화
        setContentView(binding.root) // ViewBinding의 루트를 사용하여 세팅
        token = SharedPreferencesUtil.getToken(this).toString()
        // 현재 페이지를 BottomNavigationView에서 선택 상태로 설정
        binding.bottomNavigation.selectedItemId = R.id.navigation_my

        // 사용자 데이터 설정
        val user = getUserData()
        if (user != null) {
            setUserData(user)
        }
        
        // 버튼 연결
        binding.logoutButton.setOnClickListener {
            // 버튼 클릭 시 수행할 작업
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        // RecyclerView 설정
        binding.postRecyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(posts, this)
        binding.postRecyclerView.adapter = postAdapter

        loadPosts() // 게시물 로드
        // 네비게이션 바 클릭 리스너 설정
        setupBottomNavigation()
    }

    private fun loadPosts() {
        // UserManager.user에서 schoolId를 안전하게 가져오기
        val user = getUserData()
        val schoolId = user?.schools?.schoolId // nullable 타입

        // schoolId가 null인지 확인
        if (schoolId != null) {
            // API 호출
            val call = MyApplication.networkService.getAllPosts(schoolId,
                SharedPreferencesUtil.getToken(this).toString()
            ) // API 호출
            call.enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        posts.clear()
                        response.body()?.let { allPosts ->
                            // 현재 사용자의 userId와 동일한 게시물만 필터링
                            val userPosts = allPosts.filter { post ->
                                post.user?.userId == user?.userId // 각 게시물의 userId를 확인
                            }
                            posts.addAll(userPosts) // 필터링된 게시물 추가
                        }
                        postAdapter.notifyDataSetChanged() // 어댑터 갱신
                    } else {
                        // 오류 처리
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@MyPageActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    // 요청 실패 처리
                    Toast.makeText(this@MyPageActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onEditClick(post: Post) {
        // PostEditActivity로 이동
        val intent = Intent(this, PostEditActivity::class.java)
        intent.putExtra("postId", post.postId.toString()) // Post ID를 전송
        intent.putExtra("postTitle", post.title) // Post Title을 전송
        intent.putExtra("postContent", post.content) // Post Content을 전송
        intent.putExtra("postType", post.type) // Post Type을 전송
        // 필요한 경우 다른 데이터도 추가
        startActivity(intent)
    }

    override fun onDeleteClick(post: Post) {
        val context = this // Replace with the appropriate context if used inside a fragment or adapter
        AlertDialog.Builder(context)
            .setMessage("해당 게시물을 삭제하시겠습니까?")
            .setPositiveButton("확인") { dialog, _ ->
                deletePost(post.postId, token.toString()) // 삭제 로직 호출
                dialog.dismiss() // 다이얼로그 닫기
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }
            .create()
            .show()
    }

    // 게시물 삭제 함수 (예시)
    private fun deletePost(postId: String) {
        // 삭제 로직 구현
        // 예: 서버 요청 또는 로컬 데이터베이스 삭제 처리
        println("게시물 $postId 삭제 완료")
    }

    private fun setUserData(user: User) {
        // TextView에 사용자 데이터 설정
        binding.usernameTextView.text = user.username
        binding.emailTextView.text = user.email
        binding.schoolsTextView.text = "${user.schools.schoolName}" // schools의 프로퍼티에 맞게 수정
        binding.roleTextView.text = "${user.role}"
        binding.createdAtTextView.text = "${user.createdAt}"
        binding.updatedAtTextView.text = "${user.updatedAt}"

        // 이미지 설정 (예시)
        binding.userImageView.setImageResource(R.drawable.rounded_account_circle_24) // 실제 사용자 이미지로 대체
    }

    private fun getUserData(): User? {
        // 사용자 데이터를 반환하는 함수
        return UserManager.user?.copy()
    }

    private fun deletePost(postId: UUID,token:String) {
        // 서버에 삭제 요청을 보내는 로직
        val call = MyApplication.networkService.deletePost(postId, token) // UUID를 사용하여 삭제 API 호출
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    posts.removeIf { it.postId == postId } // 로컬 리스트에서 게시물 삭제
                    postAdapter.notifyDataSetChanged() // 어댑터 갱신
                    Toast.makeText(this@MyPageActivity, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyPageActivity, "게시물 삭제 실패.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MyPageActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // 게시물 리스트 페이지로 이동
                    val intent = Intent(this, PostListViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_chat -> {
                    // 채팅 리스트 페이지로 이동
                    val intent = Intent(this, ChatListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_my -> {
                    // 현재 페이지이므로 아무 작업도 하지 않음
                    true
                }
                else -> false
            }
        }
    }
}