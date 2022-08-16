package com.teddyfreddy.common.network

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val yyyy = value.year.toString().padStart(4, '0')
        val MM = value.monthNumber.toString().padStart(2, '0')
        val dd = value.dayOfMonth.toString().padStart(2, '0')
        val hh = value.hour.toString().padStart(2, '0')
        val mm = value.minute.toString().padStart(2, '0')
        val ss = value.second.toString().padStart(2, '0')
        val string = "${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString() // yyyy-MM-ddThh:mm:ss
        return LocalDateTime(
            string.substring(0, 4).toInt(),
            string.substring(5, 7).toInt(),
            string.substring(8, 10).toInt(),
            string.substring(11, 13).toInt(),
            string.substring(14, 16).toInt(),
            string.substring(17, 19).toInt()
        )
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val yyyy = value.year.toString().padStart(4, '0')
        val MM = value.monthNumber.toString().padStart(2, '0')
        val dd = value.dayOfMonth.toString().padStart(2, '0')
        val string = "${yyyy}-${MM}-${dd}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString() // yyyy-MM-dd
        return LocalDate(
            string.substring(0, 4).toInt(),
            string.substring(5, 7).toInt(),
            string.substring(8, 10).toInt()
        )
    }
}