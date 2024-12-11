package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.R
import com.project.bridgetalk.model.vo.ChatMessage

class MessageAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    //viewType 결정  VIEW_TYPE_SENT or VIEW_TYPE_RECEIVED
    override fun getItemViewType(position: Int): Int {
//        if(messages[position].user?.userId.equals(UserManager.user?.userId)) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED

        return if (messages[position].isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.sentMessageText)
        private val timeTextView: TextView = itemView.findViewById(R.id.chatTime)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.content

            val createdAtString = message.createdAt.toString()
            if (createdAtString.length >= 16) {
                // "T"를 기준으로 날짜와 시간을 분리하고, 시간 부분만 추출하여 시, 분 포맷으로 변환
                val formattedTime = createdAtString.substring(11, 16) // HH:MM
                timeTextView.text = formattedTime
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.receivedMessageText)
        private val timeTextView: TextView = itemView.findViewById(R.id.chatTime)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.content

            val createdAtString = message.createdAt.toString()
            if (createdAtString.length >= 16) {
                // "T"를 기준으로 날짜와 시간을 분리하고, 시간 부분만 추출하여 시, 분 포맷으로 변환
                val formattedTime = createdAtString.substring(11, 16) // HH:MM
                timeTextView.text = formattedTime
            }
        }
    }
}
