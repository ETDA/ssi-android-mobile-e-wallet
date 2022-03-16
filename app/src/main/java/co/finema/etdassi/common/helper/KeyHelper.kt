package co.finema.etdassi.common.helper

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import co.finema.etdassi.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.Certificate
import java.security.spec.ECGenParameterSpec
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class KeyHelper() {
    private val alias = BuildConfig.KEY_ALIAS
    private val keyStore by lazy {
        KeyStore.getInstance(BuildConfig.KEY_TYPE).apply {
            load(null) // "AndroidKeyStore"
        }
    }

    companion object {
        const val KEY_ALGORITHM = BuildConfig.KEY_ALGORITHM_EC
        const val SIGN_ALGORITHM = BuildConfig.SIGN_ALGORITHM_EC
    }

    // GetPrivateKey
    private val privateKey: PrivateKey?
        get() {
            var key = _getPrivateKey()
            if (key != null) return key
            createAndroidKeyStoreAsymmetricKey()
            try {
                key = _getPrivateKey()
                return key
            } catch (e: Exception) {
            }
            return null
        }

    @Suppress("FunctionName")
    private fun _getPrivateKey(): PrivateKey? {
        return try {
            keyStore.getKey(alias, null) as? PrivateKey
        } catch (e: Exception) {
            null
        }
    }

    private fun createAndroidKeyStoreAsymmetricKey(): KeyPair {
        val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BuildConfig.KEY_TYPE)
        initGeneratorWithKeyGenParameterSpec(generator, alias)
        return generator.generateKeyPair()
    }

//    @SuppressLint("WrongConstant")
//    private fun initGeneratorWithKeyPairGeneratorSpec(generator: KeyPairGenerator, alias: String) {
//        val start = Calendar.getInstance()
//        val end = Calendar.getInstance()
//        end.add(Calendar.YEAR, 20)
//
//        val builder = KeyPairGeneratorSpec.Builder(app)
//            .setAlias(alias)
//            .setSubject(X500Principal("CN=$alias"))
//            .setKeySize(256)
//            .setKeyType("EC")
//            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
//            .setStartDate(start.time)
//            .setEndDate(end.time)
//            .setSerialNumber(BigInteger.ONE)
//
//        generator.initialize(builder.build())
//    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT
                    or KeyProperties.PURPOSE_DECRYPT
                    or KeyProperties.PURPOSE_SIGN
                    or KeyProperties.PURPOSE_VERIFY
        ).setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setKeySize(256)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setUserAuthenticationRequired(false)
        val spec = builder.build()
        generator.initialize(spec)

//        Log.d("Key", "getKeySize : ${spec.keySize}")
    }

    //GetPublicKey
    @Suppress("MemberVisibilityCanBePrivate")
    private val publicKey: PublicKey?
        get() {
            var certificate = _getCertificate()
            if (certificate != null) return certificate.publicKey

            createAndroidKeyStoreAsymmetricKey()
            try {
                certificate = _getCertificate()
//                Log.d("certificate", "size : ${certificate?.type}, ${certificate?.encoded?.size}")
                return certificate?.publicKey
            } catch (e: Exception) {
            }
            return null
        }

    @Suppress("FunctionName")
    private fun _getCertificate(): Certificate? {
        return try {
            keyStore.getCertificate(alias)
        } catch (e: Exception) {
            null
        }
    }

    private fun pemPublicKey(): String {
        val key = Base64.encodeToString(publicKey?.encoded, Base64.DEFAULT)
        val line = StringBuilder()
        line.append("-----BEGIN PUBLIC KEY-----\n")
        line.append(replaceDataNewLine(key))
        line.append("\n-----END PUBLIC KEY-----")

        return line.toString()
    }

    private fun convertPemToHash() : String{
        val md = MessageDigest.getInstance("SHA-256")
//        val keyRotate = pemPublicKey() + " " + ShareMethod.getInstance().countKey
        md.update(pemPublicKey().toByteArray())
        val digest = md.digest()
        return String.format("%02x", BigInteger(1, digest))
    }

    private fun stringToHex() : String {
        val newKey = NewKeyRawData(pemPublicKey(),convertPemToHash(),signDataToString(convertPemToHash(),signSignature(convertPemToHash().toByteArray(
            StandardCharsets.UTF_8))))
        return newKey.toString().toByteArray(StandardCharsets.UTF_8).toHex()
    }

    private fun hexToString(hex : String) : String{
        val bytes = hex.hexToByteArray()
        return String(bytes, StandardCharsets.UTF_8)
    }

    data class NewKeyRawData(
        val key_pem: String? = null,
        val key_hash: String? = null,
        val signature: String? = null
    ){
        fun toJson(): String = Gson().toJson(this)
        fun checkJson(): String{
            val gson = GsonBuilder().disableHtmlEscaping().create()
            val json = gson.toJson(this)
            var disJson = json.replace("\\n","")
            disJson= disJson.replace("-----BEGIN PUBLIC KEY-----","-----BEGIN PUBLIC KEY-----\\n")
            disJson = disJson.replace("-----END PUBLIC KEY-----","\\n-----END PUBLIC KEY-----")
            return disJson
        }
        override fun toString(): String = checkJson()
    }

    // sign Signature
    private val signature: Signature
        get() = Signature.getInstance(SIGN_ALGORITHM)

    private fun signSignature(data: ByteArray): ByteArray {
        var signData: ByteArray = byteArrayOf()

        signData = signature.run {
            initSign(privateKey)
            update(data)
            sign()
        }

        return signData.toAsnDecodedBytes()
    }

    private fun signDataToString(data: String, signData: ByteArray): String {
        val appendedSigned = signData.toAsnEncodedBytes()
        val signatures = Base64.encodeToString(appendedSigned, Base64.DEFAULT)
        val key = Base64.encodeToString(publicKey?.encoded, Base64.DEFAULT)
        key
        signature.run {
            initVerify(publicKey)
            update(data.toByteArray(StandardCharsets.UTF_8))
            if(verify(appendedSigned))
                return signatures
            else
                return ""
        }
    }

    fun verifySignature(data: String, signData: ByteArray) : Boolean{
        val appendedSigned = signData.toAsnEncodedBytes()
        val key = Base64.encodeToString(publicKey?.encoded, Base64.DEFAULT)
        key
        signature.run {
            initVerify(publicKey)
            update(data.toByteArray(StandardCharsets.UTF_8))
            return verify(appendedSigned)
        }
    }


    /**
     *
     * JWT handle
     *
     */

    /**
     * Mapper to transform auth header and payload to a json String.
     */
    interface JsonEncoder<H : JWTAuthHeader, P : JWTAuthPayload> {
        /**
         * Transforms the provided header to a json String.
         * @param header The header to transform.
         * @return A json String representing the header.
         */
        fun toJson(header: H): String

        /**
         * Transforms the provided payload to a json String.
         * @param payload The header to transform.
         * @return A json String representing the payload.
         */
        fun toJson(payload: P): String
    }

    interface JsonDecoder<H : JWTAuthHeader, P : JWTAuthPayload> {
        /**
         * Transforms the provided header json String into a header object.
         * @return A header object representing the json String.
         */
        fun headerFrom(json: String): H

        /**
         * Transforms the provided payload json String into a payload object.
         * @return A payload object representing the json String.
         */
        fun payloadFrom(json: String): P
    }

    interface Base64Encoder {
        /**
         * Base64 encodes the provided bytes in an URL safe way.
         * @param bytes The ByteArray to be encoded.
         * @return The encoded String.
         */
        fun encodeURLSafe(bytes: ByteArray): String

        /**
         * Base64 encodes the provided bytes.
         * @param bytes The ByteArray to be encoded.
         * @return The encoded String.
         */
        fun encode(bytes: ByteArray): String
    }

    interface Base64Decoder {
        /**
         * Base64 encodes the provided bytes.
         * @param bytes The ByteArray to be decoded.
         * @return The decoded bytes.
         */
        fun decode(bytes: ByteArray): ByteArray

        /**
         * Base64 encodes the provided String.
         * @param string The String to be decoded.
         * @return The decoded String as a ByteArray.
         */
        fun decode(string: String): ByteArray
    }

    val gson = GsonBuilder().create()
    val tokenDelimiter = "."

    // generic JSON encoder
    val jsonEncoder = object : JsonEncoder<JWTAuthHeader, JWTAuthPayload> {
        override fun toJson(header: JWTAuthHeader): String {
            return gson.toJson(header, JWTAuthHeader::class.java)
        }

        override fun toJson(payload: JWTAuthPayload): String {
            return gson.toJson(payload, JWTAuthPayload::class.java)
        }
    }

    // Base64 encoder using apache commons
    private val encoder = object : Base64Encoder {
        override fun encodeURLSafe(bytes: ByteArray): String {
            return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(bytes)
        }

        override fun encode(bytes: ByteArray): String {
            return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes)
        }
    }

    // Base64 decoder using apache commons
    private val decoder = object : Base64Decoder {
        override fun decode(bytes: ByteArray): ByteArray {
            return org.apache.commons.codec.binary.Base64.decodeBase64(bytes)
        }

        override fun decode(string: String): ByteArray {
            return org.apache.commons.codec.binary.Base64.decodeBase64(string)
        }
    }

    open class JWTAuthHeader(
        val alg: String = "ES256",
        val typ: String = "typ",
        val kid: String
    )

    open class JWTAuthPayload(
        val jti: String? = null,
        val aud: String? = null,
        val iss: String? = null,
        val nbf: Long? = null,
        val sub: String? = null,
        val vp: JWTVCDoc? = null,
        val vc: JsonObject? = null,
        val nonce: String? = null,
        val exp: String? = null
    )

    data class JWTVCDoc(
        @SerializedName("@context") val mContext: String = "https://www.w3.org/2018/credentials/v1",
        @SerializedName("type") val typeList: List<String> = listOf("claimPresentation", "VerifiablePresentation"),
        @SerializedName("verifiableCredential") val verifiableCredential: List<String>
    )

    fun jwtBuild(jwtAuthHeader: JWTAuthHeader, jwtAuthPayload: JWTAuthPayload): String {
        val jwtString = token(jwtAuthHeader, jwtAuthPayload)
        Log.e("jwtBuild => ", "pv => ${token(jwtAuthHeader, jwtAuthPayload)}")
        Log.e("verify", "verify => ${verifyJwt(jwtString)}")
        return jwtString
    }

    fun verifyJwt(token: String, charset: Charset = Charsets.UTF_8): Boolean {
        val parts = token.split(tokenDelimiter)
        if (parts.size == 3) {
            val header = parts[0].toByteArray(charset)
            val payload = parts[1].toByteArray(charset)
            val tokenSignature = decoder.decode(parts[2])
            signature.run {
                initVerify(publicKey)
                update(header)
                update(tokenDelimiter.toByteArray())
                update(payload)
                return verify(tokenSignature)
            }
        } else {
            return false
        }

    }

    fun <H : JWTAuthHeader, P : JWTAuthPayload> token(header: H, payload: P, charset: Charset = Charsets.UTF_8): String {

        val headerString = jsonEncoder.toJson(header)
        val payloadString = jsonEncoder.toJson(payload)

        val base64Header = encoder.encodeURLSafe(headerString.toByteArray(charset))
        val base64Payload = encoder.encodeURLSafe(payloadString.toByteArray(charset))

        val value = "$base64Header$tokenDelimiter$base64Payload"

        return value + tokenDelimiter + es256(value)
    }

    private fun es256(
        data: String
    ): String {
        return sign(data)
    }







//    fun verifyVC(pem:String,message:String,signature:String): Boolean{
//        val dataPublicKey = loadPublicKeyFromPem(pem)
//        val ecdsaVerify = Signature.getInstance("SHA256withECDSA")
//        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
//        val publicKeySpec : EncodedKeySpec = X509EncodedKeySpec(Base64.decode(dataPublicKey,Base64.DEFAULT))
//        val publicKey = keyFactory.generatePublic(publicKeySpec)
//        ecdsaVerify.run {
//            initVerify(publicKey)
//            update(message.toByteArray(StandardCharsets.UTF_8))
//            return verify(Base64.decode(signature,Base64.DEFAULT))
//        }
////        val keyBytes = Base64.decode(dataPublicKey.toByteArray(StandardCharsets.UTF_8),Base64.DEFAULT)
////        Log.e("Data Key", "$message ${keyBytes.size} $signature")
////        val spec = X509EncodedKeySpec(keyBytes)
////        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
////        val key = keyFactory.generatePublic(spec)
////        val appendedSigned = signature.toByteArray(StandardCharsets.UTF_8).toAsnEncodedBytes()
//        this.signature.run {
//            initVerify(publicKey)
//            update(message.toByteArray(StandardCharsets.UTF_8))
//            return verify(Base64.decode(signature,Base64.DEFAULT))
//        }
//    }

    // Encode and Decode EC
    private fun ByteArray.toAsnDecodedBytes(): ByteArray {
        val d = this.clone()
        val rlen = d[3].toInt()
        val r = d.drop(4).take(rlen).takeLast(32)
        val s = d.takeLast(32)
        val rs = arrayListOf<Byte>().apply {
            addAll(r)
            addAll(s)
        }
        return rs.toByteArray()
    }

    private fun ByteArray.toAsnEncodedBytes(): ByteArray {
        val appendedSigned: ArrayList<Byte> = arrayListOf()

        val vr = this.take(32)
        val vs = this.takeLast(32)

        val avr = if (vr.first() < 0) {
            byteArrayOf(0) + vr
        } else {
            vr.toByteArray()
        }

        val avs = if (vs.first() < 0) {
            byteArrayOf(0) + vs
        } else {
            vs.toByteArray()
        }
        appendedSigned.add(0x30)
        appendedSigned.add((4 + avr.size + avs.size).toByte())
        appendedSigned.add(0x02)
        appendedSigned.add(avr.size.toByte())
        appendedSigned.addAll(avr.toList())
        appendedSigned.add(0x02)
        appendedSigned.add(avs.size.toByte())
        appendedSigned.addAll(avs.toList())
        return appendedSigned.toByteArray()
    }

    fun sign(data: String) : String{
        val signSignature = signSignature(data.toByteArray(StandardCharsets.UTF_8))
        return signDataToString(data,signSignature)
    }

    fun verify(data: String): Boolean {
        val signatureSign = signSignature(data.toByteArray(StandardCharsets.UTF_8))
        return verifySignature(data,signatureSign)
    }

    var pem: String
        get() = pemPublicKey()
        set(value) {}

    var hash: String
        get() = convertPemToHash()
        set(value) {}
}

//return to all public

fun replaceDataNewLine(r : String): String{
    var newString : String
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val json = gson.toJson(r)
    newString = json.replace("\\n","")
    newString = newString.replace("\\s","")
    newString = newString.replace('"',' ').trim()
    return newString
}

fun replaceDataNewLineJson(r : String?): String{
    var newString : String = ""
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val json = gson.toJson(r)
    newString = json.replace("\\n","")
    newString = newString.replace('"',' ').trim()
    newString = newString.replace("\\s","")
    return newString
}

fun ByteArray.toHex(): String = this.joinToString("") { "%02x".format(it) }

fun gzip(content: String): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).bufferedWriter(StandardCharsets.UTF_8).use { it.write(content) }
    return bos.toByteArray()
}

fun ungzip(content: ByteArray): String =
    GZIPInputStream(content.inputStream()).bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

fun String.hexToByteArray(): ByteArray =
    this.chunked(2).map { it.toUpperCase().toInt(16).toByte() }.toByteArray()