package com.practicecoding.blog_sci.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.practicecoding.blog_sci.BlogReadActivity
import com.practicecoding.blog_sci.databinding.BlogItemBinding
import com.practicecoding.blog_sci.databinding.ChangeBlogItemBinding
import com.practicecoding.blog_sci.model.BlogItemModel

class MyBlogAdapter(
    private val context: Context, private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MyBlogAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(blogItem: BlogItemModel)
        fun onRead(blogItem: BlogItemModel)
        fun onDelete(blogItem: BlogItemModel)
    }

    private val blogdb = Firebase.firestore.collection("blogs")
    private val userdb = Firebase.firestore.collection("users")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    inner class BlogViewHolder(private val binding: ChangeBlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogItemModel) {
            binding.heading.text = blogItem.heading
            Glide.with(binding.profileImage.context)
                .load(blogItem.profileImage)
                .into(binding.profileImage)
            binding.userName.text = blogItem.userName
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

            binding.readMorebtn.setOnClickListener(){
                itemClickListener.onRead(blogItem)
            }
            binding.editBtn.setOnClickListener(){
                itemClickListener.onEdit(blogItem)
            }
            binding.deleteBtn.setOnClickListener(){
                itemClickListener.onDelete(blogItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChangeBlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    fun setData(savedBlogPost: MutableList<BlogItemModel>) {
        this.blogList = savedBlogPost
        notifyDataSetChanged()
    }


}