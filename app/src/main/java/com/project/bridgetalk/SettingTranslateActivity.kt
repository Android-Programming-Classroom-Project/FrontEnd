package com.project.bridgetalk

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.project.bridgetalk.Utill.SharedPreferencesUtil

class SettingTranslateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_translate)

        val sourceLangSpinner = findViewById<Spinner>(R.id.sourceLangSpinner)
        val targetLangSpinner = findViewById<Spinner>(R.id.targetLangSpinner)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // 언어 목록을 ArrayAdapter를 통해 Spinner에 설정
        val languages = resources.getStringArray(R.array.language_array_set_tran)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceLangSpinner.adapter = adapter
        targetLangSpinner.adapter = adapter

        // SharedPreferences에서 이전에 저장된 언어 설정 불러오기
        val (savedSourceLang, savedTargetLang) = SharedPreferencesUtil.loadTranslate(this)

        // Spinner의 기본 선택값을 저장된 언어로 설정
        sourceLangSpinner.setSelection(getLanguageIndex(savedSourceLang))
        targetLangSpinner.setSelection(getLanguageIndex(savedTargetLang))

        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener {
            val selectedSourceLang = sourceLangSpinner.selectedItem.toString()
            val selectedTargetLang = targetLangSpinner.selectedItem.toString()
            // 선택한 언어를 SharedPreferences에 저장
            SharedPreferencesUtil.saveTranslate(this, getLanguageCode(selectedSourceLang), getLanguageCode(selectedTargetLang))
            finish()
        }
    }

    // 언어 이름으로 인덱스를 찾는 함수 (예: "한국어" -> 0, "영어" -> 1)
    private fun getLanguageIndex(language: String?): Int {
        return when (language) {
            "ko" -> 0 // 한국어
            "en" -> 1 // 영어
            else -> 0
        }
    }

    // 언어 이름으로 언어 코드를 반환하는 함수 (예: "한국어" -> "ko", "영어" -> "en")
    private fun getLanguageCode(languageName: String): String {
        return when (languageName) {
            "한국어", "Korea" -> "ko" // 한국어 또는 korea
            "영어", "English" -> "en" // 영어 또는 english
            else -> "ko" // 기본값으로 한국어
        }
    }
}