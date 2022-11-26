package fr.devid.plantR.ui.stateGardener

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.view.ViewGroup
import android.view.View
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.devid.plantR.R
import timber.log.Timber
import java.util.ArrayList

class ResultAdapter constructor(private val context: Context) :
    BaseAdapter() {
    private val characteristicList: MutableList<BluetoothGattCharacteristic>
    private val characteristicListService: MutableList<BluetoothGattService>
        get() {
            TODO()
        }

    fun addResult(characteristic: BluetoothGattCharacteristic) {
        characteristicList.add(characteristic)
        Timber.d("La charact frere : $characteristic")
    }

    fun clear() {
        characteristicList.clear()
    }

    override fun getCount(): Int {
        return characteristicList.size
    }

    override fun getItem(position: Int): BluetoothGattCharacteristic {
        if (position > characteristicList.size) {
            println("Probleme ici dans la taille du bail")
        }
        return characteristicList[position]
    }
    fun getItemService(position: Int): BluetoothGattService {
        if (position > characteristicListService.size) {
            println("Probleme ici dans la taille du bail")
        }
        return characteristicListService[position]
    }
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView != null) {

            println("Dans result adapter diff null")

            holder = convertView.tag as ViewHolder
        } else {
            println("Dans result adapter")

            convertView = View.inflate(context, R.layout.adapter_service, null)
            holder = ViewHolder()
            convertView.tag = holder
            holder.txt_title = convertView.findViewById<View>(R.id.txt_title) as TextView
            holder.txt_uuid = convertView.findViewById<View>(R.id.txt_uuid) as TextView
            holder.txt_type = convertView.findViewById<View>(R.id.txt_type) as TextView
            holder.img_next = convertView.findViewById<View>(R.id.img_next) as ImageView
        }
        val characteristic = characteristicList[position]
        val uuid = characteristic.uuid.toString()

        holder.txt_title?.text =  context?.getString(R.string.characteristic).toString() + "（" + position + ")"
        holder.txt_uuid!!.text = uuid
        println("HOLDER ICI " + uuid)

        val property = StringBuilder()
        val charaProp = characteristic.properties
        if (charaProp and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            property.append("Read")
            property.append(" , ")
        }
        if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
            property.append("Write")
            property.append(" , ")
        }
        if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
            property.append("Write No Response")
            property.append(" , ")
        }
        if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            property.append("Notify")
            property.append(" , ")
        }
        if (charaProp and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
            property.append("Indicate")
            property.append(" , ")
        }
        if (property.length > 1) {
            property.delete(property.length - 2, property.length - 1)
        }
        if (property.length > 0) {
            holder.txt_type?.text = context.getString(R.string.characteristic)
                .toString() + "( " + property.toString() + ")"
            holder.img_next!!.visibility = View.VISIBLE
        } else {
            holder.img_next!!.visibility = View.INVISIBLE
        }
        return convertView
    }

    internal inner class ViewHolder {
        var txt_title: TextView? = null
        var txt_uuid: TextView? = null
        var txt_type: TextView? = null
        var img_next: ImageView? = null
    }

    init {
        characteristicList = ArrayList()
    }
}