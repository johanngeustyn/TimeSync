package com.opsc.timesync.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.opsc.timesync.MainActivity
import com.opsc.timesync.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var occupationEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.name)
        occupationEditText = findViewById(R.id.occupation)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.signUpButton)
        signInText = findViewById(R.id.signInText)

        signUpButton.setOnClickListener {
            signUp(
                nameEditText.text.toString(),
                occupationEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp(name: String, occupation: String, email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val user = hashMapOf(
                            "userId" to currentUser.uid,
                            "name" to name,
                            "occupation" to occupation,
                            "email" to email
                        )

                        db.collection("userProfile")
                            .document(currentUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "User profile created for ${currentUser.uid}")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error creating user profile", e)
                            }
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun validateForm(): Boolean {
        var valid = true

        val name = nameEditText.text.toString()
        if (TextUtils.isEmpty(name)) {
            nameEditText.error = "Required."
            valid = false
        } else {
            nameEditText.error = null
        }

        val email = emailEditText.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Required."
            valid = false
        } else {
            emailEditText.error = null
        }

        val password = passwordEditText.text.toString()
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Required."
            valid = false
        } else {
            passwordEditText.error = null
        }

        return valid
    }

    companion object {
        const val TAG = "EmailPassword"
    }
}
