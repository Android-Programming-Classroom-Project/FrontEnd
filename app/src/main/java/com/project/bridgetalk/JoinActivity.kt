package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.bridgetalk.databinding.JoinPageBinding
import com.project.bridgetalk.model.vo.dto.request.JoinRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding = JoinPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarJoin)
        supportActionBar?.title = ""
        // 업 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.rounded_chevron_backward_24)


        //스타일 적용
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.school_names,    // string-array 사용
            R.layout.spinner_item    // 커스텀 레이아웃
        )
        binding.schoolSpinner.adapter = adapter

        binding.joinrequest.setOnClickListener {
            val email: String = binding.editTextTextEmailAddress.text.toString().trim()
            val username: String = binding.username.text.toString().trim()
            val password: String = binding.password.text.toString().trim()
            val schoolName: String = binding.schoolSpinner.selectedItem.toString().trim()
            // 선택된 RadioButton의 텍스트 가져오기
            val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            val role: String = if (selectedRadioButtonId != -1) {
                // 선택된 RadioButton이 있을 경우 텍스트 값 가져오기
                val radioButton = findViewById<RadioButton>(selectedRadioButtonId)
                radioButton.text.toString()
            } else {
                // 선택된 RadioButton이 없을 경우 처리 (예: 빈 값 또는 기본 값)
                ""
            }
            joinUser(email, username, password, role, schoolName)
        }
    }

    // 업 버튼 클릭 시 동작 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 현재 액티비티를 종료하고 이전 액티비티로 돌아갑니다.
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun joinUser(
        email: String,
        name: String,
        password: String,
        role: String,
        schoolName: String
    ) {
        // 회원가입 요청 데이터 생성
        var joinRequest: JoinRequest = JoinRequest(email, name, password, role, schoolName)

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || role.isEmpty() || schoolName.isEmpty()) {
            Toast.makeText(this@JoinActivity, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT)
                .show()
        } else {
            // 네트워크 요청 보내기
            val call = MyApplication.networkService.join(joinRequest)
            // 비동기 요청 처리
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string() ?: "No response body"
                            Log.v("성공test",response.body().toString().trim())
                            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val responseBody = response.errorBody()?.string() ?: "No response body"
                            Log.v("200응답 제외",responseBody.toString().trim())
                            Toast.makeText(this@JoinActivity,responseBody.trim(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.v("test","${t.message}")
                        Toast.makeText(
                            this@JoinActivity,
                            "서버 요청 실패: ${t.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        }
    }
}