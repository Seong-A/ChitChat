package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.FriendAdapter
import com.example.chitchat.model.Friend
import com.example.chitchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SearchFriendActivity : AppCompatActivity(), FriendAdapter.OnItemClickListener {

    private lateinit var searchInput: EditText
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var friendList: MutableList<Friend>
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)

        searchInput = findViewById(R.id.searchInput)
        friendRecyclerView = findViewById(R.id.FriendRecyclerView)
        friendRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance()

        friendList = ArrayList()
        friendAdapter = FriendAdapter(this, friendList)
        friendRecyclerView.adapter = friendAdapter
        friendAdapter.setOnItemClickListener(this)

        searchInput.addTextChangedListener {
            val searchText = searchInput.text.toString().trim()
            if (searchText.isNotEmpty()) {
                searchFriends(searchText)
            } else {
                friendList.clear()
                friendAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun searchFriends(searchText: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = database.getReference("users")

        userRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    friendList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null && user.email != currentUser?.email) {
                            friendList.add(Friend(user.name, user.email, R.drawable.profile))
                        }
                    }
                    friendAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    override fun onItemClick(position: Int) {
        val selectedFriend = friendList[position]

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("friendName", selectedFriend.name)
        intent.putExtra("friendEmail", selectedFriend.email)
        startActivity(intent)
    }
}
