package com.krashkrosh748199.folksy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.krashkrosh748199.folksy.Daos.PostDao
import kotlinx.coroutines.DelicateCoroutinesApi


class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao: PostDao

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postDao=PostDao()
findViewById<Button>(R.id.postButton).setOnClickListener {
   val input= findViewById<EditText>(R.id.postInput).text.toString().trim()
    if (input.isNotEmpty()){
        postDao.addPost(input)
        finish()
    }
}
    }
}