package com.example.forecanow.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@RunWith(AndroidJUnit4::class)
class WeatherLocalDataSourceTest {



        private lateinit var dao: WeatherDao
        private lateinit var database: WeatherDatabase
        private lateinit var localDataSource: WeatherLocalDataSourceImp

        @Before
        fun setUp(){
            database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                WeatherDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()

            dao = database.weatherDao()
            localDataSource = WeatherLocalDataSourceImp(dao)
        }

        @After
        fun dearDown()= database.close()


    @Test
    fun addAlertUsingInsertAlertFromDao() = runTest {
        val alertOne = WeatherAlert(id = 6, startTime = 23456, endTime = 78910, alertType = "Alert")

        localDataSource.addAlert(alertOne)

        val result = localDataSource.getStoredAlerts().first()
        assertThat(result,not(nullValue()))

        assertTrue("Alert not added", result.contains(alertOne))

    }

    @Test
    fun deleteAlertUsingDeleteAlertFromDao() = runTest {
        val alertOne = WeatherAlert(id = 6, startTime = 23456, endTime = 78910, alertType = "Alert")
        val alertTwo = WeatherAlert(startTime = 23456 , endTime = 78910 , alertType = "Notification")

        localDataSource.addAlert(alertOne)
        localDataSource.addAlert(alertTwo)

        val result = localDataSource.getStoredAlerts().first()
        assertThat(result,not(nullValue()))

        localDataSource.deleteAlert(alertTwo)

        val resultAfterDelete = localDataSource.getStoredAlerts().first()

        assertFalse("Alert not deleted" , result.contains(alertTwo))

    }


    @Test
    fun addFavoriteUsingInsertFavoriteFromDao() = runTest {

        val favoriteOne = FavoriteLocation(id = 7, name = "Egypt" , country = "Giza")

        localDataSource.insertFavorite(favoriteOne)

        val result = localDataSource.getAllFavorites().first()
        assertThat(result,not(nullValue()))

        assertTrue("Favorite Location not added", result.contains(favoriteOne))

    }

    @Test
    fun deleteFavoriteUsingDeleteFavoriteFromDao() = runTest {
        val favoriteOne = FavoriteLocation(name = "Egypt" , country = "Giza")
        val favoriteTwo = FavoriteLocation(name = "Egypt" , country = "Dayrut")

        localDataSource.insertFavorite(favoriteOne)
        localDataSource.insertFavorite(favoriteOne)

        val result = localDataSource.getAllFavorites().first()
        assertThat(result,not(nullValue()))

        localDataSource.deleteFavorite(favoriteTwo)

        val resultAfterDelete = localDataSource.getAllFavorites().first()

        assertFalse("Alert not deleted" , resultAfterDelete.contains(favoriteTwo))

    }

}