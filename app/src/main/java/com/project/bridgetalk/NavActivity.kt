
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.bridgetalk.ChatListActivity
import com.project.bridgetalk.MyPageActivity
import com.project.bridgetalk.PostListViewActivity
import com.project.bridgetalk.R

open class NavActivity : AppCompatActivity() {

    protected lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_bar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        fab = findViewById(R.id.fab) // FloatingActionButton 초기화

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    if (this !is PostListViewActivity) {
                        navigateTo(PostListViewActivity::class.java, false)
                    }
                    true
                }
                R.id.navigation_chat -> {
                    if (this !is ChatListActivity) {
                        navigateTo(ChatListActivity::class.java, true)
                    }
                    true
                }
                R.id.navigation_my -> {
                    if (this !is MyPageActivity) {
                        navigateTo(MyPageActivity::class.java, false)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateTo(activity: Class<out AppCompatActivity>, showFab: Boolean) {
        val intent = Intent(this, activity)
        startActivity(intent)
        overridePendingTransition(0, 0) // 애니메이션 제거
        finish()
        // 화면 전환 후 FAB 상태를 설정
        setFabVisibility(showFab)
    }

    // FloatingActionButton 가시성 설정 메서드
    protected fun setFabVisibility(isVisible: Boolean) {
        if (::fab.isInitialized) {
            fab.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}