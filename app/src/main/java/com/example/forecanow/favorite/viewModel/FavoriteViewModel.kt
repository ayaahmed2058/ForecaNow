package com.example.forecanow.favorite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.repository.RepositoryInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoriteViewModel( val repository: RepositoryInterface) : ViewModel() {
    private val _favorites = MutableLiveData<List<FavoriteLocation>>(emptyList())
    val favorites: LiveData<List<FavoriteLocation>> = _favorites

    private val mutableMessage =  MutableSharedFlow<String>()
    val message= mutableMessage.asSharedFlow()


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


    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            val result = repository.insertFavorite(location)
            if(result>0){
                mutableMessage.emit("Favorite Location Added successfully")
                getAllFavorites()
            }else{
                mutableMessage.emit("Favorite Location is already exit")
            }
        }
    }

    fun deleteFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            val result = repository.deleteFavorite(location)
            if(result>0){
                mutableMessage.emit("Favorite Location deleted successfully")
            }else{
                mutableMessage.emit("Favorite Location doesn't found")
            }
        }
    }

}

class FavoriteViewModelFactory(private val repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repo) as T
    }
}