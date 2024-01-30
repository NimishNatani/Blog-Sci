package com.practicecoding.blog_sci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.databinding.ActivityEditBlogBinding
import com.practicecoding.blog_sci.model.BlogItemModel

class EditBlogActivity : AppCompatActivity() {
    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }
    private val blogdb = Firebase.firestore.collection("blogs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")
        binding.backbutton.setOnClickListener() {

            startActivity(Intent(this, MyBlogActivity::class.java))
        }
        binding.blogTitle.editText?.setText(blogItemModel?.heading)
        binding.blogDescription.editText?.setText(blogItemModel?.post)

        binding.editBlogBtn.setOnClickListener() {
            val updatedTitle = binding.blogTitle.editText?.text.toString().trim()
            val updatedDescription = binding.blogDescription.editText?.text.toString().trim()
            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                blogItemModel?.heading = updatedTitle
                blogItemModel?.post = updatedDescription
                blogItemModel?.likeUser=null
                blogItemModel?.likeCount=0
                if (blogItemModel != null) {
                    updateDataInFirebase(blogItemModel)
                }

            }

        }

    }

    private fun updateDataInFirebase(blogItemModel: BlogItemModel) {
        val postId = blogItemModel.time.toString()
        blogdb.document(postId).set(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog updated successful 😊", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Blog updated unsuccessful 😊", Toast.LENGTH_SHORT).show()

            }
    }
}