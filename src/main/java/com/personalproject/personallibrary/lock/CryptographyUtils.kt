package com.personalproject.personallibrary.lock

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class CryptographyUtils {
    private val encryptionKey = "galery_pss_encryption"

    @RequiresApi(Build.VERSION_CODES.O)
    public fun encryptSelectedPoints(points: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val key = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val byteStream = ByteArrayOutputStream()
        val objectStream = ObjectOutputStream(byteStream)
        objectStream.writeObject(points)
        objectStream.close()
        val encryptedBytes = cipher.doFinal(byteStream.toByteArray())

        return Base64.getEncoder().encodeToString(encryptedBytes)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    public fun decryptStoredPoints(encryptedPoints: String?): String {
        if (encryptedPoints.isNullOrEmpty()) {
            return ""
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val key = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key)

        val encryptedBytes = Base64.getDecoder().decode(encryptedPoints)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}