package com.practicecoding.blog_sci.model

data class UserData(
    val name:String ="",
    val email:String= "",
    val photoUrl:String = "",
    val savePost:List<String> ?= null,
    val myBlog:List<String>?=null
){
    constructor():this("","","", null)
}
