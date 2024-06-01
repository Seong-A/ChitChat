package com.example.chitchat

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.FriendAdapter
import com.example.chitchat.model.Friend
import com.example.chitchat.model.User
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var friendList: MutableList<Friend>
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        friendRecyclerView = findViewById(R.id.freindRecyclerView)
        friendRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance()

        friendList = ArrayList()

        loadFriendsFromFirebaseDatabase()

        friendAdapter = FriendAdapter(this, friendList)
        friendRecyclerView.adapter = friendAdapter

        val addFriendButton: ImageView = findViewById(R.id.addFriend)
        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }
    }

    private fun loadFriendsFromFirebaseDatabase() {
        val usersRef = database.getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        friendList.add(Friend(user.name, R.drawable.profile)) // Assuming a default profile image
                    }
                }
                friendAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun showAddFriendDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_friends, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("친구 추가")

        val alertDialog = dialogBuilder.show()

        val nameEditText: EditText = dialogView.findViewById(R.id.editTextFriendName)
        val phoneEditText: EditText = dialogView.findViewById(R.id.editTextFriendPhone)
        val addButton: Button = dialogView.findViewById(R.id.buttonAddFriend)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            if (name.isNotEmpty() && phone.isNotEmpty()) {
                checkAndAddFriend(name, phone, alertDialog)
            }
        }
    }

    private fun checkAndAddFriend(name: String, phone: String, alertDialog: AlertDialog) {
        val usersRef = database.getReference("users")
        usersRef.orderByChild("phoneNumber").equalTo(phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var friendFound = false
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null && user.name == name) {
                            friendList.add(Friend(user.name, R.drawable.profile)) // Assuming a default profile image
                            friendAdapter.notifyDataSetChanged()
                            friendFound = true
                            break
                        }
                    }
                    if (!friendFound) {
                        showFriendNotFoundDialog()
                    }
                    alertDialog.dismiss()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    private fun showFriendNotFoundDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("친구 추가 실패")
        builder.setMessage("해당 이름과 전화번호를 가진 친구를 찾을 수 없습니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
