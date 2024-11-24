package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class PostEditActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_edit_page)

        // UI 요소 초기화
        editTitle = findViewById(R.id.editTitle)
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView8)
        button = findViewById(R.id.button)
        toolbar = findViewById(R.id.toolbar)

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

        // 완료 버튼 클릭 시 동작
        button.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val content = editText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                // 제목과 내용이 비어 있으면 알림창 표시
                showAlertDialog("제목과 내용을 입력하세요.")
            } else {
                showAlertDialogWithCancel("게시물을 수정하시겠습니까?") {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    // 메시지를 화면에 보여주는 함수
    private fun showMessage(message: String) {
        textView.text = message
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
}

