package com.practicecoding.blog_sci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.adapter.BlogAdapter
import com.practicecoding.blog_sci.databinding.ActivitySavedBlogBinding
import com.practicecoding.blog_sci.model.BlogItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SavedBlogActivity : AppCompatActivity() {
    private val binding: ActivitySavedBlogBinding by lazy {
        ActivitySavedBlogBinding.inflate(layoutInflater)
    }
    private val savedBlogPost = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter
    private val userdb = Firebase.firestore.collection("users")
    private val blogdb = Firebase.firestore.collection("blogs")
    private val auth = FirebaseAuth.getInstance()
    private lateinit var savedUser: MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val currentUserId = auth.currentUser?.uid
        blogAdapter = BlogAdapter(savedBlogPost)
        val recyclerView = binding.savedblogrecycler
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            userdb.document(currentUserId.toString())
                .addSnapshotListener() { snapshot, e ->
                    if (e != null) {
                        Log.e("TAG", "Failed with ${e.message}.")
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        savedUser = (snapshot.get("savePost") as? MutableList<String>
                            ?: emptyList()).toMutableList()
                    }
                }
        }
        blogAdapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.IO).launch {
            delay(300)
            for (user in savedUser) {
                blogdb.document(user).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val blogItem = document.toObject(BlogItemModel::class.java)
                            if (blogItem != null) {
                                savedBlogPost.add(blogItem)
                            }
                        }
                        binding.progressBar.visibility = View.GONE
                        if (savedUser.size==0){
                            binding.noBlogCard.visibility = View.VISIBLE
                        }
                        blogAdapter.notifyDataSetChanged()
                    }
            }
        }
        binding.backbutton.setOnClickListener() {
            finish()
        }
    }
}