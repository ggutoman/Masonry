package org.gag.appdriver.Libraries.Encryption

import org.gag.appdriver.Constants.ENCRYPT_CONSTANTS
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class HashRepository {

    private val algo = ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsAlgo
    private val key = ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsKey.toByteArray(Charsets.UTF_8) // zero‑padded to 16 bytes
    private val ivSource = ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsSrc
    private val hashing = ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsHash
    private val method = ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsMethod

    private val cipher = Cipher.getInstance(ENCRYPT_CONSTANTS.AES_METHOD_ZERO_PADDING.fsMethod)

    private fun GetSecretKey(): SecretKeySpec {

        //initialize a byte array with length of 16 and zero padding
        val keyBytes = ByteArray(16)

        //copy date from one array to another
        System.arraycopy(key, 0, keyBytes, 0, key.size.coerceAtMost(16))

        //return the secret key, from the byte array
        return SecretKeySpec(keyBytes, algo)
    }

    private fun GetIvSpec(): IvParameterSpec {

        val sha256Hex = MessageDigest.getInstance(hashing) //create hashing instance
            .digest(ivSource.toByteArray(Charsets.UTF_8)) //convert iv source to byte array
            .joinToString("") { "%02x".format(it) } //convert each byte into two digit string

        val ivString = sha256Hex.substring(0, 16) // get first 16 characters of the string
        val ivBytes = ivString.toByteArray(Charsets.UTF_8) //convert first 16 characters into byte values
        return IvParameterSpec(ivBytes) //return the iv parameter
    }

    fun EncryptHex(fsRaw: String): String {

        //initialize encryption mode
        cipher.init(Cipher.ENCRYPT_MODE, GetSecretKey(), GetIvSpec())

        //convert into bytes
        val encryptedBytes = cipher.doFinal(fsRaw.toByteArray(Charsets.UTF_8))

        //return each byte into two digit string
        return encryptedBytes.joinToString("") { "%02x".format(it) }
    }

    fun DecryptHex(cipherHex: String): String {

        //extract each bytes into two characters and map into numeric byte value
        val cipherBytes = cipherHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

        //initialize decryption mode
        cipher.init(Cipher.DECRYPT_MODE, GetSecretKey(), GetIvSpec())

        //return the decrypted string
        return String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }

}