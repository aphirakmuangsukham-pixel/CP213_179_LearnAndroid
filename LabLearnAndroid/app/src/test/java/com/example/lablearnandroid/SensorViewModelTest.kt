package com.example.lablearnandroid

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.location.Location
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Constructor

class SensorViewModelTest {

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: SensorViewModel

    @Before
    fun setUp() {
        viewModel = SensorViewModel(application)
    }

    @Test
    fun `onSensorChanged should update accelData (Pass case)`() {
        val sensorEvent = createMockSensorEvent(Sensor.TYPE_ACCELEROMETER, floatArrayOf(1f, 2f, 3f))

        viewModel.onSensorChanged(sensorEvent)

        assertEquals(Triple(1f, 2f, 3f), viewModel.accelData.value)
    }

    @Test
    fun `onLocationChanged should update locationData (Pass case)`() {
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns 13.7563
        every { mockLocation.longitude } returns 100.5018

        viewModel.onLocationChanged(mockLocation)

        assertEquals(Pair(13.7563, 100.5018), viewModel.locationData.value)
    }

    /**
     * Helper to create a mock SensorEvent using reflection since the constructor is internal.
     */
    private fun createMockSensorEvent(sensorType: Int, values: FloatArray): SensorEvent {
        val sensorEvent = mockk<SensorEvent>()
        val sensor = mockk<Sensor>()
        
        // Mock sensor type
        every { sensor.type } returns sensorType
        
        // Use reflection to set the internal 'sensor' and 'values' fields if possible, 
        // or just mock the behavior if the ViewModel only accesses these.
        // In our ViewModel, it accesses event?.sensor?.type and event.values.
        
        every { sensorEvent.sensor } returns sensor
        
        // Using reflection to set 'values' field as it's a public field in SensorEvent
        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.setAccessible(true)
        
        // SensorEvent.values is usually fixed size (3 for accelerometer)
        val eventValues = FloatArray(3)
        values.copyInto(eventValues)
        
        // Actually, in a Unit Test (JUnit 4/5), mockk can mock the field access if we are careful,
        // but often it's easier to just use reflection to set the actual field if it were a real object.
        // Since we mocked sensorEvent, let's just mock the field access if MockK supports it, 
        // or just use reflection to set it on a REAL (but created via reflection) object.
        
        // For simplicity in this lab, since it's a mock, we'll try to set the field.
        try {
            valuesField.set(sensorEvent, eventValues)
        } catch (e: Exception) {
            // If field setting fails on mock, we might need a different approach.
            // But usually this works on standard JVM.
        }

        return sensorEvent
    }
}
