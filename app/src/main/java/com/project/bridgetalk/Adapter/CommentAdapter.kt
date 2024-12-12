package com.project.bridgetalk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.bridgetalk.MyApplication
import com.project.bridgetalk.PostDetailActivity
import com.project.bridgetalk.R
import com.project.bridgetalk.manage.UserManager
import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CommentAdapter(
    private val comments: MutableList<Comment>,private var originalData: MutableList<Comment>, private var token: String?
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.commentUserName)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentContent)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.commentDate)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        fun bind(comment: Comment) {
            usernameTextView.text = "익명" // 댓글 무조건 익명 표시
            commentTextView.text = comment.content
            createdAtTextView.text = comment.createdAt

            // 삭제 버튼 클릭 리스너 설정
            deleteButton.setOnClickListener {
                val user = UserManager.user?.copy()

                if (user != null) {
                    deleteComment(comment.commentId, user) // 댓글 ID와 사용자 정보를 통해 삭제
                } else {
                    Toast.makeText(itemView.context, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        private fun deleteComment(commentId: UUID, user: User) {
            val request = user
            request.updatedAt = null
            request.createdAt = null
            // 댓글 삭제 API 호출
            val call = MyApplication.networkService.deleteComment(commentId,token.toString(),request) // UUID를 사용하여 삭제 API 호출
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // 댓글 삭제 성공 시
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            comments.removeAt(position) // 리스트에서 댓글 제거
//                            originalData.clear()
//                            originalData.addAll(comments.map{it.copy()})
//                            (itemView.context as PostDetailActivity).updateOriginal()
                            notifyItemRemoved(position) // RecyclerView에 변경 사항 알림

                            Toast.makeText(itemView.context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                            (itemView.context as PostDetailActivity).updateOriginal()
                            // 댓글 수 업데이트
                            (itemView.context as PostDetailActivity).updateCommentCount()
                        }
                    } else {
                        // 오류 처리
                        val errorMessage = response.errorBody()?.string() ?: "댓글 삭제 실패"
                        Toast.makeText(itemView.context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 요청 실패 처리
                    Toast.makeText(itemView.context, "서버 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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