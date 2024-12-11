package com.project.bridgetalk.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.ChatActivity
import com.project.bridgetalk.R
import com.project.bridgetalk.model.vo.ChatItem


class ChatAdapter(private var chatList: MutableList<ChatItem>, private val onChatOutClick: (ChatItem) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val chatOutButton: ImageButton = itemView.findViewById(R.id.chatOutButton)
        val chatTime: TextView = view.findViewById(R.id.chatTime)



        //채팅방 접속
        init {
            view.setOnClickListener {
                val context = view.context
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("roomId", chatList[adapterPosition].roomId.toString())
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
        val createdAtString = chatItem.createdAt.toString()
        val formattedTime = createdAtString.substring(0, 16).replace("T", " ")


//        holder.userName.text = chatItem.user.username
        holder.lastMessage.text = chatItem.lastMessage
        holder.chatTime.text = formattedTime
//        holder.profileImage.setImageResource(chatItem.profileImage)
        // 버튼 클릭 이벤트 설정
        holder.chatOutButton.setOnClickListener {
            onChatOutClick(chatItem) // 클릭한 아이템을 전달
        }
    }

    override fun getItemCount(): Int = chatList.size

    fun updateChatList(newChatList: MutableList<ChatItem>) {
        chatList = newChatList
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        chatList.removeAt(position) // 리스트에서 아이템 삭제
        notifyItemRemoved(position) // 어댑터에 삭제 알림
    }
}
