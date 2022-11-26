package fr.devid.plantR.ui.myTasks

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.storage.StorageReference
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_full_calendar.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*


class PopupCalendar(context: Context, private val callback: ((Dialog, Boolean, String, Long?) -> Unit)): Dialog(context) {

    private lateinit var storageRef: StorageReference
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private var selected: Boolean = false
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var binding = this
    private var isDateSelected: LocalDate? = null

    var dayDateToLong : Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_full_calendar)

        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()

        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }

    private fun setupView() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.exOneDayTextPopup)
            lateinit var day: CalendarDay
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if(selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                            binding.button_validate.setTextColor(context.resources.getColor(R.color.grey_plantr, null))
                            dayDateToLong = null
                        } else {
                            selectedDates.clear()
                            selectedDates.add(day.date)
                            binding.button_validate.setTextColor(context.resources.getColor(R.color.greenFonce_plantr, null))
                            println("DayDate : " + day.date)
                            println("Day : " + day)
                            var test = LocalDateTime.of(day.date, LocalTime.MIDNIGHT);
                            dayDateToLong = test.toInstant(ZoneOffset.MIN).toEpochMilli() / 1000
                            println("Day date : " + dayDateToLong)
                        }
                        binding.exOneCalendar.notifyCalendarChanged()
                    }
                }
            }
        }

        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        binding.exOneCalendar.setup(startMonth, endMonth, firstDayOfWeek)
        binding.exOneCalendar.scrollToMonth(currentMonth)
        binding.exOneCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)
            // Called every time we need to reuse a container.
            @SuppressLint("ResourceAsColor")
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                var dateOfTheMonth = day.date.dayOfMonth.toString().capitalize()
                container.textView.text = dateOfTheMonth

                var currentDate = day.date
                var newDate = currentDate

                if (day.owner == DayOwner.THIS_MONTH) {
                    when {
                        selectedDates.contains(day.date) -> {
                                container.textView.setTextColor(context.resources.getColor(R.color.white_plantr, null))
                                container.textView.background = context.resources.getDrawable(R.drawable.round_selected_bg_green, null)
                        }
                        today == day.date -> {
                            container.textView.setTextColor(context.resources.getColor(R.color.green_plantr, null))
                            container.textView.background = context.resources.getDrawable(R.drawable.round_selected_bg_white, null)
                        }
                        else -> {
                            container.textView.setTextColor(context.resources.getColor(R.color.light_black_plantr, null))
                            container.textView.background = null
                    }
                    }
                } else {
                    container.textView.setTextColor(context.resources.getColor(R.color.dark_grey_plantr, null))
                    container.textView.background = null
                }
            }
        }

        binding.exOneCalendar.monthScrollListener = {
            if (binding.exOneCalendar.maxRowCount == 6) {
                //binding.exOneYearText.text = it.yearMonth.year.toString()
                binding.exOneMonthText.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                // In week mode, we show the header a bit differently.
                // We show indices with dates from different months since
                // dates overflow and cells in one index can belong to different
                // months/years.
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                  //  binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    binding.exOneMonthText.text = monthTitleFormatter.format(firstDate)
                } else {
                    binding.exOneMonthText.text = "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                     //   binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    } else {
                  //      binding.exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }
        }

        binding.button_validate.setOnClickListener {
            if(dayDateToLong != null) {
                callback(this, true, "", dayDateToLong)

                println("JE VALIDE MON ISDATDATE : " + dayDateToLong)
            } else {
                //callback(this, false, "", -1)
            }
        }

        binding.button_exit.setOnClickListener {
            callback(this, false, "", null)
        }

        this.setOnDismissListener {
            callback(this, false, "", null)
        }

//        binding.button_close.setOnClickListener {
//            callback(this, false, "")
//        }

    }
}