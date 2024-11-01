package com.project.bridgetalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.bridgetalk.databinding.MyPageBinding
import com.project.bridgetalk.model.vo.Schools
import com.project.bridgetalk.model.vo.User



class MyPageActivity : AppCompatActivity() {

    // ViewBinding 생성
    private lateinit var binding: MyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageBinding.inflate(layoutInflater) // ViewBinding 초기화
        setContentView(binding.root) // ViewBinding의 루트를 사용하여 세팅

        // 사용자 데이터 설정
        val user = getUserData()
        setUserData(user)
    }

    private fun setUserData(user: User) {
        // TextView에 사용자 데이터 설정
        binding.usernameTextView.text = user.username
        binding.emailTextView.text = user.email
        binding.schoolsTextView.text = "학교: ${user.schools.schoolName}" // schools의 프로퍼티에 맞게 수정
        binding.roleTextView.text = "역할: ${user.role}"
        binding.createdAtTextView.text = "생성일: ${user.createdAt}"
        binding.updatedAtTextView.text = "업데이트일: ${user.updatedAt}"

        // 이미지 설정 (예시)
        binding.userImageView.setImageResource(R.drawable.rounded_account_circle_24) // 실제 사용자 이미지로 대체
    }

    private fun getUserData(): User {
        // 사용자 데이터를 반환하는 예시 함수 (실제 구현에 맞게 수정)
        return User(
            userId = "1",
            username = "사용자 이름",
            email = "user@example.com",
            schools = Schools("Sample University", schoolName = "한신대"),
            role = "학생",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-31"
        )
    }
}
