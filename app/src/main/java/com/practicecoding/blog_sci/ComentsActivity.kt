package com.practicecoding.blog_sci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.adapter.BlogAdapter
import com.practicecoding.blog_sci.adapter.CommentAdapter
import com.practicecoding.blog_sci.databinding.ActivityComentsBinding
import com.practicecoding.blog_sci.model.BlogItemModel
import com.practicecoding.blog_sci.model.CommentModel
import com.practicecoding.blog_sci.model.Quadruple
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ComentsActivity : AppCompatActivity() {
    private val binding: ActivityComentsBinding by lazy {
        ActivityComentsBinding.inflate(layoutInflater)
    }
    private val commentList = mutableListOf<CommentModel>()

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val blogdb = Firebase.firestore.collection("blogs")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener() {
            finish()
        }
        val blogItem = intent.getParcelableExtra<BlogItemModel>("blogItem")
        if (blogItem != null) {
            binding.title.text = blogItem.heading
        } else {
            Toast.makeText(this, "Failed to load blog", Toast.LENGTH_LONG).show()
        }
        val currentUserId = currentUser?.uid.toString()
        val blogUserId = blogItem?.userId.toString()

        val recycleView = binding.commentRecyclerView
        val commentAdapter = CommentAdapter(commentList, this, blogUserId)
        val layoutManager = LinearLayoutManager(this)
        recycleView.adapter = commentAdapter
        recycleView.layoutManager = layoutManager

        val commentLinearLayout = binding.commentLinearLayout
        commentLinearLayout.orientation = LinearLayout.HORIZONTAL

        blogdb.document(blogItem?.time.toString()).collection("message")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("TAG", "Failed with ${e.message}.")
                    return@addSnapshotListener
                }
                commentList.clear()
                snapshot?.let {
                    for (document in snapshot.documents) {
                        val messageItem = document.toObject(CommentModel::class.java)
                        if (messageItem != null) {
                            commentList.add(messageItem)
                        } else {
                            Toast.makeText(
                                this@ComentsActivity,
                                "No Blogs write Now",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Toast.makeText(this@ComentsActivity,"${commentList.size}",Toast.LENGTH_LONG).show()
                }
//                blogsItems.reverse()
                commentAdapter.notifyDataSetChanged()
            }
        binding.send.setOnClickListener() {
            if (binding.comment.text.isEmpty()) {
                Toast.makeText(this, "Type some Comment", Toast.LENGTH_SHORT).show()
            } else {
                val comment = binding.comment.text.toString()
                val (hour, minute, second, millisecond) = getCurrentTime()
                val commentModel = CommentModel(
                    comment,
                    currentUser?.uid.toString(), "$hour:$minute"
                )
                blogdb.document(blogItem?.time.toString()).collection("message")
                    .document(SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(Date()).toString())
                    .set(commentModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            commentAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to add comment",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            }
        }
    }

    fun getCurrentTime(): Quadruple<Int, Int, Int, Int> {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val millisecond = cal.get(Calendar.MILLISECOND)
        return Quadruple(hour, minute, second, millisecond)
    }
}