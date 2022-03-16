package co.finema.etdassi.feature.myvc.myvptab

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.util.Pair
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.databinding.FragmentMyVcListBinding
import co.finema.etdassi.feature.myvc.myvptab.detail.MyVPDetailActivity
import co.finema.sso.common.base.BaseFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MyVPListFragment:BaseFragment<MyVPViewModel, FragmentMyVcListBinding>(MyVPViewModel::class.java) {
    override val mViewModel: MyVPViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc_list

    private var filterSelect: Pair<Long, Long>? = null

    override fun init() {
        super.init()

        val titleAdapter = TitleAdapter()
        val mAdapter = MyVPAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mAdapter.registerOnClickLister(object :MyVPAdapter.MyVPAdapterLister {
            override fun onItemClick(data: MyVPAdapter.VPDocumentAdapterModel) {
                val intent = Intent(requireContext(), MyVPDetailActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }

        })

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        mViewModel.vpList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.sortedByDescending { it.date })
            titleAdapter.submitList(listOf(TitleAdapterDataModel(it.size)))
        })


//        mBinding.filter.visibility = View.VISIBLE
//        mBinding.filter.setOnClickListener {
//            val builder = MaterialDatePicker.Builder.dateRangePicker()
////            val now = CalendarConstraints.Builder()
//            builder.setCalendarConstraints(limitRange().build())
//            builder.setTheme(R.style.ThemeOverlay_App_DatePicker)
//            builder.setTitleText("เลือกช่วงเวลาที่ลงนามเอกสาร")
//            filterSelect?.let { builder.setSelection(it) }
//            val picker = builder.build()
//            picker.show(activity?.supportFragmentManager!!, picker.toString())
//            picker.addOnNegativeButtonClickListener {
//                println("CLEAR")
//                filterSelect = null
//                mViewModel.resetFilter()
//            }
//            picker.addOnPositiveButtonClickListener {
//                println("The selected date range is ${it.first} - ${it.second}")
//                filterSelect = it
//                if (it.first != null && it.second != null) {
//                    val listOfFilter= mViewModel.filterByDate(it.first!!, it.second!!)
//                    mAdapter.submitList(listOfFilter)
//                    titleAdapter.submitList(listOf(TitleAdapterDataModel(listOfFilter?.size ?: 0)))
//                }
//            }
//
//        }


    }

    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = Calendar.getInstance()
        val calendarEnd: Calendar = Calendar.getInstance()

        val year = 2019
        val startMonth = 1
        val startDate = 1

        val endMonth = calendarEnd.get(Calendar.MONTH)
        val endDate = calendarEnd.get(Calendar.DATE)

        calendarStart.set(year, startMonth, startDate)
        calendarEnd.set(calendarEnd.get(Calendar.YEAR), endMonth, endDate)

        val minDate = calendarStart.timeInMillis
        val maxDate = calendarEnd.timeInMillis

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))

        return constraintsBuilderRange
    }

    class RangeValidator(private val minDate:Long, private val maxDate:Long) : CalendarConstraints.DateValidator{


        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong()
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented")
        }

        override fun describeContents(): Int {
            TODO("not implemented")
        }

        override fun isValid(date: Long): Boolean {
            return !(minDate > date || maxDate < date)

        }

        companion object CREATOR : Parcelable.Creator<RangeValidator> {
            override fun createFromParcel(parcel: Parcel): RangeValidator {
                return RangeValidator(parcel)
            }

            override fun newArray(size: Int): Array<RangeValidator?> {
                return arrayOfNulls(size)
            }
        }

    }
}