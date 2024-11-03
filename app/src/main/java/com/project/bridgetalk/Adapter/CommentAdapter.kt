package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.databinding.CommentItemBinding
import com.project.bridgetalk.model.vo.Comment

class CommentAdapter(
    private val comments: List<Comment>,
    private val onReplyClick: (Comment) -> Unit // 대댓글 버튼 클릭 시 처리할 함수
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentUserName.text = comment.userName
            binding.commentDate.text = comment.date
            binding.commentContent.text = comment.content
            binding.commentLikeCount.text = comment.likeCount.toString()

            // 대댓글 버튼 클릭 이벤트 설정
            binding.replyButton.setOnClickListener {
                onReplyClick(comment)
            }

            // 대댓글 RecyclerView 설정
            binding.replyRecyclerView.adapter = ReplyAdapter(comment.replies)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size
}