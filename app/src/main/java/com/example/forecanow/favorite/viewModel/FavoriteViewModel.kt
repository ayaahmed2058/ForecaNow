package com.example.forecanow.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.repository.RepositoryInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel( val repository: RepositoryInterface) : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    init {
        getAllFavorites()
    }

    fun getAllFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        return repository.getFavoriteById(id)
    }

    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.insertFavorite(location)
        }
    }

    fun deleteFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteFavorite(location)
        }
    }

}

class FavoriteViewModelFactory(private val repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repo) as T
    }
}