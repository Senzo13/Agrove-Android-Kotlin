package fr.devid.plantR.utils

import android.content.Context
import com.google.gson.Gson
import fr.devid.plantR.models.ListPaysModel
import fr.devid.plantR.models.Pays

class PaysUtils(var context: Context) {
    var listPaysModel : ListPaysModel =
        Gson().fromJson(JsonUtils().read_json(context, "pays.json"), ListPaysModel::class.java)
    val hashMapPaysIndexed = HashMap<Int, Pays>()

    init {
        listPaysModel.data.mapIndexed { index, pays -> hashMapPaysIndexed.put(index, pays) }
    }

    fun getPositionByCode(code : String) : Int {
        return hashMapPaysIndexed.filter { it.value.code == code }.map { it.key }.first()
    }

    fun getAllPaysName() : ArrayList<String> {
        return listPaysModel.data.map { it.name } as ArrayList<String>
    }

    fun getCodeByName(name : String) : String {
        return listPaysModel.data.first { it.name == name }.code
    }

    fun getNameByCode(code : String) : String {
        return listPaysModel.data.first { it.code == code }.name
    }

}