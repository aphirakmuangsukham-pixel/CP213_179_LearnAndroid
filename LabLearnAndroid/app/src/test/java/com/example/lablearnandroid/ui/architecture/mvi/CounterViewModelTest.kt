package com.example.lablearnandroid.ui.architecture.mvi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CounterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CounterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have count 0`() {
        assertEquals(0, viewModel.state.value.count)
    }

    @Test
    fun `processIntent IncrementCounter should increase count (Pass case)`() = runTest {
        viewModel.processIntent(CounterIntent.IncrementCounter)
        
        // Advance dispatcher to execute the launch block
        advanceUntilIdle()
        
        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `multiple IncrementCounter intents should increase count accordingly`() = runTest {
        viewModel.processIntent(CounterIntent.IncrementCounter)
        viewModel.processIntent(CounterIntent.IncrementCounter)
        viewModel.processIntent(CounterIntent.IncrementCounter)
        
        advanceUntilIdle()
        
        assertEquals(3, viewModel.state.value.count)
    }
}
