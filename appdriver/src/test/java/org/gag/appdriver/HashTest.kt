package org.gag.appdriver

import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.junit.Test

class HashTest {

    val hashRepository = HashRepository()

    @Test
    fun testEncryptMatchesPhp() {
        val Plaintext= "ggutoman1998"
        val rawStr = hashRepository.EncryptHex(Plaintext)

        println(rawStr)
    }

    @Test
    fun testDecryptMatchesPhp() {
        val Encrypttext= "a6aaea631b282a4c2148474c"
        val encryptStr = hashRepository.DecryptHex(Encrypttext)

        println(encryptStr)
    }

    @Test
    fun TestFormat(){

        val name : String = "Gutoman, Guillier A"

        print(name.replace(name.split(",").get(0), "Doe"))
    }

}