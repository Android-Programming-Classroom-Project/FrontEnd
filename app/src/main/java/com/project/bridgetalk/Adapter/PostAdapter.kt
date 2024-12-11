package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.R
import com.project.bridgetalk.model.vo.Post

class PostAdapter(private val posts: List<Post>, private val listener: OnPostClickListener) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val contentTextView: TextView = itemView.findViewById(R.id.content)
        private val likeCountTextView: TextView = itemView.findViewById(R.id.likeCount)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.createdAt)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(post: Post) {
            titleTextView.text = post.title
            contentTextView.text = post.content
            likeCountTextView.text = post.like_count.toString()
            createdAtTextView.text = post.updatedAt

            editButton.setOnClickListener {
                listener.onEditClick(post)
            }

            deleteButton.setOnClickListener {
                listener.onDeleteClick(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mypage_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    interface OnPostClickListener {
        fun onEditClick(post: Post)
        fun onDeleteClick(post: Post)
    }
}
