package com.sirius.library.naclJava

import kotlin.jvm.Volatile

class LibSodium private constructor() {
    fun getLazySodium(): LazySodium? {
        return lazySodium
    }

    val lazyAaed: AEAD.Lazy?
        get() = getLazySodium() as AEAD.Lazy?
    val nativeAaed: AEAD.Native?
        get() = getLazySodium() as AEAD.Native?
    val lazyBox: Box.Lazy?
        get() = getLazySodium() as Box.Lazy?
    val nativeBox: Box.Native?
        get() = getLazySodium() as Box.Native?
    val lazyAuth: Auth.Lazy?
        get() = getLazySodium() as Auth.Lazy?
    val lazyHash: Hash.Lazy?
        get() = getLazySodium() as Hash.Lazy?
    val lazySign: Sign.Lazy?
        get() = getLazySodium() as Sign.Lazy?
    val lazyPwHash: PwHash.Lazy?
        get() = getLazySodium() as PwHash.Lazy?
    val lazyScrypt: Scrypt.Lazy?
        get() = getLazySodium() as Scrypt.Lazy?
    val lazyStream: Stream.Lazy?
        get() = getLazySodium() as Stream.Lazy?
    val lazyHelpers: Helpers.Lazy?
        get() = getLazySodium() as Helpers.Lazy?
    val lazyPadding: Padding.Lazy?
        get() = getLazySodium() as Padding.Lazy?
    val lazySecretBox: SecretBox.Lazy?
        get() = getLazySodium() as SecretBox.Lazy?
    val lazyShortHash: ShortHash.Lazy?
        get() = getLazySodium() as ShortHash.Lazy?
    val lazyStreamJava: StreamJava.Lazy?
        get() = getLazySodium() as StreamJava.Lazy?
    val lazyGenericHash: GenericHash.Lazy?
        get() = getLazySodium() as GenericHash.Lazy?
    val lazyKeyExchange: KeyExchange.Lazy?
        get() = getLazySodium() as KeyExchange.Lazy?
    val lazySecretStream: SecretStream.Lazy?
        get() = getLazySodium() as SecretStream.Lazy?
    val lazySecureMemory: SecureMemory.Lazy?
        get() = getLazySodium() as SecureMemory.Lazy?
    val lazyDiffieHellman: DiffieHellman.Lazy?
        get() = getLazySodium() as DiffieHellman.Lazy?
    val lazyKeyDerivation: KeyDerivation.Lazy?
        get() = getLazySodium() as KeyDerivation.Lazy?
    private var lazySodium: LazySodium? = null

    companion object {
        @Volatile
        private var mInstance: LibSodium? = null
        val instance: LibSodium?
            get() {
                if (mInstance == null) {
                    synchronized(LibSodium::class.java) {
                        if (mInstance == null) {
                            mInstance = LibSodium()
                        }
                    }
                }
                return mInstance
            }
    }

    init {
        if (Platform.isAndroid()) {
            lazySodium = LazySodiumAndroid(SodiumAndroid(), java.nio.charset.StandardCharsets.US_ASCII)
        } else {
            lazySodium = LazySodiumJava(SodiumJava(), java.nio.charset.StandardCharsets.US_ASCII)
        }
    }
}
