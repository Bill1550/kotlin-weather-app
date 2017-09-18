package com.loneoaktech.test.weatherapp.model

import java.util.regex.Pattern

/**
 * Simple class to enforce ZipCode validity
 * Created by BillH on 9/16/2017.
 */
class ZipCode(private val _zip: Int){

    companion object {

        fun fromString(value: String): ZipCode {
            val zi = parse(value) ?: throw IllegalArgumentException("Bad zip code format '$value'")
            return ZipCode(zi)
        }

        fun fromStringOrNull(value: String): ZipCode? {
            val zi = parse(value) ?: return null

            return ZipCode(zi)
        }

        private val VALIDATE_REGEX = Pattern.compile("""^\s*(\d{1,5})\s*$""")
        private fun parse(zip: String): Int? {
            with(VALIDATE_REGEX.matcher(zip)) {
                if (!find())
                    return null
                return group(1).toInt()
            }
        }

        /**
         * Checks string to make sure that it is a valid looking zip code.
         * Does not check if it is an actual valid zip code
         */
        fun isValidZipCodeString(value: String): Boolean = VALIDATE_REGEX.matcher(value).find()
    }

    override fun toString(): String = String.format("%05d", _zip)

    override operator fun equals(other: Any?): Boolean = when (other ) {
        is ZipCode -> other._zip == _zip
        else -> false
    }

    override fun hashCode(): Int = _zip.hashCode()

}
