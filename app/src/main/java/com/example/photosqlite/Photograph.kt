package com.example.photosqlite

// Photograph.kt
data class Photograph(
    val id: Long,
    val image: ByteArray,
    val description: String
) {
    // Es necesario sobreescribir equals y hashCode para que los ByteArray
    // se comparen correctamente, aunque para este ejercicio no es crucial.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Photograph
        if (id != other.id) return false
        if (!image.contentEquals(other.image)) return false
        if (description != other.description) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}