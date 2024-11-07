package com.project.bridgetalk.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.ChatActivity
import com.project.bridgetalk.R
import com.project.bridgetalk.model.vo.ChatItem

class ChatAdapter(private val chatList: List<ChatItem>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)

        //채팅방 접속
        init {
            view.setOnClickListener {
                val context = view.context
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("chat", chatList[adapterPosition].roomId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.userName.text = chatItem.user.username
//        holder.lastMessage.text = chatItem.lastMessage
//        holder.profileImage.setImageResource(chatItem.profileImage)
    }

    override fun getItemCount(): Int = chatList.size
}
