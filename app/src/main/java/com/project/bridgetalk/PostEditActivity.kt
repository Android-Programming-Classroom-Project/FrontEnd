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
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class PostEditActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar
    private lateinit var spinnerCategory: Spinner
    private var token : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_edit_page)

        token = SharedPreferencesUtil.getToken(this).toString()
        // UI 요소 초기화
        editTitle = findViewById(R.id.editTitle)
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView8)
        button = findViewById(R.id.button)
        toolbar = findViewById(R.id.toolbar)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Spinner에 카테고리 설정
        val categories = resources.getStringArray(com.project.bridgetalk.R.array.type_post_edit)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Toolbar 설정
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            // 뒤로가기 버튼 클릭 시 동작
            showAlertDialogWithCancel(
                "게시물 수정을 취소하시겠습니까?",
                onConfirm = {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }

        // Intent로부터 데이터 수신
        val postId = UUID.fromString(intent.getStringExtra("postId"))
        val postTitle = intent.getStringExtra("postTitle")
        val postContent = intent.getStringExtra("postContent")
        val postType = intent.getStringExtra("postType")

        // 수신한 데이터를 UI에 설정
        editTitle.setText(postTitle)
        editText.setText(postContent)
        if (postType != null) {
            setSpinnerSelectionByValue(spinnerCategory, postType)
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
                showAlertDialogWithCancel("게시물을 수정하시겠습니까?") {
                    // 게시물 수정처리 후 뷰 이동
                        postEdit(postId, title, content, selectedCategory)
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
    private fun postEdit(postId: UUID, title: String, content: String, selectedCategory: String) {
        val user = UserManager.user?.copy()
        if (user != null) {
            user.updatedAt = null
            user.createdAt = null
        }
        // LikeRequest 객체 생성
        val post = Post(
            postId = postId,
            user = user,
            schools = user?.schools,
            title = title,
            content = content,
            like_count = 0,
            type = selectedCategory,
            createdAt = null,
            updatedAt = null )

        // API 호출
        val call = MyApplication.networkService.editPost(token.toString(),post) // API 호출
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    // 게시물 작성 성공 시 처리
                    val intent = Intent(this@PostEditActivity, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@PostEditActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@PostEditActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSpinnerSelectionByValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                spinner.setSelection(i) // 일치하는 항목의 인덱스를 선택
                return
            }
        }
        // 만약 일치하는 항목이 없다면, 기본 선택을 설정할 수 있음
        spinner.setSelection(0) // 기본적으로 첫 번째 항목 선택
    }
}

