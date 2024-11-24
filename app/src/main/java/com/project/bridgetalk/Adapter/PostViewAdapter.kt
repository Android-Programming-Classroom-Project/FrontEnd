package com.project.bridgetalk.Adapter

import android.util.Log
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
//        // 데이터를 역순으로 매핑
//        val reversedDatas = datas.reversed()
//        val post = reversedDatas[position]
        val post = datas[position]

        binding.title.text = post.title
        binding.content.text = post.content
        binding.likeCount.text = post.like_count.toString()
        binding.createdAt.text = post.updatedAt.toString()

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

    fun updatePost(updatedPost: Post, position: Int) {
        // 특정 위치의 게시물을 업데이트
        if (position in datas.indices) { // 유효한 인덱스인지 확인
            datas[position] = updatedPost // 해당 위치에 새로운 게시물로 교체
            notifyItemChanged(position) // 해당 아이템만 갱신
        } else {
            Log.e("PostViewAdapter", "Invalid position: $position")
        }
    }

    // 데이터 리스트 가져오기
    fun getPosts(): MutableList<Post> {
        return datas
    }
}