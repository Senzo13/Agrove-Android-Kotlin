package fr.devid.plantR.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlin.String

@IgnoreExtraProperties
data class CheckIsPublic(var ispublic: Boolean = false)
data class SubscribeMember(var uid: String, var metadata: Metadata = Metadata(), var guid : String)

@IgnoreExtraProperties
data class Geoapify(
        var features : ArrayList<PaysProperties> = arrayListOf()
)

@IgnoreExtraProperties
data class Pays(
        var name : String = "",
        var code : String = ""
)

@IgnoreExtraProperties
data class Irrigation(
        var received : Received = Received(),
        var payload : String = ""
)

@IgnoreExtraProperties
data class Received(
        var request : RequestIrrig = RequestIrrig(),
        var status : String ="",
        var updated : String =""
)

@IgnoreExtraProperties
data class RequestIrrig(
        var value: GetData = GetData()
)

@IgnoreExtraProperties
data class GetData(
        var data : String ="",
        var port : Int = -1
)




@IgnoreExtraProperties
data class ListPaysModel(
        var data : ArrayList<Pays> = ArrayList()
)

@IgnoreExtraProperties
data class ListPaysModelIndexed(
        var data : HashMap<Int, Pays> = HashMap()
)

@IgnoreExtraProperties
data class PaysProperties(
    var properties : PaysData = PaysData()
)

@IgnoreExtraProperties
data class PaysData(
            var city: String? = null,
            var country_code: String? = null,
            var country: String? = null,
            var post_code: String? = null
)

@IgnoreExtraProperties
data class GardenerClimat(
        var humidity: Int? = null,
        var luminosity: Int? = null,
        var pressure: Int? = null,
        var temperature: Int? = null
)

@IgnoreExtraProperties
data class Gardener(
        var metadata : GardenerMetadata = GardenerMetadata(),
        var stats : GardenerClimat = GardenerClimat()
)

@IgnoreExtraProperties
data class GardenerData(
        var metadata : GardenerMetadata = GardenerMetadata(),
        var stats : GardenerClimat = GardenerClimat(),
        var stage : String = "",
        var type : String = "",
        var loraTs : String = ""
)

@IgnoreExtraProperties
data class Ticket(
        var dateOfPost : Double = 0.1,
        var priority : Int = -1,
        var state : Int = 0,
        var title : String = "",
        var messages : List<Topic> = emptyList()
)

@IgnoreExtraProperties
data class Association(
        var name : String = ""
)

@IgnoreExtraProperties
data class Topic(
        var date : Double = 0.1,
        var message : String = "",
        var userId : String = ""
)

@IgnoreExtraProperties
data class Capteur(
        var DeviceID : String = "",
        var ssnID : String = "",
        var name : String = "",
        var value : Double = 0.0
)

@IgnoreExtraProperties
data class ClassicGardener(
        var ispublic: Boolean = false,
        var stage: String = "",
        var metadata: GardenerMetadata = GardenerMetadata(),
        var owners : HashMap<String, Boolean> = HashMap(),
        var type : String = "",
        var dimension : Int = -1
)

@IgnoreExtraProperties
data class DoneBy(var date: Double = 0.1, var userId: String = "")

@IgnoreExtraProperties
data class GardenerStats(
        var humidity: Float? = null,
        var battery: Int? = null,
        var soilMisture: Int? = null,
        var luminosity: Int? = null,
        var temperature: Float? = null,
        var pressure : Float?= null,
        var waterLevel: Int? = null,
        var capacities: Capacities = Capacities()
)

@IgnoreExtraProperties
data class ProgressBarObject(
        var battery: Int? = null,
        var waterLevel: Int? = null,
        var capacities: Int? = null
)


@IgnoreExtraProperties
data class Capacities(
        var c1 : Int? = null,
        var c2 : Int? = null,
        var c3 : Int? = null,
        var c4 : Int? = null,
        var c5 : Int? = null
)

@IgnoreExtraProperties
data class GardenerTips(
        var battery: Boolean = false,
        var capa: Boolean = false,
        var temperature: Boolean = false,
        var waterLevel: Boolean = false
)

@IgnoreExtraProperties
data class GardenerInvit(var invitation: String = "")

@IgnoreExtraProperties
data class HomeGardener(
        var id: String = "",
        var name: String = ""
)

@IgnoreExtraProperties
data class Categorie(
        var name: String = ""
)

@IgnoreExtraProperties
data class FriendsGardeners(
        var friends: HashMap<String, Boolean> = HashMap()
)

data class ListsModels(var name: User)

@IgnoreExtraProperties
data class GardenerMetadata(
        var address: String = "",
        var countryCode: String = "",
        var zipCode : String = "",
        var city: String = "",
        var emplacement : Int = 0,
        var ensoleillement : Int = 0,
        var images: HashMap<String, Boolean> = HashMap(),
        var name: String = "",
        var orientation : Int = 0)

@IgnoreExtraProperties
data class WeatherApp(
        var main : WeatherMain = WeatherMain(),
        val cod : Int? = null,
        val message : String? = null,
        var name : String
)

data class WeatherMain(
        var temp : Double? = null,
        var humidity : Double? = null,
        var pressure : Double? = null
)
@IgnoreExtraProperties
data class GardenerMetadataOwners(
        var address: String = "",
        var city: String = "",
        var owners : HashMap<String, Boolean> = HashMap(),
        var emplacement : Int = 0,
        var ensoleillement : Int = 0,
        var images: HashMap<String, Boolean> = HashMap(),
        var name: String = "",
        var orientation : Int = 0
)

@IgnoreExtraProperties
data class GardenerStage(
        var stage: String = "",
        var rangs: Int? = null,
        var ispublic: Boolean = false,
        var gardenerParent : String? = null,
        var metadata: GardenerMetadata = GardenerMetadata(),
        var type : String = "",
        var dimension : Int = -1
)

@IgnoreExtraProperties
data class InitGardener(
        var ispublic: Boolean = false,
        var type : String = "",
        var dimension : Int = -1
)
@IgnoreExtraProperties
data class UserGardenerGuest(
        var gardenersGuest: HashMap<String, Boolean> = HashMap()
)

@IgnoreExtraProperties
data class HomeCategories(
        var id: String = "",
        var name: String = ""
)

@IgnoreExtraProperties
data class Metadata(
        var firstName: String = "",
        var lastName: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "firstName" to firstName,
                "lastName" to lastName
        )
    }
}

@IgnoreExtraProperties
data class Weather(
        var weather: WeatherDesc = WeatherDesc(),
        var main: Main = Main(),
        var wind: Wind = Wind()
)

@IgnoreExtraProperties
data class WeatherDesc(
        var main: String = ""
)

@IgnoreExtraProperties
data class Main(
        var humidity: Int = 0,
        var temp_min: Double = 0.0,
        var temp_max: Double = 0.0,
        var temp: Double = 0.0
)

@IgnoreExtraProperties
data class Wind(
        var speed: Double = 0.0
)


@IgnoreExtraProperties
data class TeamList(
        var uid: String,
        var firstName: String,
        var lastName: String
) {
    @Exclude
    fun toMap(): Map<String, String> {
        return mapOf(
                "uid" to uid,
                "firstName" to firstName,
                "lastName" to lastName

        )
    }
}

@IgnoreExtraProperties
data class User(
        var currentGardener: String = "",
        var gardeners: HashMap<String, Boolean> = HashMap(),
        var Metadata: Metadata = Metadata()
)

@IgnoreExtraProperties
data class CurrentGardener(var currentGardener: String)

@IgnoreExtraProperties
data class ImageModelList(
        var id: String = "",
        var gardenersImageKeys: List<String> = emptyList(),
        var hasLogo: Boolean = false,
        var gardenerName: String = ""
)

@IgnoreExtraProperties
data class PlantsToAdd(var plantID: String, var plantName: String) {
    @Exclude
    fun toMap(): Map<String, String> {
        return mapOf(
                "plantID" to plantID,
                "plantName" to plantName
        )
    }
}
@IgnoreExtraProperties
data class PlantsToAddPreviousDate(var plantID: String, var plantName: String, var previousDate : Double) {
}

@IgnoreExtraProperties
data class Plant(
        var association: HashMap<String, Boolean> = HashMap(),
        var characteristic: Characteristic? = null,
        var description: String = "",
        var name: String = "",
        var sowingPeriod: SowingPeriod? = null,
        var plantingPeriod: PlantingPeriod? = null,
        var filtre: HashMap<String, Boolean> = HashMap(),
        var harvestPeriod: HarvestPeriod? = null,
        var tasks :  ArrayList<Tasks> = arrayListOf(),
        var nuisibles : HashMap<String, Boolean> = HashMap())

@IgnoreExtraProperties
data class Plant2(
        var association: HashMap<String, Boolean> = HashMap(),
        var characteristic: Characteristic? = null,
        var description: String = "",
        var name: String = "",
        var sowingPeriod: SowingPeriod? = null,
        var plantingPeriod: PlantingPeriod? = null,
        var filtre: HashMap<String, Boolean> = HashMap(),
        var harvestPeriod: HarvestPeriod? = null,
        var tasks :  ArrayList<Tasks> = arrayListOf())

@IgnoreExtraProperties
data class PlantFavs(
        var sowingPeriod: SowingPeriod? = null,
        var plantingPeriod: PlantingPeriod? = null
)

@IgnoreExtraProperties
//Allows to be use during the test phase
data class PlantTest(
        var description: String = "",
        var name: String = "",
        var harvestPeriod: HarvestPeriod? = null,
        var sowingPeriod: SowingPeriod? = null
)

@IgnoreExtraProperties
data class Filter(var filtre: Categories? = null)

@IgnoreExtraProperties
data class Categories(
        var name: String = "Fruit",
        var description: String = "description",
        var img: String = "img"
)

@IgnoreExtraProperties
data class Legume(
        var name: String = "Légume",
        var img: String = "url",
        var description: String = "description de légume"
)

@IgnoreExtraProperties
data class Fruit(
        var name: String = "Fruit",
        var img: String = "url",
        var description: String = "description de fruit"
)

@IgnoreExtraProperties
data class Aromatique(
        var name: String = "Aromatique",
        var img: String = "url",
        var description: String = "description de aromatique"
)

@IgnoreExtraProperties
data class SowingPeriod(
        var endMonth: Int? = -1,
        var growingTime: Int? = -1,
        var startMonth: Int? = -1
)

@IgnoreExtraProperties
data class PlantingPeriod(var endMonth: Int? = -1, var startMonth: Int? = -1)

@IgnoreExtraProperties
data class HarvestPeriod(var endMonth: Int? = -1, var startMonth: Int? = -1)

@IgnoreExtraProperties
data class GardenerPlant(
        var plants: HashMap<String, PositionDataPlant> = HashMap()
)

@IgnoreExtraProperties
data class GardenerPlantTasks(
        var plants: HashMap<String, Tasks> = HashMap()
)

data class CheckMyList(var id: String, val value: PositionDataPlant?)
data class SortOfPlant(
        var id: String = "",
        var positionDataPlant: PositionDataPlant? = null
)

@IgnoreExtraProperties
data class PositionDataPlant(
        var dateHarvested: Int = -1,
        var dateSowing: Int = -1,
        var picture: Boolean = false,
        var plantID: String = "",
        var plantName: String = "",
        var tasks: ArrayList<Tasks> = arrayListOf(),
        var status : String? = null
)

@IgnoreExtraProperties
data class MonthRange(var endMonth: Int = 0, var startMonth: Int = 0)

@IgnoreExtraProperties
data class Characteristic(
        var exhibition: Int = 0,
        var height: String = "",
        var ph: Int = 0,
        var rusticite: Int = 0,
        var usda: Int = 0,
        var water: Int = 0
)

@IgnoreExtraProperties
data class TaskCalendar(var position: Int, var plantId: String, var plantName : String, var key: String, var tasks: Tasks, var status : String? = null)

@IgnoreExtraProperties
data class GardenerTasks(
        var date: Double = 0.1,
        var dayFromPlantation: Int = 0,
        var description: String = "",
        var done: Boolean = true,
        var title: String = ""
)

@IgnoreExtraProperties
data class Tasks(
        var date: Double = 0.1,
        var dayFromPlantation: Int = 0,
        var description: String = "",
        var done: Boolean = true,
        var doneBy: DoneBy? = null,
        var doneInTime: Boolean? = null,
        var taskId: String? = "",
        var title: String = "",
        var priority: Int = 0,
        var doInTime: String = "",
        var url : String = "",
        var steps :  ArrayList<Steps> = arrayListOf(),
        var tools: HashMap<String, Boolean> = HashMap()
)

@IgnoreExtraProperties
data class TasksUpdateReferencePlant(
        var description: String = "",
        var title: String = "",
        var priority: Int = 0,
        var doInTime: String = "",
        var url : String = "",
        var steps :  ArrayList<Steps> = arrayListOf(),
        var tools: HashMap<String, Boolean> = HashMap()
)

@IgnoreExtraProperties
data class Steps(
        var description : String = "",
        var image : String = "",
        var title : String? = null
)

@IgnoreExtraProperties
data class StepsImg(
        var image : String = ""
)

data class PushTask(
        var title: String,
        var body: String,
        var userId: String,
        var plantUID: String,
        var gardenerId: String,
        var stage: String,
        var taskName: String,
        var taskId: String
)

data class NotificationData(
        var userId: String,
        var title: String,
        var body: String,
        var type: String,
        var plantUID: String,
        var stage: String,
        var gardenerId: String,
        var taskName: String,
        var taskId: String
)

@IgnoreExtraProperties
data class PlantTasks(
        var index : Int,
        var plantId : String,
        var plantName : String,
        var tasks : Tasks
)

@IgnoreExtraProperties
data class NuisibleWithKey(
        var key : String = "",
        var nuisible : Nuisible = Nuisible()
)

@IgnoreExtraProperties
data class Nuisible(
        var name : String = "",
        var image : String = "",
        var description : String = "",
        var pieges : Pieges = Pieges(),
        var soinsnuisibles : Soins = Soins(),
        var auxiliaires : NuisibleByAuxiliaires = NuisibleByAuxiliaires()
)

@IgnoreExtraProperties
data class Pieges(
        var description : String ="",
        var title : String =""
)

@IgnoreExtraProperties
data class Auxiliaire(
        var image : String? = "",
        var name : String? = ""
)

@IgnoreExtraProperties
data class Soins(
        var description : String="",
        var title : String="",
        var url : String="",
        var traitements : ArrayList<Traitement>? = arrayListOf()
)

@IgnoreExtraProperties
data class Traitement(
        var description: String ="",
        var image : String ="",
        var name : String =""
)

@IgnoreExtraProperties
data class NuisibleByAuxiliaires(
        var description : String = "",
        var title : String ="",
        var listeauxiliaire : HashMap<String, Boolean> = HashMap()
)

data class NotificationContent(
        var title: String,
        var body: String
)

data class FilterTask(
        var planter: HashMap<String, Boolean> = HashMap(),
        var semer: HashMap<String, Boolean> = HashMap()
)

data class NotificationDto(
        var to: String,
        var data: NotificationData,
        var priority : String = "high"
)