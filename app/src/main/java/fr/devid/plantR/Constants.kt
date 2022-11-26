package fr.devid.plantR

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import fr.devid.plantR.models.Categories
import fr.devid.plantR.ui.myPlants.CategorieModels
import fr.devid.plantR.utils.Utils
import java.util.ArrayList

object Constants {
    var utils  = Utils()

    val array : ArrayList<CategorieModels> = arrayListOf(
        CategorieModels(Categories("Aromatique", "l'Aromatique est en fait une...", "mon url")),
        CategorieModels(Categories("Legume", "Le légume est ...", "mon url")),
        CategorieModels(Categories("Fruit", "Le fruit est un...", "mon url")))

    const val IS_SUCCESS_API_SEARCHED = 200
    const val WeatherApiKey: String = "a231c70247581b21d3fd566f1fd91818" // Use your own API key
    const val youtubeApiKey = "AIzaSyC4858FBQ1o5wvbdfaH0k0WF6SqVeUwFsY"

    const val BASE_URL = BuildConfig.BASE_URL
   // const val BASE_URL_LIVE_OBJECTS = BuildConfig.BASE_URL_LIVE_OBJECTS

    //JARDIN = 0
    //TERASSE = 1
    //BALCON = 2
    //Rambarde de fenêtre = 3
    //Intérieur = 4

    val arrayEmplacement : ArrayList<Int> = arrayListOf(R.string.not_defined, R.string.location_1,  R.string.location_2,  R.string.location_3, R.string.location_4,  R.string.location_5)

    //A l'ombre toute la journée = 0
    //Soleil le matin = 1
    //Soleil l'après-midi = 2
    //Soleil toute la journée = 3

    val arrayEnsoleillement : ArrayList<Int> = arrayListOf(R.string.not_defined, R.string.sunlight_1, R.string.sunlight_2, R.string.sunlight_3, R.string.sunlight_4)


    val arrayOrientation : ArrayList<Int> = arrayListOf(R.string.not_defined,R.string.orientation_place_1 , R.string.orientation_place_2, R.string.orientation_place_3,R.string.orientation_place_4, R.string.orientation_place_5, R.string.orientation_place_6, R.string.orientation_place_7, R.string.orientation_place_8)
    val arrayRangsOrStages : ArrayList<String> = arrayListOf("Etages", "Rangs")

    lateinit var life : LifecycleOwner

    var arrayList = arrayListOf<String>()

    const val ADRESSE_URL = "https://api.openweathermap.org/data/2.5/weather?"
    const val ADRESS_API_KEY = "98fb899cb6254d51b9e29e294af38106"
    val livePoukou = MutableLiveData<String>("Empty")

}