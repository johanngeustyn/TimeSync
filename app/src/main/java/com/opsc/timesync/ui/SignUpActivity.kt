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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.opsc.timesync.MainActivity
import com.opsc.timesync.R


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val signInText = findViewById<TextView>(R.id.signInText)

        signUpButton.setOnClickListener {
            signUp(email.text.toString(), password.text.toString())
        }

        signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp(email: String, password: String){
        Log.d(LoginActivity.TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = findViewById<EditText>(R.id.email)
        if (TextUtils.isEmpty(email.toString())) {
            email.error = "Required."
            valid = false
        } else {
            email.error = null
        }

        val password = findViewById<EditText>(R.id.password)
        if (TextUtils.isEmpty(password.toString())) {
            password.error = "Required."
            valid = false
        } else {
            password.error = null
        }

        return valid
    }

    companion object {
        const val TAG = "EmailPassword"
    }
}