package com.project.bridgetalk

import TranslateViewModel
import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.project.bridgetalk.Adapter.PostViewAdapter
import com.project.bridgetalk.Utill.SharedPreferencesUtil
import com.project.bridgetalk.databinding.PostRecyclerviewBinding
import com.project.bridgetalk.model.vo.Post
import java.util.UUID

class PostListViewActivity: AppCompatActivity() {
    private lateinit var translateViewModel: TranslateViewModel
    var translateState :Boolean = false // 번역 아이콘 활성화 위한 변수
    private val originalContentMap = mutableMapOf<UUID, String>() //원본 저장
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = PostRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel 설정
        translateViewModel = ViewModelProvider(this).get(TranslateViewModel::class.java)

        // 스피너 항목 데이터 추가
        val categories = listOf("전체", "홍보", "자유")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

//         스피너에 어댑터 연결
        binding.categorySpinner.adapter = adapter

        val data = mutableListOf<Post>()
        data.add(
            Post(
                postId = UUID.fromString("faf2435f-969f-460f-ba7e-471f97570b54"),
                user = null,
                schools = null,
                title = "게시물 제목",
                content = "Content for post 1",
                like_count = 10,
                createdAt = "2023-10-01T00:00:00",
                updatedAt = "2023-10-05",
                type = "general"
            )
        )

        data.add(
            Post(
                postId = UUID.fromString("faf2435f-969f-460f-ba7e-471f97570b54"),
                user = null,
                schools = null,
                title = "게시물 제목",
                content = "Content for post 1",
                like_count = 10,
                createdAt = "2023-10-01",
                updatedAt = "2023-10-05",
                type = "general"
            )
        )

        binding.postView.layoutManager = LinearLayoutManager(this)
        binding.postView.adapter = PostViewAdapter(data)
        binding.postView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        // 번역 설정 이벤트 처리
        val settingTranslateButton = binding.settingTranslate
        settingTranslateButton.setOnClickListener{
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
                translateButton.setImageResource(com.project.bridgetalk.R.drawable.outline_g_translate_24_blue)
                performTranslation(binding)
                Toast.makeText(this, "번역 활성화", Toast.LENGTH_SHORT).show()
            } else {
                revertTranslation(binding)
                // 번역 비활성화 상태: 기본 아이콘 변경
                translateButton.setBackgroundColor(Color.TRANSPARENT)
                translateButton.setImageResource(com.project.bridgetalk.R.drawable.outline_g_translate_24)
                Toast.makeText(this, "번역 비활성화", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 번역 수행 함수
    private fun performTranslation(binding:PostRecyclerviewBinding) {
        val posts = (binding.postView.adapter as PostViewAdapter).datas
        val (sourceLanguage, targetLanguage) = SharedPreferencesUtil.loadTranslate(this)

        //세팅이 안되어있을 때 세팅페이지로 이동
        if (sourceLanguage.isNullOrEmpty() || targetLanguage.isNullOrEmpty()) {
            // 번역 설정으로 이동
            val intent = Intent(this, SettingTranslateActivity::class.java)
            startActivity(intent)
            return
        }

        posts.forEachIndexed { index, post ->
            val sourceText = post.content

            if (sourceText.isNotEmpty()) {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLanguage)
                    .setTargetLanguage(targetLanguage)
                    .build()
                val translator = Translation.getClient(options)
                //필요한 번역 모델이 기기에 다운로드되었는지 확인
                val conditions = DownloadConditions.Builder().build()
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                posts[index] = post.copy(content = translatedText)
                                binding.postView.adapter?.notifyItemChanged(index)
                                Log.v("test",posts[0].content)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"번역에러", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    // 번역 비활성화 시 원본 콘텐츠로 되돌리기
    private fun revertTranslation(binding: PostRecyclerviewBinding) {
        val posts = (binding.postView.adapter as PostViewAdapter).datas
        val (sourceLanguage, targetLanguage) = SharedPreferencesUtil.loadTranslate(this)

        //세팅이 안되어있을 때 세팅페이지로 이동
        if (sourceLanguage.isNullOrEmpty() || targetLanguage.isNullOrEmpty()) {
            // 번역 설정으로 이동
            val intent = Intent(this, SettingTranslateActivity::class.java)
            startActivity(intent)
            return
        }

        posts.forEachIndexed { index, post ->
            val sourceText = post.content

            if (sourceText.isNotEmpty()) {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(targetLanguage)
                    .setTargetLanguage(sourceLanguage)
                    .build()
                val translator = Translation.getClient(options)
                //필요한 번역 모델이 기기에 다운로드되었는지 확인
                val conditions = DownloadConditions.Builder().build()
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                posts[index] = post.copy(content = translatedText)
                                binding.postView.adapter?.notifyItemChanged(index)
                                Log.v("test",posts[0].content)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"번역에러", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}