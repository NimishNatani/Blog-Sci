package com.practicecoding.blog_sci

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.adapter.MyBlogAdapter
import com.practicecoding.blog_sci.databinding.ActivityMyBlogBinding
import com.practicecoding.blog_sci.model.BlogItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBlogActivity : AppCompatActivity() {
    private val binding: ActivityMyBlogBinding by lazy {
        ActivityMyBlogBinding.inflate(layoutInflater)
    }
    private val blogdb = Firebase.firestore.collection("blogs")
    private val userdb = Firebase.firestore.collection("users")
    private val currentUser = FirebaseAuth.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val savedBlogPost = mutableListOf<BlogItemModel>()
    private lateinit var savedUser: List<String>
    private lateinit var blogAdapter: MyBlogAdapter
    private val EDIT_BLOG_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backbutton.setOnClickListener(){
            finish()
        }
        val userId = auth.currentUser?.uid
        val recycleView = binding.blogRecyclerView
        recycleView.layoutManager = LinearLayoutManager(this)
        blogAdapter = MyBlogAdapter(this, emptyList(), object : MyBlogAdapter.OnItemClickListener {
            override fun onEdit(blogItem: BlogItemModel) {
                val intent = Intent(this@MyBlogActivity, EditBlogActivity::class.java)
                intent.putExtra("blogItem", blogItem)
                finish()
                this@MyBlogActivity.startActivityForResult(intent,EDIT_BLOG_REQUEST_CODE)

            }

            override fun onRead(blogItem: BlogItemModel) {
                val intent = Intent(this@MyBlogActivity, BlogReadActivity::class.java)
                intent.putExtra("blogItem", blogItem)
                this@MyBlogActivity.startActivity(intent)
            }

            override fun onDelete(blogItem: BlogItemModel) {
                deleteBlogPost(blogItem)
            }
        }
        )
        recycleView.adapter = blogAdapter


        CoroutineScope(Dispatchers.IO).launch {
            userdb.document(userId.toString())
                .addSnapshotListener() { snapshot, e ->
                    if (e != null) {
                        Log.e("TAG", "Failed with ${e.message}.")
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        savedUser = (snapshot.get("myBlog") as? MutableList<String>
                            ?: emptyList()).toMutableList()
                    }
                }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            for (user in savedUser) {
                blogdb.document(user).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val blogItem = document.toObject(BlogItemModel::class.java)
                            if (blogItem != null) {
                                savedBlogPost.add(0, blogItem)
//                                launch(Dispatchers.Main) {
//                                    Toast.makeText(
//                                        this@MyBlogActivity,
//                                        "${savedBlogPost.size}",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
                            }
                        }
                    }
            }
            launch(Dispatchers.Main) {
//                Toast.makeText(this@MyBlogActivity,"${savedBlogPost.size}",Toast.LENGTH_SHORT).show()
                delay(500)
                binding.progressBar.visibility = View.GONE

                if (savedBlogPost.size > 0) {
                    blogAdapter.setData(savedBlogPost)
                } else {
                    binding.noBlogCard.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        val postId = blogItem.time
        blogdb.document(postId.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Blog delete successfully 👍", Toast.LENGTH_SHORT).show()
                savedBlogPost.remove(blogItem)
                blogAdapter.setData(savedBlogPost)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Blog delete task unsuccessful 😫", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_BLOG_REQUEST_CODE && requestCode== Activity.RESULT_OK){

        }
    }
}