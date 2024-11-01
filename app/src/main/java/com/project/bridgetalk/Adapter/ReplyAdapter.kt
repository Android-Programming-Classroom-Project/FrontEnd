package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.databinding.ReplyItemBinding
import com.project.bridgetalk.model.vo.Reply

class ReplyAdapter(
    private val replies: List<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    inner class ReplyViewHolder(private val binding: ReplyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply) {
            binding.replyUserName.text = reply.userName
            binding.replyDate.text = reply.date
            binding.replyContent.text = reply.content
            binding.replyLikeCount.text = reply.likeCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val binding = ReplyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bind(replies[position])
    }

    override fun getItemCount(): Int = replies.size
}