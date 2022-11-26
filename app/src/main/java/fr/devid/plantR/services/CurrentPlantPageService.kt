package fr.devid.plantR.services


import fr.devid.plantR.models.SortOfPlant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPlantPageService @Inject constructor(){

    var currentPage : String? = null
    var currentPlantModel : List<SortOfPlant> = emptyList()
    var currentPlantModelRangs : List<SortOfPlant> = emptyList()
    var gardenerId : String? = null
}