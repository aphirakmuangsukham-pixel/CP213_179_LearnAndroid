package com.example.lablearnandroid.ui.architecture.mvvm

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MvvmCounterViewModelTest {

    private lateinit var viewModel: MvvmCounterViewModel

    @Before
    fun setUp() {
        viewModel = MvvmCounterViewModel()
    }

    @Test
    fun `initial count should be 0`() {
        assertEquals(0, viewModel.count.value)
    }

    @Test
    fun `onIncrementClicked should increase count`() {
        viewModel.onIncrementClicked()
        assertEquals(1, viewModel.count.value)
        
        viewModel.onIncrementClicked()
        assertEquals(2, viewModel.count.value)
    }
}
