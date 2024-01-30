package com.practicecoding.blog_sci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.practicecoding.blog_sci.databinding.ActivityBlogReadBinding
import com.practicecoding.blog_sci.model.BlogItemModel

class BlogReadActivity : AppCompatActivity() {
    private val binding:ActivityBlogReadBinding by lazy{
        ActivityBlogReadBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener(){
            finish()
        }

        val blog = intent.getParcelableExtra<BlogItemModel>("blogItem")

        if(blog != null){
            binding.titletext.text = blog.heading
            binding.date.text = blog.date
            binding.bodytext.text = blog.post
            binding.userName.text = blog.userName
            val userImage = blog.profileImage
            Glide.with(this)
                .load(userImage)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage2)
        }else{
            Toast.makeText(this,"Failed to load blog",Toast.LENGTH_LONG).show()
        }
    }
}