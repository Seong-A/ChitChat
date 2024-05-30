package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity(){

    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var email: String = ""
    private var phone: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.Email_Edit)
        val etPass = findViewById<EditText>(R.id.Pass_Edit)
        val btnLogin = findViewById<Button>(R.id.LoginpBtn)
        val btnSignUp = findViewById<Button>(R.id.SignupBtn)

        // 회원가입 버튼 클릭
        btnSignUp.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        //로그인 버튼
        btnLogin.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this@LoginActivity,"이메일과 비밀번호를 입력하시오", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        //로그인 성공 시 매인 액티비티로 이동
                        Toast.makeText(this@LoginActivity,"로그인성공",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }else{
                        //로그인 실패 시 사용자에게 메시지 표시
                        if(task.exception is FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this@LoginActivity, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun saveUserToFirebase(uid: String?, email: String?, name: String?) {
        uid?.let { uid ->
            val userMap = HashMap<String, Any>()
            userMap["uid"] = uid ?:""
            userMap["email"] = email ?: ""
            userMap["name"] = name ?: ""

            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            database.child("users").child(uid).setValue(userMap)
                .addOnSuccessListener {
                    Log.d("LoginActivity", "회원 등록 성공")
                }
                .addOnFailureListener { e ->
                    Log.w("LoginActivity", "회원 등록 실패", e)
                }
        }
    }

}