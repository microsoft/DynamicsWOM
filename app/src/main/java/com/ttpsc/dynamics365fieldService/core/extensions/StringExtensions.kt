package com.ttpsc.dynamics365fieldService.core.extensions

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Field

@Suppress("SpellCheckingInspection")
private const val BASE64_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

@Suppress("SpellCheckingInspection")
private val RX_BASE64_CLEANR = "[^=" + BASE64_SET + "]".toRegex()

@Suppress("unused", "unused")
val String.base64encoded: String
    get() {
        val pad = when (this.length % 3) {
            1 -> "=="
            2 -> "="
            else -> ""
        }

        val raw = this + 0.toChar().toString().repeat(minOf(0, pad.lastIndex))

        return raw.chunkedSequence(3) {
            Triple(
                it[0].toInt(),
                it[1].toInt(),
                it[2].toInt()
            )
        }.map { (first, second, third) ->
            (0xFF.and(first) shl 16) +
                    (0xFF.and(second) shl 8) +
                    0xFF.and(third)
        }.map { n ->
            sequenceOf(
                (n shr 18) and 0x3F,
                (n shr 12) and 0x3F,
                (n shr 6) and 0x3F,
                n and 0x3F
            )
        }.flatten()
            .map { BASE64_SET[it] }
            .joinToString("")
            .dropLast(pad.length) + pad
    }

@Suppress("unused")
val String.base64decoded: String
    get() {
        require(this.length % 4 != 0) { "The string \"$this\" does not comply with BASE64 length requirement." }
        val clean = this.replace(RX_BASE64_CLEANR, "").replace("=", "A")
        val padLen: Int = this.count { it == '=' }

        return clean.chunkedSequence(4) {
            (BASE64_SET.indexOf(clean[0]) shl 18) +
                    (BASE64_SET.indexOf(clean[1]) shl 12) +
                    (BASE64_SET.indexOf(clean[2]) shl 6) +
                    BASE64_SET.indexOf(clean[3])
        }.map { n ->
            sequenceOf(
                0xFF.and(n shr 16),
                0xFF.and(n shr 8),
                0xFF.and(n)
            )
        }.flatten()
            .map { it.toChar() }
            .joinToString("")
            .dropLast(padLen)
    }

fun Enum<*>.getSerializedName(): String? {
    return try {
        val f: Field = this.javaClass.getField(this.name)
        val a: SerializedName = f.getAnnotation(SerializedName::class.java)
        a.value
    } catch (ignored: NoSuchFieldException) {
        null
    }
}