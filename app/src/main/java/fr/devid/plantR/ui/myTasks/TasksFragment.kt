package fr.devid.plantR.ui.myTasks

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentTasksBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.ui.home.ProfileViewModel
import java.time.*
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class TasksFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentTasksBinding
    var dayDateToLong: Long? = null
    private val selectedDates = mutableSetOf<LocalDate>()
    private val adapter by lazy { PagerManager(childFragmentManager) }
    val currentMonth = YearMonth.now()
    val firstMonth = currentMonth.minusMonths(12)
    val endMonth = currentMonth.plusMonths(12)
    private var tsLong = System.currentTimeMillis() / 1000
    private val today = LocalDate.now()
    var myDates: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("ONRESUME")
        setupTabLayout()
        val firstDayOfWeek = WeekFields.of(Locale.FRENCH).firstDayOfWeek
        binding.calendarView.setup(firstMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToDate(LocalDate.now())
        binding.calendarCurrentDate.text = getDate(tsLong)
    }


    private fun setupTabLayout() {
        adapter.resetAll()
        adapter.addFragmentPage(FragmentOneTasks("0"), getString(R.string.to_do))
        adapter.addFragmentPage(FragmentTwoTasks("1"), getString(R.string.to_done))
        binding.tlMyTasks.setupWithViewPager(binding.vpTasks)
        binding.vpTasks.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        println("ONSTOP")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalTime
    private fun initView() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.exOneDayText)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            dayDateToLong = null
                            selectedDates.remove(day.date)
                            profilViewModel.userService.isDateSelected = null
                        } else {
                            selectedDates.clear()
                            selectedDates.add(day.date)
                            println("DayDate : " + day.date)
                            println("Day : " + day)
                            var test = LocalDateTime.of(day.date, LocalTime.MAX);
                            dayDateToLong = test.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000
                            println("Day date : " + dayDateToLong)
                            profilViewModel.userService.isDateSelected = dayDateToLong
                            setupTabLayout()
                        }
                        binding.calendarView.notifyCalendarChanged()
                    }
                }
            }
        }
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {

            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textView.text = day.date.dayOfMonth.toString()

                var currentDate = day.date
                println("MON DAY DATE : " + day.date)
                var newDate = currentDate.plusDays(1L)

                println("Day date : " + newDate)
//                binding.calendarIvPrevious.setOnClickListener {
//
//                    newDate = newDate.minusDays(7L)
//                    binding.calendarView.smoothScrollToDate(newDate)
//                }
//
//                if(newDate >= today.plusWeeks(2)) {
//                    binding.calendarIvNext.visibility = View.GONE
//                } else {
//                    binding.calendarIvNext.visibility = View.VISIBLE
//                }
//                if(newDate <= today.minusWeeks(13)) {
//                    binding.calendarIvPrevious.visibility = View.GONE
//                }else {
//                    binding.calendarIvPrevious.visibility = View.VISIBLE
//                }
//                binding.calendarIvNext.setOnClickListener {
//                    newDate = newDate.plusDays(7L)
//                    binding.calendarView.smoothScrollToDate(newDate)
//                }

                if (day.owner == DayOwner.THIS_MONTH) {
                    when {
                        selectedDates.contains(day.date) -> {
                            if (day.date == today) {
                                container.textView.setTextColor(
                                    requireView().resources.getColor(R.color.green_plantr, null)
                                )
                                container.textView.background = requireView().resources.getDrawable(
                                    R.drawable.round_selected_bg_white,
                                    null
                                )
                            } else {
                                myDates = day.date

                                container.textView.setTextColor(
                                    requireView().resources.getColor(
                                        R.color.white_plantr,
                                        null
                                    )
                                )
                                container.textView.background = requireView().resources.getDrawable(
                                    R.drawable.round_selected_bg_green_extra,
                                    null
                                )
                            }

                        }
                        today == day.date -> {
                            print("TODAY")
                            container.textView.setTextColor(
                                requireView().resources.getColor(
                                    R.color.green_plantr,
                                    null
                                )
                            )
                            container.textView.background = requireView().resources.getDrawable(
                                R.drawable.round_selected_bg_white,
                                null
                            )
                        }
                        else -> {
                            container.textView.setTextColor(Color.WHITE)
                            container.textView.background = null
                        }
                    }
                } else {
                    container.textView.setTextColor(
                        resources.getColor(
                            R.color.light_grey_plantr,
                            null
                        )
                    )
                }

            }
        }

        binding.calendarView.updateMonthConfiguration(
            inDateStyle = InDateStyle.ALL_MONTHS,
            maxRowCount = 1,
            hasBoundaries = false
        )

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
            profilViewModel.userService.isDateSelected = null
        }

        binding.ivBtnCalendar.setOnClickListener {
            val popupAddGardener = PopupCalendar(requireContext()) { popup, bool, str, isDateSelected ->
                    if (bool) {
                        popup.dismiss()
                        selectedDates.clear()
                        profilViewModel.userService.isDateSelected = isDateSelected
                        setupTabLayout()
                        val epoch = isDateSelected!!.toLong()
                        println("mon mili " + epoch)
                        val ld = Instant.ofEpochMilli(epoch * 1000)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        println("MON DATE NEW :" + ld)
                        binding.calendarView.smoothScrollToDate(ld)
                        binding.calendarView.notifyCalendarChanged()
                        selectedDates.add(ld)
                        when {
                            selectedDates.contains(ld) -> {
                                println("LA DATE EST ENREGISTRER")
                            }
                        }
                    } else if (!bool && isDateSelected?.toInt() == -1) {
                        AlerterService.showError(
                            context?.getString(R.string.CONFIRM_AFTER_DATE)!!,
                            requireActivity()
                        )
                    } else {
                        popup.dismiss()
                    }
                }
            popupAddGardener.show()
        }
    }


    fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.FRENCH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("EEEE d MMMM yyyy", calendar).toString()
        val words = date.split(" ")
        var newStr = ""
        words.forEach {
            newStr += it.capitalize() + " "
        }
        return newStr.trimEnd()
    }

}

