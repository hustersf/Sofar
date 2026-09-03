package com.sofar.network.cache.storage.disk

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 文件缓存内容仍使用软密钥做 AES-GCM 加解密，Keystore 仅用于保护该软密钥。
 *
 * 文件密文格式：
 *   [IV(12字节)][密文 + GCM Tag(16字节)]
 */
internal class DiskCacheCipher(
  private val cacheDir: File
) {

  private val keystoreKey: SecretKey? by lazy {
    getOrCreateKeystoreKey()
  }

  private val fileSecretKey: SecretKey? by lazy {
    loadOrCreateFileSecretKey()
  }

  fun warmUpKey() {
    fileSecretKey
  }

  /**
   * 加密：返回 [12字节 IV][密文 + 16字节 GCM Tag]
   */
  fun encrypt(data: ByteArray, aad: ByteArray?): ByteArray? {
    val key = fileSecretKey ?: return null
    return runCatching {
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(Cipher.ENCRYPT_MODE, key)
      aad?.let { cipher.updateAAD(it) }
      cipher.iv + cipher.doFinal(data)
    }.getOrNull()
  }

  /**
   * 解密：从首 12 字节提取 IV，解密剩余密文
   */
  fun decrypt(data: ByteArray, aad: ByteArray?): ByteArray? {
    val key = fileSecretKey ?: return null
    if (data.size <= GCM_IV_SIZE) return null
    return runCatching {
      val iv = data.copyOfRange(0, GCM_IV_SIZE)
      val ciphertext = data.copyOfRange(GCM_IV_SIZE, data.size)
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
      aad?.let { cipher.updateAAD(it) }
      cipher.doFinal(ciphertext)
    }.getOrNull()
  }

  private fun loadOrCreateFileSecretKey(): SecretKey? {
    val keystoreSecretKey = keystoreKey ?: return null
    val encryptedKeyFile = File(cacheDir, ENCRYPTED_KEY_FILE)
    val keyBytes = runCatching {
      cacheDir.mkdirs()
      if (encryptedKeyFile.exists()) {
        loadExistingFileSecretKey(keystoreSecretKey, encryptedKeyFile)
      } else {
        createAndPersistSoftKey(keystoreSecretKey, encryptedKeyFile)
      }
    }.getOrNull() ?: return null

    if (keyBytes.size != KEY_SIZE_BYTES) return null
    return SecretKeySpec(keyBytes, AES_ALGORITHM)
  }

  private fun loadExistingFileSecretKey(
    keystoreSecretKey: SecretKey,
    encryptedKeyFile: File
  ): ByteArray? {
    val keyBytes = decryptSoftKey(keystoreSecretKey, encryptedKeyFile.readBytes())
    if (keyBytes?.size == KEY_SIZE_BYTES) {
      return keyBytes
    }
    encryptedKeyFile.delete()
    return createAndPersistSoftKey(keystoreSecretKey, encryptedKeyFile)
  }

  private fun createAndPersistSoftKey(
    keystoreSecretKey: SecretKey,
    encryptedKeyFile: File
  ): ByteArray? {
    val plainKeyBytes = ByteArray(KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
    val encryptedKeyBytes = encryptSoftKey(keystoreSecretKey, plainKeyBytes) ?: return null
    encryptedKeyFile.writeBytes(encryptedKeyBytes)
    return plainKeyBytes
  }

  private fun getOrCreateKeystoreKey(): SecretKey? {
    return runCatching {
      val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
      if (keyStore.containsAlias(KEYSTORE_KEY_ALIAS)) {
        (keyStore.getEntry(KEYSTORE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
      } else {
        generateKeystoreKey()
      }
    }.getOrNull()
  }

  private fun generateKeystoreKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    val keySpec = KeyGenParameterSpec.Builder(
      KEYSTORE_KEY_ALIAS,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).apply {
      setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      setKeySize(KEY_SIZE_BITS)
      setRandomizedEncryptionRequired(true)
    }.build()
    keyGenerator.init(keySpec)
    return keyGenerator.generateKey()
  }

  private fun encryptSoftKey(keystoreSecretKey: SecretKey, plainKeyBytes: ByteArray): ByteArray? {
    return runCatching {
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(Cipher.ENCRYPT_MODE, keystoreSecretKey)
      cipher.iv + cipher.doFinal(plainKeyBytes)
    }.getOrNull()
  }

  private fun decryptSoftKey(
    keystoreSecretKey: SecretKey,
    encryptedKeyBytes: ByteArray
  ): ByteArray? {
    if (encryptedKeyBytes.size <= GCM_IV_SIZE) return null
    return runCatching {
      val iv = encryptedKeyBytes.copyOfRange(0, GCM_IV_SIZE)
      val ciphertext = encryptedKeyBytes.copyOfRange(GCM_IV_SIZE, encryptedKeyBytes.size)
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(Cipher.DECRYPT_MODE, keystoreSecretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
      cipher.doFinal(ciphertext)
    }.getOrNull()
  }

  private companion object {
    const val AES_ALGORITHM = "AES"
    const val ANDROID_KEYSTORE = "AndroidKeyStore"
    const val ENCRYPTED_KEY_FILE = "cache.key.enc"
    const val KEYSTORE_KEY_ALIAS = "network.cache.key"
    const val KEY_SIZE_BITS = 256
    const val KEY_SIZE_BYTES = 32
    const val TRANSFORMATION = "AES/GCM/NoPadding"
    const val GCM_IV_SIZE = 12
    const val GCM_TAG_BITS = 128
  }
}
