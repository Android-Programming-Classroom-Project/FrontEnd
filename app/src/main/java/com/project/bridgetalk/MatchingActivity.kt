package com.project.bridgetalk

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MatchingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)

        // 툴바 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.title = "매칭 화면" // 툴바 제목 설정

        // 취소 버튼 이벤트 설정
        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish() // 액티비티 종료로 매칭 취소
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 툴바의 뒤로가기 버튼 클릭 시 액티비티 종료
        return true
    }
}
