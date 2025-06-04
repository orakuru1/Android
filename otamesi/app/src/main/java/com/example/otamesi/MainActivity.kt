package com.example.otamesi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.otamesi.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding
private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ttt.text = "ありがとう"


        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("rooms/room12345")
/*
        val playerData = mapOf(
            "name" to "からす",
            "ready" to true,
            "score" to 3
        )

        roomRef.child("player2").setValue(playerData)

 */
        binding.scorebutton.setOnClickListener{
            score ++
            val playerData = mapOf(
                "name" to "からす",
                "ready" to true,
                "score" to score
            )
            roomRef.child("player2").setValue(playerData)
        }
    }
}