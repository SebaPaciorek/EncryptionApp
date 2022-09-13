package com.example.encryptionapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.dataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.encryptionapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val FILE_NAME_DATA_STORE = "user-settings.json"

private val Context.dataStore by dataStore(
    fileName = FILE_NAME_DATA_STORE,
    serializer = UserSettingsSerializer(CryptoManager())
)

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

            try {
                val fileOutputStream = FileOutputStream(file)

                val encrypted = cryptoManager.encrypt(
                    bytes = bytes,
                    outputStream = fileOutputStream
                ).decodeToString()

                binding.encryptedText.text = encrypted

                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Encrypted successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (illegalStateException: IllegalStateException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is in a wrong state (e.g., has not been initialized) $illegalStateException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (illegalBlockSizeException: javax.crypto.IllegalBlockSizeException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is a block cipher, no padding has been requested  $illegalBlockSizeException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (badPaddingException: javax.crypto.BadPaddingException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is in decryption mode, and (un)padding has been requested, but the decrypted data is not bounded by the appropriate padding bytes $badPaddingException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        binding.buttonDecrypt.setOnClickListener {
            val file = File(filesDir, "secret.txt")

            val fileInputStream = FileInputStream(file)

            try {
                val decrypted = cryptoManager.decrypt(
                    inputStream = fileInputStream
                ).decodeToString()

                binding.decryptedText.text = decrypted

                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypted successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (illegalStateException: IllegalStateException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is in a wrong state (e.g., has not been initialized) $illegalStateException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (illegalBlockSizeException: javax.crypto.IllegalBlockSizeException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is a block cipher, no padding has been requested  $illegalBlockSizeException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (badPaddingException: javax.crypto.BadPaddingException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Decrypting failure: Cipher is in decryption mode, and (un)padding has been requested, but the decrypted data is not bounded by the appropriate padding bytes $badPaddingException",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    try {
                        dataStore.updateData {
                            UserSettings(
                                username = binding.encryptTextInputEditText.text.toString(),
                                password = binding.encryptTextInputEditText.text.toString()
                            )
                        }
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Saved successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (ioException: IOException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Saving failure: IOException $ioException",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Saving failure: Exception $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }

        binding.buttonLoad.setOnClickListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    try {
                        binding.datastoreText.text = dataStore.data.first().toString()

                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Loaded successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: NoSuchElementException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Loading failure: Exception $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (illegalStateException: IllegalStateException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Decrypting failure: Cipher is in a wrong state (e.g., has not been initialized) $illegalStateException",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (illegalBlockSizeException: javax.crypto.IllegalBlockSizeException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Decrypting failure: Cipher is a block cipher, no padding has been requested  $illegalBlockSizeException",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (badPaddingException: javax.crypto.BadPaddingException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Decrypting failure: Cipher is in decryption mode, and (un)padding has been requested, but the decrypted data is not bounded by the appropriate padding bytes $badPaddingException",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (AEADBadTagException: javax.crypto.AEADBadTagException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Loading failure: Exception $AEADBadTagException",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Loading failure: Exception $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }

    }
}