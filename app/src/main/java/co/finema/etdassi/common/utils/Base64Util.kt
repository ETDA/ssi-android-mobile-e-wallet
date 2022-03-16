package co.finema.etdassi.common.utils

import co.finema.etdassi.common.helper.KeyHelper
import com.google.gson.Gson

object Base64Util {

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

    // Base64 encoder using apache commons
    val encoder = object : Base64Encoder {
        override fun encodeURLSafe(bytes: ByteArray): String {
            return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(bytes)
        }

        override fun encode(bytes: ByteArray): String {
            return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes)
        }
    }


     val decoder = object : Base64Decoder {
        override fun decode(bytes: ByteArray): ByteArray {
            return org.apache.commons.codec.binary.Base64.decodeBase64(bytes)
        }

        override fun decode(string: String): ByteArray {
            return org.apache.commons.codec.binary.Base64.decodeBase64(string)
        }
    }
}