package com.example.forecanow.favorite.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.repository.RepositoryInterface
import io.mockk.mockk
import com.example.forecanow.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    lateinit var viewModel: FavoriteViewModel
    lateinit var repo: RepositoryInterface

    @Before
    fun setUp (){
        repo = mockk(relaxed = true)
        viewModel = FavoriteViewModel(repo)
    }

    @Test
    fun addFavorite_favoriteLocationTobeAdded_ListIsNotNull() {
        val favoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
        viewModel.addFavorite(favoriteOne)
        val result = viewModel.favorites.getOrAwaitValue {}
        assertThat(result,not(nullValue()))
    }

    @Test
    fun deleteFavorite_ListOfFavoriteLocationToBeDeleteDeleted_ListIsEmpty() {

        val favoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
        val favoriteTwo = FavoriteLocation(name = "Egypt" , country = "Dayrut")

        viewModel.addFavorite(favoriteOne)
        viewModel.addFavorite(favoriteTwo)

        val result = viewModel.favorites.getOrAwaitValue {}
        assertThat(result,not(nullValue()))

        viewModel.deleteFavorite(favoriteTwo)
        viewModel.deleteFavorite(favoriteOne)

        val resultAfterDelete = viewModel.favorites.getOrAwaitValue {}
        assertThat(resultAfterDelete, `is` (emptyList()))

    }

    @Test
    fun deleteFavorite_favoriteLocationToBeDeleted_ListIsDecreasedByOne() {

        val favoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
        val favoriteTwo = FavoriteLocation(name = "Egypt" , country = "Dayrut")

        viewModel.addFavorite(favoriteOne)
        viewModel.addFavorite(favoriteTwo)

        val result = viewModel.favorites.getOrAwaitValue {}
        assertThat(result,not(nullValue()))

        viewModel.deleteFavorite(favoriteTwo)

        val resultAfterDelete = viewModel.favorites.getOrAwaitValue {}
        assertThat(resultAfterDelete,not(nullValue()))

    }

}