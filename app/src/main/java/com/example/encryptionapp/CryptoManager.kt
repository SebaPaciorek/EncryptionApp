package com.example.encryptionapp

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun initEncryptCipher(): Cipher? {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, createKey())
        }
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry

        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    @Throws(
        IllegalStateException::class,
        javax.crypto.IllegalBlockSizeException::class,
        javax.crypto.BadPaddingException::class,
        java.io.IOException::class,
        javax.crypto.AEADBadTagException::class
    )
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val cipher = initEncryptCipher() as Cipher
        val encryptedBytes = cipher.doFinal(bytes)
        /*
        common way is to append iv in front of output stream
         */
        outputStream.use {
            it.write(cipher.iv.size)
            it.write(cipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }

        return encryptedBytes
    }

    @Throws(
        IllegalStateException::class,
        javax.crypto.IllegalBlockSizeException::class,
        javax.crypto.BadPaddingException::class,
        java.io.IOException::class,
        javax.crypto.AEADBadTagException::class
    )
    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"
    }
}