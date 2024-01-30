package com.practicecoding.blog_sci.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.BlogReadActivity
import com.practicecoding.blog_sci.ComentsActivity
import com.practicecoding.blog_sci.R
import com.practicecoding.blog_sci.databinding.BlogItemBinding
import com.practicecoding.blog_sci.model.BlogItemModel


class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    private val blogdb = Firebase.firestore.collection("blogs")
    private val userdb = Firebase.firestore.collection("users")
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var postLikedReference: DocumentReference
    private lateinit var postSaveReference: DocumentReference
    private lateinit var context: Context
    private var value = false
    private var value1 = false

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            context = binding.root.context
            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profileImage.context)
                .load(blogItemModel.profileImage)
                .into(binding.profileImage)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()

            binding.readMorebtn.setOnClickListener() {
                val intent = Intent(context, BlogReadActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }
            binding.comment.setOnClickListener(){
                val intent = Intent(context, ComentsActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }
            if (currentUser?.uid != null) {
                postLikedReference = blogdb.document(blogItemModel.time.toString())
                postLikedReference.get()
                    .addOnSuccessListener { document ->
                        var likeUser: List<String> =
                            document.get("likeUser") as? List<String> ?: emptyList()
                        for (user in likeUser) {
                            if (currentUser.uid == user) {
                                binding.likeBtn.setImageResource(R.drawable.iconloveinred)
                                value = true
                                break
                            }
                        }
                        if (value == false) {
                            binding.likeBtn.setImageResource(R.drawable.iconloveinblack)

                        }
                        value = false
                    }
                postSaveReference = userdb.document(currentUser.uid)
                postSaveReference.get()
                    .addOnSuccessListener { documents ->
                        var savePost: List<String> =
                            documents.get("savePost") as? List<String> ?: emptyList()
                        for (post in savePost) {
                            if (post == blogItemModel.time.toString()) {
                                binding.saveBtn.setImageResource(R.drawable.vectormessageinred)
                                value1 = true
                                break
                            }
                        }
                        if (!value1) {
                            binding.saveBtn.setImageResource(R.drawable.redmessageborder)
                        }
                        value1 = false

                    }
            }

            binding.likeBtn.setOnClickListener() {

                handlelikebtn(blogItemModel)
            }
            binding.saveBtn.setOnClickListener() {
                handleSaveBtm(blogItemModel)
            }
        }

        private fun handleSaveBtm(blogItemModel: BlogItemModel) {
            if (currentUser?.uid != null) {
                postSaveReference = userdb.document(currentUser.uid)
                postSaveReference.get()
                    .addOnSuccessListener { document ->
                        var savePost: List<String> =
                            document.get("savePost") as? List<String> ?: emptyList()
                        var mutableSavePost = savePost.toMutableList()
                        for (post in savePost) {
                            if (blogItemModel.time.toString() == post) {
                                binding.saveBtn.setImageResource(R.drawable.redmessageborder)

                                mutableSavePost.remove(post)

                                break
                            }
                        }
                        if (savePost.size <= mutableSavePost.size) {
                            binding.saveBtn.setImageResource(R.drawable.vectormessageinred)
                            mutableSavePost.add(blogItemModel.time.toString())
                        }
                        savePost = mutableSavePost.toList()
                        postSaveReference.update("savePost", savePost)
                        notifyDataSetChanged()
                    }
            }
        }

        private fun handlelikebtn(blogItemModel: BlogItemModel) {
            if (currentUser?.uid != null) {
                postLikedReference = blogdb.document(blogItemModel.time.toString())
                postLikedReference.get()
                    .addOnSuccessListener { document ->
                        var likeUser: List<String> =
                            document.get("likeUser") as? List<String> ?: emptyList()
                        val mutableLikeUser = likeUser.toMutableList()
                        for (user in likeUser) {
                            if (currentUser?.uid == user) {
                                binding.likeBtn.setImageResource(R.drawable.iconloveinblack)
                                mutableLikeUser.remove(user)
                                break
                            }
                        }
                        if (likeUser.size <= mutableLikeUser.size) {
                            binding.likeBtn.setImageResource(R.drawable.iconloveinred)
                            mutableLikeUser.add(currentUser.uid)
                        }
                        likeUser = mutableLikeUser.toList()
                        postLikedReference.update("likeUser", likeUser)
                        postLikedReference.update("likeCount", likeUser.size)
                        blogItemModel.likeCount = mutableLikeUser.size
                        notifyDataSetChanged()
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflate, parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        var blogItem = items[position]
        holder.bind(blogItem)
    }
}