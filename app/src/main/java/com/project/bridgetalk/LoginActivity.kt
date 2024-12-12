package com.project.bridgetalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.LoginPageBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.User
import com.project.bridgetalk.model.vo.dto.request.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding = LoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        // 업 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.rounded_chevron_backward_24)

        //로그인 요청 버튼
        val loginButton = findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, PostListViewActivity::class.java)
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
//                startActivity(intent)
//                finish()
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //회원가입 버튼
        val joinButton = findViewById<Button>(R.id.join)
        joinButton.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
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

    private fun loginUser(email: String, password: String) {
        // 로그인 요청 데이터 생성
        val request = LoginRequest(email, password)
        // 네트워크 요청 보내기
        val call = MyApplication.networkService.login(request)
        // 비동기 요청 처리x
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    Log.v("test", response.code().toString().trim())
                    val loginResponse = response.body()
                    val token = response.headers()["Authorization"].toString().trim()
                    Log.v("로그인 성공시 응답 정보", loginResponse.toString().trim())
                    Log.v("로그인 성공시 응답 정보", token)
                    //access토큰저장
                    SharedPreferencesUtil.saveToken(this@LoginActivity,token)

                    Log.v("test",loginResponse.toString().trim())
                    UserManager.user = loginResponse

                    val intent = Intent(this@LoginActivity, PostListViewActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}