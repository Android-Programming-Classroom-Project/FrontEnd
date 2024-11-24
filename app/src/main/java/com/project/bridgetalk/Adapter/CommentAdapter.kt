package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.R
import com.project.bridgetalk.model.vo.Comment

class CommentAdapter(
    private val comments: MutableList<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.commentUserName)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentContent)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.commentDate)
        fun bind(comment: Comment) {
            usernameTextView.text = "익명" // 댓글 무조건 익명 표시
            commentTextView.text = comment.content
            createdAtTextView.text = comment.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size
}