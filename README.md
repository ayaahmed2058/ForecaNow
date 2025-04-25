## Weather Forecast Application


https://github.com/user-attachments/assets/cdb4e894-17a5-4bcd-8cfe-8caa32e87e22


An Android mobile application that displays the weather status and temperature based on your current location. You can also select a specific location on the map, then add it to a list of favorite locations to view detailed weather information.  
Additionally, the app allows users to set alerts for different weather conditions such as rain, wind, very low or high temperatures, fog, snow, and more.


## Application Screens & Features

### Home Screen
Displays:
- Current temperature  
- Current date and time  
- Humidity  
- Wind speed  
- Pressure  
- Cloud coverage  
- City name  
- Weather icon 
- Weather description (e.g., clear sky, light rain)  
- Hourly forecast for the current day  
- Daily forecast for the next 5 days  


### Settings Screen
Allows the user to:
- **Choose Location:**
  - Use GPS
  - Pick a specific location from the map  
- **Select Units:**
  - Temperature: Kelvin, Celsius, Fahrenheit  
  - Wind Speed: meter/sec, miles/hour  
- **Select Language:**
  - Arabic or English


### Weather Alerts Screen
Allows users to:
- Add a new weather alert  
- Choose:
  - Duration of the alert  
  - Type of alert (notification or default alarm sound)  


### Favorite Screen
- Lists all saved favorite locations  
- Clicking a location shows full forecast details   
- Ability to remove saved locations  


## API Used

The app uses the OpenWeatherMap Forecast API:  
🔗 [OpenWeatherMap 5-Day Forecast API](https://api.openweathermap.org/data/2.5/forecast)  


## Technologies Used

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Room Database
- Coroutines
- OpenWeatherMap API
- Lottie Animations
