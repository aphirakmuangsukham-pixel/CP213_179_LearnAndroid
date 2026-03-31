package com.example.lablearnandroid.ui.utils

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SharedPreferencesUtilTest {

    private val context = mockk<Context>()
    private val sharedPreferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()

    @Before
    fun setUp() {
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } returns Unit

        SharedPreferencesUtil.init(context)
    }

    @Test
    fun `saveString should call putString and apply`() {
        val key = "testKey"
        val value = "testValue"

        SharedPreferencesUtil.saveString(key, value)

        verify { editor.putString(key, value) }
        verify { editor.apply() }
    }

    @Test
    fun `getString should return correct value (Pass case)`() {
        val key = "testKey"
        val value = "testValue"
        every { sharedPreferences.getString(key, "") } returns value

        val result = SharedPreferencesUtil.getString(key)

        assertEquals(value, result)
    }

    @Test
    fun `getString should return default value when key missing (Fail case)`() {
        val key = "missingKey"
        val defaultValue = "default"
        every { sharedPreferences.getString(key, defaultValue) } returns null

        val result = SharedPreferencesUtil.getString(key, defaultValue)

        assertEquals(defaultValue, result)
    }

    @Test
    fun `saveInt and getInt should work correctly`() {
        val key = "age"
        val value = 25
        every { sharedPreferences.getInt(key, 0) } returns value

        SharedPreferencesUtil.saveInt(key, value)
        val result = SharedPreferencesUtil.getInt(key)

        verify { editor.putInt(key, value) }
        assertEquals(value, result)
    }

    @Test
    fun `saveBoolean and getBoolean should work correctly`() {
        val key = "isLoggedIn"
        val value = true
        every { sharedPreferences.getBoolean(key, false) } returns value

        SharedPreferencesUtil.saveBoolean(key, value)
        val result = SharedPreferencesUtil.getBoolean(key)

        verify { editor.putBoolean(key, value) }
        assertEquals(value, result)
    }

    @Test
    fun `remove should call remove and apply`() {
        val key = "testKey"
        
        SharedPreferencesUtil.remove(key)

        verify { editor.remove(key) }
        verify { editor.apply() }
    }

    @Test
    fun `clearAll should call clear and apply`() {
        SharedPreferencesUtil.clearAll()

        verify { editor.clear() }
        verify { editor.apply() }
    }
}
