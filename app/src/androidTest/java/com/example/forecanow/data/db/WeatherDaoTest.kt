package com.example.forecanow.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.runner.RunWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var dao: WeatherDao
    private lateinit var database: WeatherDatabase


    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao = database.weatherDao()
    }

    @After
    fun dearDown()= database.close()


    @Test
    fun insertAlert_alertTobeAdded_notNullList() = runTest(){
        val alertOne = WeatherAlert(id = 6, startTime = 23456, endTime = 78910, alertType = "Alert", isActive = true)

        dao.insertAlert(alertOne)
        val result = dao.getAllAlerts().first()

        assertThat(result,not(nullValue()))

    }

    @Test
    fun deleteAlert_alertTobeDeleted_ListSizeDecreaseByONe() = runTest(){
        val alertOne = WeatherAlert(id = 7, startTime = 23456, endTime = 78910, alertType = "Alert", isActive = true)
        val alertTwo = WeatherAlert(id = 8, startTime = 23456 , endTime = 78910 , alertType = "Notification")

        dao.insertAlert(alertOne)
        dao.insertAlert(alertTwo)
        val result = dao.getAllAlerts().first()
        assertThat(result,not(nullValue()))

        dao.deleteAlert(alertOne)

        val resultAfterDelete = dao.getAllAlerts().first()

        assertFalse("Alert not deleted" , resultAfterDelete.contains(alertOne))
    }

    @Test
    fun deleteFavorite_favoriteTobeDeleted_ListSizeDecreaseByONe() = runTest {
        val favoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
        val favoriteTwo = FavoriteLocation(name = "Egypt" , country = "Dayrut")

        dao.insertFavorite(favoriteOne)
        dao.insertFavorite(favoriteOne)

        val result = dao.getAllFavorites().first()
        assertThat(result,not(nullValue()))

        dao.deleteFavorite(favoriteTwo)

        val resultAfterDelete = dao.getAllFavorites().first()

        assertFalse("Alert not deleted" , resultAfterDelete.contains(favoriteTwo))

    }

    @Test
    fun insertFavorite_favoriteTobeAdded_notNullList() = runTest {

        val favoriteOne = FavoriteLocation(id = 7, name = "Egypt" , country = "Giza")

        dao.insertFavorite(favoriteOne)

        val result = dao.getAllFavorites().first()

        assertThat(result,not(nullValue()))

        assertTrue("Favorite Location not added", result.contains(favoriteOne))

    }




}