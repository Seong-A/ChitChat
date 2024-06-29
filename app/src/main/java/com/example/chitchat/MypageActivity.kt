package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MypageActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val userTextView = findViewById<TextView>(R.id.user_name)
        updateUserDisplayName(userTextView)

        findViewById<View>(R.id.user_modify).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

//        findViewById<View>(R.id.contents).setOnClickListener {
//            val intent = Intent(this, ContentActivity::class.java)
//            startActivity(intent)
//        }

//        findViewById<View>(R.id.service).setOnClickListener {
//            val intent = Intent(this, ServiceActivity::class.java)
//            startActivity(intent)
//        }

        findViewById<View>(R.id.logout).setOnClickListener{
            firebaseAuth.signOut()


            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    finish()
                    true
                }
                R.id.chat -> {
                    val chatintent = Intent(this, ChatListActivity::class.java)
                    startActivity(chatintent)
                    finish()
                    true
                }
                R.id.botchat -> {
                    val botintent = Intent(this, BotChatActivity::class.java)
                    startActivity(botintent)
                    finish()
                    true
                }
                R.id.mypage -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.mypage
    }

    private fun updateUserDisplayName(userTextView: TextView) {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId: String = user.uid
            val userRef = databaseReference.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    name?.let {
                        val nameMessage = "$it 님"
                        val emailMessage = "$email"
                        userTextView.text = nameMessage + "\n" + emailMessage
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MypageActivity, "사용자 데이터 찾기 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}