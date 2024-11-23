package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.project.bridgetalk.databinding.PostItemBinding
import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.viewHolder.PostViewHolder
import java.util.UUID

class PostViewAdapter(
    val datas: MutableList<Post>,
    private val itemClickListener: OnItemClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(postId: UUID)
        fun onButtonClick(post: Post) // 버튼 클릭 리스너 추가
    }

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

        // 아이템 클릭 리스너 설정
        binding.root.setOnClickListener {
            itemClickListener.onItemClick(post.postId) // 클릭 시 게시물 ID 전달
        }

        // 버튼 클릭 리스너 설정
        holder.binding.likeButton.setOnClickListener {
            itemClickListener.onButtonClick(post) // 버튼 클릭 시 게시물 ID 전달
        }
    }

    // 데이터 업데이트 메서드
    fun updateData(newDatas: MutableList<Post>) {
        datas.clear() // 기존 데이터 비우기
        datas.addAll(newDatas) // 새로운 데이터 추가
        notifyDataSetChanged() // RecyclerView에 변경 사항 알림
    }
}