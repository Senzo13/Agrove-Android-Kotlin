package fr.devid.plantR.ui.myPlants
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import fr.devid.plantR.R
import java.util.*

class DatePickerHelper(context: Context, isSpinnerType: Boolean = false) {
    var dialog: DatePickerDialog
    private var callback: Callback? = null

    private val listener = OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
        callback?.onDateSelected(dayOfMonth, monthOfYear, year)
    }

    init {
        val style = if (isSpinnerType) R.style.DialogTheme else R.style.AlreadyPlantCalendar
        val cal = Calendar.getInstance(Locale.FRANCE)
        dialog = DatePickerDialog(context, style, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    fun showDialog(dayofMonth: Int, month: Int, year: Int, callback: Callback?) {
        this.callback = callback
        dialog.datePicker.init(year, month, dayofMonth, null)
        dialog.show()
        //dialog.findViewById<Button>(DatePickerDialog.BUTTON_NEGATIVE).resources.getColor(R.color.green_plantr, null)
    }

    fun setMinDate(minDate: Long) {
        dialog.datePicker.minDate = minDate
    }
    fun setMaxDate(maxDate: Long) {
        dialog.datePicker.maxDate = maxDate
    }
    interface Callback {
        fun onDateSelected(dayofMonth: Int, month: Int, year: Int)
    }
}