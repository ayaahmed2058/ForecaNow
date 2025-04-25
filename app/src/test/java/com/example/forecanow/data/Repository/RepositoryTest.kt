package com.example.forecanow.data.Repository

import com.example.forecanow.data.db.FakeWeatherLocalDataSource
import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.db.WeatherAlert
import com.example.forecanow.data.network.FakeWeatherRemoteDataSource
import com.example.forecanow.data.repository.RepositoryImp
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    private val alertOne = WeatherAlert(startTime = 23456 , endTime = 78910 , alertType = "Alert")
    private val alertTwo = WeatherAlert(startTime = 23456 , endTime = 78910 , alertType = "Notification")

    private val FavoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
    private val FavoriteTwo = FavoriteLocation(name = "Egypt" , country = "Dayrut")

    private val favList = listOf<FavoriteLocation> (FavoriteOne,FavoriteTwo)
    private val alertList = listOf<WeatherAlert> (alertOne,alertTwo)

    private lateinit var fakeLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var repository: RepositoryImp

    @Before
    fun setUp(){
        fakeRemoteDataSource = FakeWeatherRemoteDataSource()
        fakeLocalDataSource = FakeWeatherLocalDataSource(alertList.toMutableList(),favList.toMutableList())
        repository = RepositoryImp.getInstance(fakeRemoteDataSource,fakeLocalDataSource)
    }

    @Test
    fun addAlert_weatherAlert_resultGreaterThanZero() = runTest(){

        val insertResult = repository.insertAlert(alertOne)

//        val result = repository.getAllAlerts().first()
//        val alertOneTest = result[0]

        assertTrue("insert result should be greater than 0", insertResult > 0)

    }

    @Test
    fun deleteAlert_weatherAlert_resultGreaterThanZero () = runTest(){

        val insertResult = repository.insertAlert(alertTwo)
        assertTrue("insert Result should be greater than 0", insertResult > 0)
        val deleteResult = repository.deleteAlert(alertTwo)
        assertTrue("delete Result should be greater than 0", deleteResult > 0)
    }

    @Test
    fun addFavorite_favoriteLocation_resultGreaterThanZero() = runTest(){

        val insertResult = repository.insertFavorite(FavoriteOne)

//        val result = repository.getAllAlerts().first()
//        val alertOneTest = result[0]

        assertTrue("insert result should be greater than 0", insertResult > 0)

    }

    @Test
    fun deleteFavorite_FavoriteLocation_resultGreaterThanZero () = runTest(){

        val insertResult = repository.insertFavorite(FavoriteTwo)
        assertTrue("insert Result should be greater than 0", insertResult > 0)
        val deleteResult = repository.deleteFavorite(FavoriteTwo)
        assertTrue("delete Result should be greater than 0", deleteResult > 0)
    }
}