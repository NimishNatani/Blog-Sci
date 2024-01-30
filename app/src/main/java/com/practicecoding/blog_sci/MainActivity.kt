package com.practicecoding.blog_sci

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.adapter.BlogAdapter
import com.practicecoding.blog_sci.databinding.ActivityMainBinding
import com.practicecoding.blog_sci.model.BlogItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val blogdb = Firebase.firestore.collection("blogs")
    private val userdb = Firebase.firestore.collection("users")
    private val blogsItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfileImage(userId)
        }
        val recycleView = binding.blogrecyclerView
        val blogAdapter = BlogAdapter(blogsItems)
        val layoutManager = LinearLayoutManager(this)
//        layoutManager.reverseLayout = true
//        layoutManager.stackFromEnd = true
        recycleView.adapter = blogAdapter
        recycleView.layoutManager = layoutManager

        blogdb
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("TAG", "Failed with ${e.message}.")
                    return@addSnapshotListener
                }
                blogsItems.clear()
                snapshot?.let {
                    for (document in snapshot.documents) {
                        val blogItem = document.toObject(BlogItemModel::class.java)
                        if (blogItem != null) {
//                            Toast.makeText(this@MainActivity,"${blogItem.heading}",Toast.LENGTH_SHORT).show()
                            blogsItems.add(0, blogItem)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "No Blogs write Now",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
//                blogsItems.reverse()
                blogAdapter.notifyDataSetChanged()
            }

        binding.profileImage.setOnClickListener() {
            CoroutineScope(Dispatchers.IO).launch {
                launch(Dispatchers.Main) {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                }
                delay(100)
            }
        }
        binding.addBlogbtn.setOnClickListener() {
            startActivity(Intent(this, AddPostActivity::class.java))
        }
        binding.savearticleButton.setOnClickListener() {
            startActivity(Intent(this, SavedBlogActivity::class.java))
        }

    }

    private fun loadUserProfileImage(userId: String) {
        val userImage = userdb.document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val profileImage = document.get("photoUrl").toString()
                    if (profileImage != null) {

                        Glide.with(this@MainActivity)
                            .load(profileImage)
                            .into(binding.profileImage)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@MainActivity,
                    "Error in loading profile image ",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", "get failed with ", exception)
            }
    }
}