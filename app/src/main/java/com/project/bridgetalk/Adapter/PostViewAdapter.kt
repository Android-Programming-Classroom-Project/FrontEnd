package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.project.bridgetalk.databinding.PostItemBinding
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.viewHolder.PostViewHolder

class PostViewAdapter(val datas: MutableList<Post>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  =
        PostViewHolder(PostItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = (holder as PostViewHolder).binding
        // 데이터를 역순으로 매핑
        val reversedDatas = datas.reversed()
        val post = reversedDatas[position]

        binding.title.text = post.title
        binding.content.text = post.content
        binding.likeCount.text = post.like_count.toString()
        binding.createdAt.text = post.createdAt.toString()
    }

}