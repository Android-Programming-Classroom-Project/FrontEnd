package com.project.bridgetalk

import NavActivity
import TranslateViewModel
import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
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

class PostListViewActivity : NavActivity() {
    private lateinit var translateViewModel: TranslateViewModel
    var translateState: Boolean = false // 번역 아이콘 활성화 위한 변수
    var copyPost: Boolean = false
    var originalData = mutableListOf<Post>()//원본 데이터로 만들기 위한 list
    var data = mutableListOf<Post>() // 게시물 list

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


        data.add(
            Post(
                postId = UUID.fromString("faf2435f-969f-460f-ba7e-471f97570b54"),
                user = null,
                schools = null,
                title = "title for test1",
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
                title = "title for test2",
                content = "Content for post 1",
                like_count = 10,
                createdAt = "2023-10-01",
                updatedAt = "2023-10-05",
                type = "general"
            )
        )
        //번역 원본 list 추후 게시물 접속시 서버에서 받은 게시물을 동일저장 만약 추가,삭제시에 동일하게 해줘야합니다
        originalData = data.map { it.copy() }.toMutableList()

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
    private fun performTranslation(binding: PostRecyclerviewBinding) {
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
                data.forEachIndexed { index, post ->
                    val sourceText = post.content
                    val titleText = post.title
                    if (sourceText.isNotEmpty() && titleText.isNotEmpty()) {
                        // 내용 번역
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                Log.v("test", translatedText)
                                post.content = translatedText
                                data[index] = post
                                // 제목 번역
                                translator.translate(titleText)
                                    .addOnSuccessListener { translatedText1 ->
                                        post.title = translatedText1
                                        data[index] = post
                                        binding.postView.adapter?.notifyItemChanged(index)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "제목 번역 실패", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "내용 번역 실패", Toast.LENGTH_SHORT).show()
                            }

//                        // 제목 번역
//                        translator.translate(titleText)
//                            .addOnSuccessListener { translatedText ->
//                                data[index] = post.copy(title = translatedText)
//                                binding.postView.adapter?.notifyItemChanged(index)
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this, "제목 번역 실패", Toast.LENGTH_SHORT).show()
//                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "번역에러", Toast.LENGTH_SHORT).show()
            }
    }


    // 번역 비활성화 시 원본 콘텐츠로 되돌리기
    private fun revertTranslation(binding: PostRecyclerviewBinding) {
        data.clear()
        data.addAll(originalData.map { it.copy() })
        data.forEachIndexed { index, post ->
            Log.v("test",data[index].content)
            binding.postView.adapter?.notifyItemChanged(index)
        }
    }
}