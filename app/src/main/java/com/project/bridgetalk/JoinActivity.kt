package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 뒤로가기 버튼 클릭 시 동작
        binding.toolbar.setNavigationOnClickListener {
            showAlertDialogWithCancel(
                "회원가입을 취소하시겠습니까?",
                onConfirm = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }

        //스타일 적용
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.school_names_join,    // string-array 사용
            R.layout.spinner_item    // 커스텀 레이아웃
        )
        binding.schoolSpinner.adapter = adapter

        binding.joinrequest.setOnClickListener {
            val email: String = binding.editTextTextEmailAddress.text.toString().trim()
            val username: String = binding.username.text.toString().trim()
            val password: String = binding.password.text.toString().trim()
            val myschoolName: String = binding.schoolSpinner.selectedItem.toString().trim()
            val universities = listOf(
                "Hanshin University" to "한신대학교",
                "Seoul National University" to "서울대학교",
                "Yonsei University" to "연세대학교",
                "Korea University" to "고려대학교",
                "Sungkyunkwan University" to "성균관대학교",
                "Hanyang University" to "한양대학교",
                "Ewha Womans University" to "이화여자대학교",
                "Chung-Ang University" to "중앙대학교",
                "Kyung Hee University" to "경희대학교",
                "Hankuk University of Foreign Studies" to "한국외국어대학교",
                "University of Seoul" to "서울시립대학교",
                "Konkuk University" to "건국대학교",
                "Dongguk University" to "동국대학교",
                "Inha University" to "인하대학교",
                "Pusan National University" to "부산대학교",
                "University of Ulsan" to "울산대학교",
                "Dankook University" to "단국대학교",
                "Chonnam National University" to "전남대학교",
                "Chonbuk National University" to "전북대학교",
                "Jeju National University" to "제주대학교",
                "Kangwon National University" to "강원대학교",
                "Pohang University of Science and Technology" to "포항공과대학교",
                "Kwangwoon University" to "광운대학교",
                "Soongsil University" to "숭실대학교",
                "Sejong University" to "세종대학교",
                "Kookmin University" to "국민대학교",
                "The Catholic University of Korea" to "가톨릭대학교",
                "Hongik University" to "홍익대학교",
                "Chungnam National University" to "충남대학교",
                "Chungbuk National University" to "충북대학교",
                "Kyungpook National University" to "경북대학교",
                "Daegu University" to "대구대학교",
                "Myongji University" to "명지대학교",
                "Sogang University" to "서강대학교",
                "Gachon University" to "가천대학교",
                "Donga University" to "동아대학교",
                "Chosun University" to "조선대학교",
                "Korea University of Technology and Education" to "한국기술교육대학교",
                "Kangnam University" to "강남대학교",
                "Handong Global University" to "한동대학교",
                "Sungui Women’s College" to "숭의여자대학교"
            )
            val schoolName = universities.find { it.first == myschoolName }?.second

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
            if (schoolName != null) {
                joinUser(email, username, password, role, schoolName)
            }
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
        }else if(!isValidEmail(email)){
            Toast.makeText(this@JoinActivity, "올바른 이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
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

    // 이메일 형식 검증 함수
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }
}