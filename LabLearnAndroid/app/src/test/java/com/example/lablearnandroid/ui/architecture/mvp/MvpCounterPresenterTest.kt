package com.example.lablearnandroid.ui.architecture.mvp

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MvpCounterPresenterTest {

    private val view = mockk<MvpCounterView>(relaxed = true)
    private val model = mockk<MvpCounterModel>(relaxed = true)
    private lateinit var presenter: MvpCounterPresenter

    @Before
    fun setUp() {
        presenter = MvpCounterPresenter(view, model)
    }

    @Test
    fun `onIncrementClicked should update model and tell view to show new count (Pass case)`() {
        // Given
        val expectedCount = 5
        every { model.getCount() } returns expectedCount

        // When
        presenter.onIncrementClicked()

        // Then
        verify { model.incrementCounter() }
        verify { view.showCount(expectedCount) }
    }

    @Test
    fun `multiple clicks should result in multiple view updates`() {
        // Given
        every { model.getCount() } returns 1 andThen 2

        // When
        presenter.onIncrementClicked()
        presenter.onIncrementClicked()

        // Then
        verify(exactly = 2) { model.incrementCounter() }
        verify { view.showCount(1) }
        verify { view.showCount(2) }
    }
}
