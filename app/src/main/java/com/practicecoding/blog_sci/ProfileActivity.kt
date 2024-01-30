package com.practicecoding.blog_sci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.databinding.ActivityProfileBinding
import com.practicecoding.blog_sci.register.WelcomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private val binding:ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private val auth = FirebaseAuth.getInstance()
    private val userdb = Firebase.firestore.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.addNewArticle.setOnClickListener(){
            startActivity(Intent(this,AddPostActivity::class.java))
        }
        binding.logOut.setOnClickListener(){
            auth.signOut()
//            startActivity(Intent(this,WelcomeActivity::class.java))
//            finish()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
        binding.myBlogsButton.setOnClickListener(){
            CoroutineScope(Dispatchers.IO).launch {
                delay(200)
            launch(Dispatchers.Main){
            startActivity(Intent(this@ProfileActivity,MyBlogActivity::class.java))}}
        }
        val userId = auth.currentUser?.uid
        if(userId != null){
            userdb.document(userId).get()
                .addOnSuccessListener {document ->
                    if(document.exists()){
                        binding.textView10.text = document.get("name").toString()
                        val imageUrl = document.get("photoUrl").toString()
                        if(imageUrl != null){
                            Glide.with(this)
                                .load(imageUrl)
                                .into(binding.imageView2)
                        }
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to load user data 😫",Toast.LENGTH_SHORT).show()
                }
        }

    }
}