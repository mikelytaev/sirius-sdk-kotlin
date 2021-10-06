package com.sirius.library

import com.sirius.library.encryption.Custom
import com.sirius.library.encryption.Ed25519
import com.sirius.library.encryption.UnpackModel
import com.sirius.library.errors.sirius_exceptions.SiriusCryptoError
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidType
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.StringUtils
import kotlin.test.*

class TestEncryption {
    var seed1 = "000000000000000000000000000SEED1"
    var seed2 = "000000000000000000000000000SEED2"
    val testMessage: String
        get() {
            val string = "Test encryption строка"
            val enc_message: JSONObject = JSONObject()
            enc_message.put("content", string)
            val message: String = enc_message.toString()
            return StringUtils.escapeStringLikePython(message)
        }

    @Test
    @Throws(SiriusCryptoError::class, SodiumException::class, SiriusInvalidType::class)
    fun encrypt() {
        //CREATE KEYPAIR
        val keyPairRecipient: KeyPair =
            Custom.createKeypair(seed1.toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val verkeyRecipient: String = Custom.bytesToB58(keyPairRecipient.getPublicKey().getAsBytes())
        val sigkeyRecipient: String = Custom.bytesToB58(keyPairRecipient.getSecretKey().getAsBytes())
        val keyPairSender: KeyPair = Custom.createKeypair(seed2.toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val verkeySender: String = Custom.bytesToB58(keyPairSender.getPublicKey().getAsBytes())
        val sigkeySender: String = Custom.bytesToB58(keyPairSender.getSecretKey().getAsBytes())

        //CREATE TESTmessage
        val message = testMessage
        val ed25519 = Ed25519()


        //PACK MESSAGE
        val verkeys: MutableList<String> = ArrayList<String>()
        verkeys.add(verkeyRecipient)
        val packedString: String = ed25519.packMessage(message, verkeys, verkeySender, sigkeySender)

        //UNPACK MESSAGE
        val unpackedModel: UnpackModel = ed25519.unpackMessage(packedString, verkeyRecipient, sigkeyRecipient)

        //ASSERTING
        assertEquals(unpackedModel.sender_vk, verkeySender)
        assertEquals(unpackedModel.recip_vk, verkeyRecipient)
        assertEquals(message, unpackedModel.message)
    }

    //read={"protected":"eyJlbmMiOiJ4Y2hhY2hhMjBwb2x5MTMwNV9pZXRmIiwidHlwIjoiSldNLzEuMCIsImFsZyI6IkF1dGhjcnlwdCIsInJlY2lwaWVudHMiOlt7ImVuY3J5cHRlZF9rZXkiOiJYT2x2emVXcm5EdGhWUEEyN2wwZVp3NFBBOUxJT0x0WUwwRjdEcTBwNUR3c1N6RjA1bVU0a0hITnJPdWtweE9LIiwiaGVhZGVyIjp7ImtpZCI6IjZRdlEzWTVwUE1HTmd6dnM4Nk4zQVFvOThwRjVXcnpNMWg2V2tLSDNkTDdmIiwiaXYiOiJVbEo4NlRxM0E2aVlDbi1NNnZfeUJHWkxZbDZjMzFvTiIsInNlbmRlciI6IjM1b1h4azI5dDVzVTVvSF9BRnhNUmx0RTZqMWNKUlRhaV9pRmx3RDNXaGM4YlhHMkdrbGxhMjQ1aE1waVdFVFJnYWdxNjYyeGtnRXFvcEFSd2UzanZ6bzV0VkhrWTlhN2pvM095RE1GOEtNcU9fMEhjMmk1bjJhZ05qTT0ifX1dfQ==","iv":"YINZWei8GWFxaw0o","ciphertext":"NMXqUXeGzxDxcFhTDW2QU0G3DPShSydosdMtFhRUVDw7Fl9mzqP3m9AiDT5IlfywOhyRTkkS5KfO9lcAv3PU46q6kFQNnoYf9eddALmoGmPFESJKF5gwItEdzFtUCuozzWcCi1kY85sJO9D-JOzFa-SZCluBftBbMU6qcgM_2vWm1iPP2CBLK5nQAfcDQzj5L_tkB5CMcXTMv1Wbq0EPIDT2_kFXi9Dn7L8eLWAWCmjMbdU80qXzX6KsntXSJ_ibKlEiGrvR3_clyg21L2xwzWlS9GBgv1P28dyC1Ofcd2xPpQclvg7e5nSB5scoKgsDrCdHX7_DllXsMA7uymwj7mIV9rifw4zwkldH_LApajYXbEpod-uEeN0KFu5TyhmwKKCfALtBZ6CctrqOLYm6D-rJCKzP7gUjfWKxwsNiXrhIy38LCQrO25nJ7Z8NPSbIaktpRiMJbz4oaJrmdvcjXVR1d-e8uDzRkvwvyEBRVFJKpuBKP9E-4HHLXh_F-6A=","tag":"8tOGZg-i1AO9RO0Eh2mh5Q=="}
    @Test
    @Throws(SiriusCryptoError::class, SodiumException::class, SiriusInvalidType::class)
    fun test_fixture() {
        var keyPairRecipient: KeyPair? = null
        //CREATE KEYPAIR
        keyPairRecipient = Custom.createKeypair(seed1.toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val verkey_recipient: String = Custom.bytesToB58(keyPairRecipient.getPublicKey().getAsBytes())
        val sigkey_recipient: String = Custom.bytesToB58(keyPairRecipient.getSecretKey().getAsBytes())
        val keyPairSender: KeyPair = Custom.createKeypair(seed2.toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val verkeySender: String = Custom.bytesToB58(keyPairSender.getPublicKey().getAsBytes())
        val sigkeySender: String = Custom.bytesToB58(keyPairSender.getSecretKey().getAsBytes())
        val packed =
            "{\"protected\": \"eyJlbmMiOiAieGNoYWNoYTIwcG9seTEzMDVfaWV0ZiIsICJ0eXAiOiAiSldNLzEuMCIsICJhbGciOiAiQXV0aGNyeXB0IiwgInJlY2lwaWVudHMiOiBbeyJlbmNyeXB0ZWRfa2V5IjogInBKcW1xQS1IVWR6WTNWcFFTb2dySGx4WTgyRnc3Tl84YTFCSmtHU2VMT014VUlwT0RQWTZsMVVsaVVvOXFwS0giLCAiaGVhZGVyIjogeyJraWQiOiAiM1ZxZ2ZUcDZRNFZlRjhLWTdlVHVXRFZBWmFmRDJrVmNpb0R2NzZLR0xtZ0QiLCAic2VuZGVyIjogIjRlYzhBeFRHcWtxamd5NHlVdDF2a0poeWlYZlNUUHo1bTRKQjk1cGZSMG1JVW9KajAwWmswNmUyUEVDdUxJYmRDck8xeTM5LUhGTG5NdW5YQVJZWk5rZ2pyYV8wYTBQODJpbVdNcWNHc1FqaFd0QUhOcUw1OGNkUUYwYz0iLCAiaXYiOiAiVU1PM2o1ZHZwQnFMb2Rvd3V0c244WEMzTkVqSWJLb2oifX1dfQ==\", \"iv\": \"MchkHF2M-4hneeUJ\", \"ciphertext\": \"UgcdsV-0rIkP25eJuRSROOuqiTEXp4NToKjPMmqqtJs-Ih1b5t3EEbrrHxeSfPsHtlO6J4OqA1jc5uuD3aNssUyLug==\", \"tag\": \"sQD8qgJoTrRoyQKPeCSBlQ==\"}"
        val ed25519 = Ed25519()
        //UNPACK MESSAGE
        val unpackedModel: UnpackModel = ed25519.unpackMessage(packed, verkey_recipient, sigkey_recipient)
        //В pytone при json.dumps добавляется пробел между ключом значением.
        val testMessage = "{\"content\": \"Test encryption \\u0441\\u0442\\u0440\\u043e\\u043a\\u0430\"}"
        //ASSERTING
        assertEquals(unpackedModel.sender_vk, verkeySender)
        assertEquals(unpackedModel.recip_vk, verkey_recipient)
        assertEquals(unpackedModel.message, testMessage)
    }

    @Test
    @Throws(SiriusCryptoError::class, SodiumException::class)
    fun test_CryptoSign() {
        val kp: KeyPair =
            Custom.createKeypair("0000000000000000000000000000SEED".toByteArray(java.nio.charset.StandardCharsets.UTF_8))
        val msg = "message"
        var signature: ByteArray =
            Custom.signMessage(msg.toByteArray(java.nio.charset.StandardCharsets.UTF_8), kp.getSecretKey().getAsBytes())
        assertEquals(
            "3tfqJYZ8ME8gTFUSHcH4uVTUx5kV7S1qPJJ65k2VtSocMfXvnzR1sbbfq6F2RcXrFtaufjEr4KQVu7aeyirYrcRm",
            Custom.bytesToB58(signature)
        )
        assertTrue(
            Custom.verifySignedMessage(
                kp.getPublicKey().getAsBytes(),
                msg.toByteArray(java.nio.charset.StandardCharsets.UTF_8), signature
            )
        )
        val kp2: KeyPair =
            Custom.createKeypair("000000000000000000000000000SEED2".toByteArray(java.nio.charset.StandardCharsets.UTF_8))
        assertNotEquals(kp2.getPublicKey().getAsBytes(), kp.getPublicKey().getAsBytes())
        signature = Custom.signMessage(
            msg.toByteArray(java.nio.charset.StandardCharsets.UTF_8),
            kp2.getSecretKey().getAsBytes()
        )
        assertFalse(
            Custom.verifySignedMessage(
                kp.getPublicKey().getAsBytes(),
                msg.toByteArray(java.nio.charset.StandardCharsets.UTF_8), signature
            )
        )
    }

    @Test
    @Throws(SiriusCryptoError::class, SodiumException::class)
    fun test_didFromVerkey() {
        val kp: KeyPair =
            Custom.createKeypair("0000000000000000000000000000SEED".toByteArray(java.nio.charset.StandardCharsets.UTF_8))
        assertEquals(
            "GXhjv2jGf2oT1sqMyvJtgJxNYPMHmTsdZ3c2ZYQLJExj",
            Custom.bytesToB58(kp.getPublicKey().getAsBytes())
        )
        val did: ByteArray = Custom.didFromVerkey(kp.getPublicKey().getAsBytes())
        assertEquals("VVZbGvuFqBdoVNY1Jh4j9Q", Custom.bytesToB58(did))
    }
}
