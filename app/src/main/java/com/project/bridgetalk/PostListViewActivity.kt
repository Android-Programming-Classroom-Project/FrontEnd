package com.project.bridgetalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.bridgetalk.Adapter.PostViewAdapter
import com.project.bridgetalk.databinding.PostRecyclerviewBinding
import com.project.bridgetalk.model.vo.Post

class PostListViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = PostRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = mutableListOf<Post>()
        data.add(
            Post(
                postId = "1",
                user = null,
                schools = null,
                title = "게시물 제목",
                content = "Content for post 1",
                like_count = 10,
                createdAt = "2023-10-01",
                updatedAt = "2023-10-05"))

        data.add(
            Post(
                postId = "2",
                user = null,
                schools =null,
                title = "Title 2",
                content = "Content for post 2",
                like_count = 20,
                createdAt = "2023-10-02",
                updatedAt = "2023-10-06"
            )
        )

        binding.postView.layoutManager = LinearLayoutManager(this)
        binding.postView.adapter = PostViewAdapter(data)
        binding.postView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
    }
}