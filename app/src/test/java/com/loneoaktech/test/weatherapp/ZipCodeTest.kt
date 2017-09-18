package com.loneoaktech.test.weatherapp

import com.loneoaktech.test.weatherapp.model.ZipCode
import org.junit.Test

import org.junit.Assert.*

/**
 * Never trust a regex
 * Created by BillH on 9/17/2017.
 */
class ZipCodeTest {

    @Test
    fun regex_correct() {
        assertTrue( ZipCode.isValidZipCodeString("12345"))
        assertTrue( ZipCode.isValidZipCodeString("   00000 "))
        assertFalse( ZipCode.isValidZipCodeString( "1234556"))
        assertFalse( ZipCode.isValidZipCodeString( "1235a"))
    }

    @Test
    fun construction_correct() {
        assertNotNull(ZipCode.fromStringOrNull("12345"))
        assertNull( ZipCode.fromStringOrNull("abce"))
    }

    @Test
    fun equals_correct() {
        val z1 = ZipCode.fromString("06482")
        val z2 = ZipCode.fromString("10549")
        val z3 = ZipCode.fromString("  06482")

        assertEquals(z1, z3)
        assertNotEquals(z1, z2)
    }

}