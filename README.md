# README #

An example app using Architecture Components and Kotlin running under Oreo.

### Basic Function ###

The application displays the current weather for a selected zip code.

 To Run this app, you must get an API key from DarkSky at https://darksky.net/dev. Insert you key into the AppParameters.kt file.

When weather is displayed, it is updated every minute.
When the app is not running, the weather for the last selected location is updated in the background so that will be displayed immediately on restart.
The backgroud processing complies with the Oreo background restrictions by using the JobScheduler

### Libries used ###

* Android Archtecture Components (LiveView, ViewModel, Lifecycle)
* Dagger 2 - for dependency injection
* Timber - to provide logging
* com.github.johnhoitt:DarkSky - to read from the DarkSky API (uses Retrofit)
* Gson - for serialziing forecast data for caching.

 
