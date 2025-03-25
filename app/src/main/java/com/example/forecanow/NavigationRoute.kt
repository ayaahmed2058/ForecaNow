package com.example.forecanow

import kotlinx.serialization.Serializable


@Serializable
sealed class NavigationRoute{

    @Serializable
    object Home: NavigationRoute()

    @Serializable
    object Favourite: NavigationRoute()

    @Serializable
    object Alarm: NavigationRoute()

    @Serializable
    object Setting: NavigationRoute()

}