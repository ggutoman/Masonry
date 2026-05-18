package org.gag.appdriver.Libraries.HTTP

import java.lang.Exception
import java.security.MessageDigest
import java.util.Locale

object HashRepository {

    fun hashMD5(hashval: String): String{

        return try {

            val hashObj: MessageDigest = MessageDigest.getInstance("MD5")

            hashObj.update(hashval.toByteArray())

            val bytes: ByteArray = hashObj.digest()

            val hexArray: CharArray = "0123456789ABCDEF".toCharArray()
            val hexChars = CharArray(bytes.size * 2)

            for (index in bytes.indices){

                val ind: Int = bytes[index].toInt() and 0xFF

                hexChars[index * 2] = hexArray[ind.ushr(4)]
                hexChars[index * 2 + 1] = hexArray[ind.and(0x0F)]

            }

            String(hexChars).lowercase(Locale.ROOT)

        }catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }
}