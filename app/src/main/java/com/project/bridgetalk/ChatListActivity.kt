package com.project.bridgetalk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.project.bridgetalk.Adapter.ChatAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.ActivityChatListBinding
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.ChatItem
import com.project.bridgetalk.model.vo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ChatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatListBinding
    var chatList = mutableListOf<ChatItem>() // 채팅목록 담는 배열
    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    var originalData = mutableListOf<ChatItem>()//원본 데이터로 만들기 위한 list
    private lateinit var chatAdapter: ChatAdapter
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = UserManager.user?.copy()
        token = SharedPreferencesUtil.getToken(this).toString()
        // BottomNavigationView 초기화 및 현재 페이지 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_chat

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, PostListViewActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_chat -> {
                    // 현재 페이지, 아무 작업도 하지 않음
                    true
                }

                R.id.navigation_my -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // FloatingActionButton 이벤트 설정
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // 새 채팅 시작 (MatchingActivity로 이동)
            val intent = Intent(this, MatchingActivity::class.java)
            startActivity(intent)
        }

        chatAdapter = ChatAdapter(chatList){ item ->
            // 채팅 삭제 처리
            val roomId = item.roomId
            if (roomId != null) {
                deleteChat(roomId)
            }
        }
        recyclerView.adapter = chatAdapter
        if (user != null) {
            chatListFind(user)
        }
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
                translateButton.setImageResource(R.drawable.outline_g_translate_24_blue)
                performTranslation(binding)
                Toast.makeText(this, "번역 활성화", Toast.LENGTH_SHORT).show()
            } else {
                revertTranslation(binding)
                // 번역 비활성화 상태: 기본 아이콘 변경
                translateButton.setBackgroundColor(Color.TRANSPARENT)
                translateButton.setImageResource(R.drawable.outline_g_translate_24)
                Toast.makeText(this, "번역 비활성화", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 번역 수행 함수
    private fun performTranslation(binding: ActivityChatListBinding) {
        originalData.clear()
        originalData.addAll(chatList.map { it.copy() })
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
                chatList.forEachIndexed { index, chat ->
                    val sourceText = chat.lastMessage ?: ""
//                    val titleText = post.title
                    if (sourceText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("test", translatedText)
                                chat.lastMessage = translatedText
                                chatList[index] = chat
                                binding.recyclerView.adapter?.notifyItemChanged(index)
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
    private fun revertTranslation(binding: ActivityChatListBinding) {
        chatList.clear()
        chatList.addAll(originalData.map { it.copy() })
        chatList.forEachIndexed { index, chat ->
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun chatListFind(user: User) {
        var u = user
        u.createdAt = null
        u.updatedAt = null
        val call = MyApplication.networkService.selectChatList(token.toString(),u)
        call.enqueue(object : Callback<List<ChatItem>> {
            override fun onResponse(
                call: Call<List<ChatItem>>,
                response: Response<List<ChatItem>>
            ) {
                if (response.isSuccessful) {
                    val chats = response.body()?.toMutableList() ?: mutableListOf()
                    Log.v("test", chats.toString())
                    chats.let {
                        val chatAdapter = binding.recyclerView.adapter as? ChatAdapter
                        chatAdapter?.updateChatList(chats)
                        chatList = chats
                    }
                    originalData = chatList.map { it.copy() }.toMutableList()

                } else {
                    // 오류 처리
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@ChatListActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ChatItem>>, t: Throwable) {
                // 요청 실패 처리
                Toast.makeText(this@ChatListActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.v("test", t.message.toString())
            }
        })
    }

    private fun deleteChat(roomId: UUID) {
        // 서버에 삭제 요청을 보내는 로직
        val call = MyApplication.networkService.deleteChat(roomId) // UUID를 사용하여 삭제 API 호출
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val position = chatList.indexOfFirst { it.roomId == roomId }
                    if (position != -1) {
                        chatAdapter.removeItem(position) // ChatAdapter의 removeItem 메서드 호출
                        originalData = chatList.map { it.copy() }.toMutableList() // 깊은 복사
                    }
                    Toast.makeText(this@ChatListActivity, "채팅방이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChatListActivity, "채팅방 삭제 실패.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatListActivity, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}