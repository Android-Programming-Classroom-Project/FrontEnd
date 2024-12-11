package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.LikeRequest
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class PostMakeActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar
    private lateinit var spinnerCategory: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_make_page)

        // UI 요소 초기화
        editTitle = findViewById(R.id.editTitle)
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView8)
        button = findViewById(R.id.button)
        toolbar = findViewById(R.id.toolbar)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Spinner에 카테고리 설정
        val categories = resources.getStringArray(com.project.bridgetalk.R.array.type_post_make)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        spinnerCategory.setSelection(0)

        // Toolbar 설정
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            // 뒤로가기 버튼 클릭 시 동작
            showAlertDialogWithCancel(
                "게시물 작성을 취소하시겠습니까?",
                onConfirm = {
                    val intent = Intent(this, PostListViewActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }

        // 완료 버튼 클릭 시 동작
        button.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val content = editText.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString() // 선택된 카테고리 가져오기

            if (title.isEmpty() || content.isEmpty()) {
                // 제목과 내용이 비어 있으면 알림창 표시
                showAlertDialog("제목과 내용을 입력하세요.")
            } else {
                // 게시물이 작성되었습니다 알림창 표시
                showAlertDialogWithCancel("게시물을 작성하시겠습니까?") {
                    // 게시물 작성처리 후 뷰 이동
                    val user = UserManager.user?.copy()

                    if (user != null) {
                        postMake(title, content, selectedCategory, user) // userId가 null이 아닐 때만 호출
                    } else {
                        // userId가 null인 경우에 대한 처리
                        Toast.makeText(this, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    // 기본 알림창
    private fun showAlertDialog(message: String, onConfirm: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                onConfirm?.invoke()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // 확인 및 취소 버튼이 있는 알림창
    private fun showAlertDialogWithCancel(message: String, onConfirm: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                onConfirm?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()  // 취소 버튼 클릭 시 알림창 닫기
            }
            .setCancelable(false)
            .show()
    }
    // 게시물 작성 통신 함수
    private fun postMake(title: String, content: String, selectedCategory: String, user: User) {
        var u = user
        u.updatedAt = null
        u.createdAt = null
        // LikeRequest 객체 생성
        val likeRequest = LikeRequest(
            post = Post(
                postId = UUID.randomUUID(),
                user = u,
                schools = u.schools,
                title = title,
                content = content,
                like_count = 0,
                type = selectedCategory,
                createdAt = null,
                updatedAt = null ),
            user = u
        )

        // API 호출
        val call = MyApplication.networkService.makePost(likeRequest) // API 호출
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    // 게시물 작성 성공 시 처리
                    val intent = Intent(this@PostMakeActivity, PostListViewActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@PostMakeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostMakeActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

