package com.practicecoding.blog_sci.register

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.practicecoding.blog_sci.MainActivity
import com.practicecoding.blog_sci.R
import com.practicecoding.blog_sci.databinding.ActivityLoginBinding
import com.practicecoding.blog_sci.model.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    val uri =
        Uri.parse("https://api-private.atlassian.com/users/2143ab39b9c73bcab4fe6562fff8d23d/avatar");

    private var imageUri: Uri? = uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        val action = intent.getStringExtra("action")
        if (action == "login") {
            binding.loginButton.visibility = View.VISIBLE
            binding.loginMail.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.registerButton.isEnabled = false
            binding.registerButton.alpha = 0.5f

            binding.registerName.visibility = View.GONE
            binding.registerMail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.registerNewHere.isEnabled = false
            binding.registerNewHere.alpha = 0.5f
            binding.loginButton.setOnClickListener() {
                val loginEmail = binding.loginMail.text.toString()
                val loginPassword = binding.loginPassword.text.toString()
                if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_LONG).show()
                } else {
                    loginUser(loginEmail, loginPassword)
                }
            }


        } else {
            binding.loginButton.isEnabled = false
            binding.loginButton.alpha = 0.5f
            binding.registerButton.setOnClickListener() {
                val register_Name = binding.registerName.text.toString()
                val register_Email = binding.registerMail.text.toString()
                val register_Password = binding.registerPassword.text.toString()
                if (register_Name.isEmpty() || register_Email.isEmpty() || register_Password.isEmpty()) {
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_LONG).show()
                } else {
                    registerUser(register_Name, register_Email, register_Password)
                }


            }
            binding.cardView.setOnClickListener() {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select  an Image"),
                    PICK_IMAGE_REQUEST
                )
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = auth.currentUser
                        auth.signOut()
                        user?.let {
                            val userData = UserData(name, email)
                            db.collection("users").document(user.uid).set(userData)

//                            .addOnFailureListener{e->
//                                Log.e("failed","onCreate: Error ${e.message}")
//                            }
                            //upload image to firebase storage
                            val storageRef =
                                storage.reference.child("profile_image/${user.uid}.jpg")
                            storageRef.putFile(imageUri!!).addOnCompleteListener { task ->
                                storageRef.downloadUrl.addOnCompleteListener { imageUri ->
                                    val imageUri = imageUri.result.toString()
                                    db.collection("users").document(user.uid).update(
                                        mapOf(
                                            "photoUrl" to imageUri
                                        )
                                    )

                                }

                            }
                        }
                    }
                    Toast.makeText(this@LoginActivity, "Register Successfully 😍", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this@LoginActivity, WelcomeActivity::class.java))
                        finish()


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userImage)
        }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    Toast.makeText(this, "Login Successfully 👍", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Please enter Correct details 😫",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}