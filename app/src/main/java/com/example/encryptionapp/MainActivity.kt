package com.example.encryptionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.encryptionapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cryptoManager = CryptoManager()

        binding.buttonEncrypt.setOnClickListener {
            val bytes = binding.encryptTextInputEditText.text.toString().encodeToByteArray()
            //data/data/com.example.encryptionapp/files/secret.txt
            val file = File(filesDir, "secret.txt")
            if (!file.exists()) {
                file.createNewFile()
            }

            val fileOutputStream = FileOutputStream(file)

            val encrypted = cryptoManager.encrypt(
                bytes = bytes,
                outputStream = fileOutputStream
            ).decodeToString()

            binding.encryptedText.text = encrypted
        }

        binding.buttonDecrypt.setOnClickListener {
            val file = File(filesDir, "secret.txt")

            val fileInputStream = FileInputStream(file)

            val decrypted = cryptoManager.decrypt(
                inputStream = fileInputStream
            ).decodeToString()

            binding.decryptedText.text = decrypted
        }

    }
}