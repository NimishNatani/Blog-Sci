package com.practicecoding.blog_sci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.practicecoding.blog_sci.databinding.ActivityAddPostBinding
import com.practicecoding.blog_sci.model.BlogItemModel
import com.practicecoding.blog_sci.notification.NotificationData
import com.practicecoding.blog_sci.notification.PushNotification
import com.practicecoding.blog_sci.notification.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

const val TOPIC = "/topics/myTopic"
class AddPostActivity : AppCompatActivity() {
    private val binding: ActivityAddPostBinding by lazy {
        ActivityAddPostBinding.inflate(layoutInflater)
    }
    private val userdb = Firebase.firestore
    val currentDatefire = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date())
    private val blogdb = Firebase.firestore.collection("blogs").document(currentDatefire)

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        binding.backbutton.setOnClickListener() {
            finish()
        }
        binding.addPostBtn.setOnClickListener() {
            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please Fill all the fiels", Toast.LENGTH_LONG).show()
            } else {
                val user: FirebaseUser? = auth.currentUser

                if (user != null) {
                    val userId = user.uid
                    val userName = user.displayName ?: "Anonymous"
                    val userImageUrl = user.photoUrl ?: ""
                    userdb.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userNameFromDB = document.get("name").toString()
                                val userImageUrlFromDB = document.get("photoUrl").toString()
                                var myPost =
                                    (document.get("myBlog") as? MutableList<String> ?: emptyList())
                                var mutablePost = myPost.toMutableList()
                                mutablePost.add(currentDatefire)
                                userdb.collection("users").document(userId)
                                    .update(mapOf("myBlog" to mutablePost))
                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                val blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB,
                                    currentDatefire,
                                    auth.currentUser?.uid

                                    )

                                blogdb.set(blogItem)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Failed to add blog",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }

                }
                PushNotification(
                    NotificationData(title,description),
                    TOPIC
                ).also {
                    pushNotification(it)
                }

            }
        }
    }
    private fun pushNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
//                Log.d("notification","Response: ${Gson().toJson(response)}")
            }else{
                Log.d("notification",response.errorBody().toString())

            }
        }catch (e:Exception){
            Log.e("notification",e.toString())
        }
    }
}