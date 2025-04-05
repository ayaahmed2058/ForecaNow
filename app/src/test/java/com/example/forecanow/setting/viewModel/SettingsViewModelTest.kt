package com.example.forecanow.setting.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.forecanow.data.repository.RepositoryInterface
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    lateinit var viewModel: SettingsViewModel
    lateinit var repo: RepositoryInterface

    @Before
    fun setUp (){
        repo = mockk(relaxed = true)
        viewModel = SettingsViewModel(repo)
    }


}