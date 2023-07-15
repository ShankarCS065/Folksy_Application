package com.krashkrosh748199.folksy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.krashkrosh748199.folksy.Daos.PostDao
import com.krashkrosh748199.folksy.models.Post

class MainActivity : AppCompatActivity(), IPostAdapter {
     private lateinit var postDao:PostDao
    private lateinit var adapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

val fabFirst=findViewById<FloatingActionButton>(R.id.fab)
    fabFirst.setOnClickListener{
val intent =  Intent(this,CreatePostActivity::class.java)
    startActivity(intent)
}
        setUpRecyclerView()
    }

      private fun setUpRecyclerView() {
          postDao=PostDao()
        val postsCollections=postDao.postCollections
        val query =postsCollections.orderBy("createdAt",Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

        adapter= PostAdapter(recyclerViewOptions,this)

       val rvSecond= findViewById<RecyclerView>(R.id.recyclerView)
           rvSecond.adapter=adapter
        rvSecond.layoutManager=LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.startListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }


}