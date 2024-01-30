package com.practicecoding.blog_sci.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.practicecoding.blog_sci.R
import com.practicecoding.blog_sci.model.CommentModel

class CommentAdapter(private val messages: MutableList<CommentModel>,val context: Context,val blogerId:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.sender_comment, parent, false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.receiver_comment, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SENT -> {
                val sentViewHolder = holder as SentMessageViewHolder
                sentViewHolder.bind(message)
            }
            VIEW_TYPE_RECEIVED -> {
                val receivedViewHolder = holder as ReceivedMessageViewHolder
                receivedViewHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
Toast.makeText(context,messages[position].id,Toast.LENGTH_LONG).show()
        return if (blogerId==messages[position].id) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    // ViewHolder for sent messages
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderMsg: TextView = itemView.findViewById(R.id.sendermessage)
        private val senderTime:TextView = itemView.findViewById(R.id.sendertime)

        fun bind(message: CommentModel) {
            senderMsg.text = message.comment
            senderTime.text = message.time
        }
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiverMsg: TextView = itemView.findViewById(R.id.receivermessage)
        private val receiverTime:TextView =itemView.findViewById(R.id.receivertime)

        fun bind(message: CommentModel) {
            receiverMsg.text = message.comment
            receiverTime.text = message.time
        }
    }
}