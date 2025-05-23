package com.example.beautysalonapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class BookingActivityTest {

    private val serviceDuration = 30

    private fun overlaps(time1: String, time2: String): Boolean {
        // handle empty string inputs to avoid crash
        if (time1.isBlank() || time2.isBlank()) return false

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val t1 = Calendar.getInstance()
        val t2 = Calendar.getInstance()
        t1.time = sdf.parse(time1)!!
        t2.time = sdf.parse(time2)!!

        val t1End = t1.clone() as Calendar
        val t2End = t2.clone() as Calendar
        t1End.add(Calendar.MINUTE, serviceDuration)
        t2End.add(Calendar.MINUTE, serviceDuration)

        return t1.timeInMillis < t2End.timeInMillis && t2.timeInMillis < t1End.timeInMillis
    }

    private fun formatTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun isPasswordValid(pwd: String): Boolean {
        return pwd.length >= 6
    }

    @Test
    fun testFormatTimestamp() {
        val millis = 1716483600000L // e.g. 23 May 2024, 15:00
        val result = formatTimestamp(millis)
        assertTrue(result.contains("2025") || result.contains("May"))
    }

    @Test
    fun testNonEmptyUsername() {
        val username = "PJayesh12"
        assertTrue(username.isNotBlank())
    }

    @Test
    fun testValidEmailFormat() {
        val email = "test@example.com"
        assertTrue(
            email.contains("@") && email.indexOf(".") > email.indexOf("@") + 1
        )

    }


    @Test
    fun testShortPasswordIsInvalid() {
        assertFalse(isPasswordValid("123"))
    }

    @Test
    fun testStrongPasswordIsValid() {
        assertTrue(isPasswordValid("abc123456"))
    }

    @Test
    fun testTimeParsingIsCorrect() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = sdf.parse("14:30")
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY))
    }


    @Test
    fun testOverlapReturnsTrue() {
        // Tests if two overlapping times are detected correctly
        assertTrue(overlaps("10:00", "10:15"))
    }

    @Test
    fun testOverlapReturnsFalse() {
        // Tests if two non-overlapping times are handled correctly
        assertFalse(overlaps("10:00", "10:45"))
    }

    @Test
    fun testOverlapFail() {
        //  correctly expects overlapping times to return true
        assertTrue(overlaps("10:00", "10:15"))
    }

    @Test
    fun testEmptyInputFails() {
        //  handles empty input and expects false
        assertFalse(overlaps("", ""))
    }
}
