package com.example.lablearnandroid

import com.example.lablearnandroid.ui.utils.PokedexResponse
import com.example.lablearnandroid.ui.utils.PokemonEntry
import com.example.lablearnandroid.ui.utils.PokemonNetwork
import com.example.lablearnandroid.ui.utils.PokemonSpecies
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(PokemonNetwork)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `fetchPokemon should update pokemonList when API success (Pass case)`() = runTest {
        // Mock data
        val mockEntries = listOf(
            PokemonEntry(1, PokemonSpecies("Bulbasaur", "url1")),
            PokemonEntry(2, PokemonSpecies("Ivysaur", "url2"))
        )
        val mockResponse = PokedexResponse(mockEntries)

        // Mock API call
        coEvery { PokemonNetwork.api.getKantoPokedex() } returns mockResponse

        // Create ViewModel
        val viewModel = PokemonViewModel()
        
        // Wait for fetchPokemon (which is called in init)
        advanceUntilIdle()

        // Verify
        assertEquals(mockEntries, viewModel.pokemonList.value)
    }

    @Test
    fun `fetchPokemon should handle exception when API fails (Fail case)`() = runTest {
        // Mock API error
        coEvery { PokemonNetwork.api.getKantoPokedex() } throws Exception("Network Error")

        // Create ViewModel
        val viewModel = PokemonViewModel()
        
        // Wait for fetchPokemon
        advanceUntilIdle()

        // Verify that the list is empty (default state)
        assertTrue(viewModel.pokemonList.value.isEmpty())
    }
}
