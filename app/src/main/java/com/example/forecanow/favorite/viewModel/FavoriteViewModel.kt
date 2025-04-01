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

class FavoriteViewModel(private val repository: RepositoryInterface) : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _navigateToDetails = MutableSharedFlow<FavoriteLocation>()
    val navigateToDetails = _navigateToDetails.asSharedFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()

    init {
        getAllFavorites()
    }

    fun getAllFavorites(){
        viewModelScope.launch {
            repository.getAllFavorites().collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    fun onFavoriteClicked(favorite: FavoriteLocation) {
        viewModelScope.launch {
            _navigateToDetails.emit(favorite)
        }
    }

    fun showAddFavoriteDialog() {
        _showAddDialog.value = true
    }

    fun dismissAddFavoriteDialog() {
        _showAddDialog.value = false
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