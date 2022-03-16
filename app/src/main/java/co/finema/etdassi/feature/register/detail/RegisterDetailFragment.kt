package co.finema.etdassi.feature.register.detail

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.core.util.Pair
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentRegisterDetailBinding
import co.finema.etdassi.feature.myvc.myvptab.MyVPListFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.sso.common.base.BaseFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel

import java.util.*


class RegisterDetailFragment:BaseFragment<RegisterDetailViewModel, FragmentRegisterDetailBinding>(RegisterDetailViewModel::class.java) {

    override val mViewModel: RegisterDetailViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_register_detail

    private var someFieldError = false

    private var firstName = false
    private var lastName = false
    private var idCard = false
    private var backCard = false
    private var email = false
    private var birthDate = false

    override fun init() {
        super.init()
        checkDisableButton()

        mBinding.buttonRegisterDetail.disable = false
        checkDisableButton()

        mBinding.toolbarRegisterDetail.apply {
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            toolbarTitle = getString(R.string.register_title)
        }

        mBinding.toolbarDescription = getString(R.string.please_input_your_information)

        mBinding.apply {
            fieldFirstname.apply {
                hintText = getString(R.string.hint_first_name)
                titleText = getString(R.string.title_first_name)
                editTextFiled.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        firstName = !s?.toString().isValid()
                        alertWrong = firstName
                        mViewModel.firstName.value = s?.toString()?.trim()
                        checkDisableButton()
                    }

                })
            }

            fieldLastname.apply {
                hintText = getString(R.string.hint_last_name)
                titleText = getString(R.string.title_last_name)
                editTextFiled.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        lastName = !s?.toString().isValid()
                        alertWrong = lastName
                        mViewModel.lastName.value = s?.toString()?.trim()
                        checkDisableButton()
                    }

                })
            }

            fieldBirthDate.apply {
                hintText = getString(R.string.hint_birth_date)
                titleText = getString(R.string.title_birth_date)
                endImage.visibility = View.VISIBLE
                editTextFiled.isFocusable = false
                editTextFiled.isClickable = false

                editTextFiled.setOnClickListener {

                    val builder = MaterialDatePicker.Builder.datePicker()
                    builder.setCalendarConstraints(limitRange().build())
                    builder.setTheme(R.style.ThemeOverlay_App_DatePicker)
                    val picker = builder.build()
                    picker.show(activity?.supportFragmentManager!!, picker.toString())
                    picker.addOnNegativeButtonClickListener {
                    }
                    picker.addOnPositiveButtonClickListener {
                        val mCalendar = Calendar.getInstance()
                        mCalendar.timeInMillis = it
                        mCalendar.updateLabel()
                    }
                }
            }

            fieldCardId.apply {
                hintText = getString(R.string.hint_card_id)
                titleText = getString(R.string.title_card_id)
                editTextFiled.inputType = InputType.TYPE_CLASS_PHONE
                editTextFiled.filters += InputFilter.LengthFilter(17)
                editTextFiled.addTextChangedListener(object :TextWatcher {
                    var isEdiging: Boolean = false
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isEdiging) return
                        isEdiging = true
                        val sb = StringBuilder()
                        sb.append(s?.toString()?.replace("-", ""))
                        mViewModel.cardIdNumber.value = sb.toString().trim()
                        println("GGGG =>${sb.toString().trim().length}")
                        mBinding.fieldCardId.alertWrong = sb.toString().trim().length < 13
                        if (sb.length>1) {
                            sb.insert(1, "-")
                        }
                        if (sb.length>6) {
                            sb.insert(6,"-")
                        }
                        if (sb.length>12) {
                            sb.insert(12, "-")
                        }
                        if (sb.length>15) {
                            sb.insert(15,"-")
                        }
                        s?.replace(0, s.length, sb.toString())
                        checkDisableButton()
                        isEdiging = false
                    }

                })
            }

            fieldBackCard.apply {
                hintText = getString(R.string.hint_back_card_number)
                titleText = getString(R.string.title_back_card_number)
                editTextFiled.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                editTextFiled.filters += InputFilter.LengthFilter(14)
                editTextFiled.addTextChangedListener(object :TextWatcher {
                    var isEdiging: Boolean = false
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        backCard = !s?.toString()?.trim().isEnglish()
                        alertWrong = backCard
                        if (!backCard) {
                            if (isEdiging) return
                            isEdiging = true
                            val sb = StringBuilder()
                            sb.append(s?.toString()?.replace("-", "")?.uppercase())
                            mViewModel.backCardId.value = sb.toString().trim()
                            if (sb.length>3) {
                                sb.insert(3, "-")
                            }
                            if (sb.length>11) {
                                sb.insert(11,"-")
                            }
                            s?.replace(0, s.length, sb.toString())
                            isEdiging = false
                        }
                        checkDisableButton()
                    }

                })
            }

            fieldContact.apply {
                hintText = getString(R.string.hint_contect_address)
                titleText = getString(R.string.title_contect_address)
                editTextFiled.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                editTextFiled.addTextChangedListener(object :TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        mViewModel.email.value = s?.toString()?.trim()
                        checkDisableButton()
                    }

                })
            }
        }



        mBinding.buttonRegisterDetail.apply {
            buttonText = getString(R.string.check_data)
        }
    }

    private fun Calendar.updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        mBinding.fieldBirthDate.editTextFiled.setText(sdf.format(this.time))
        mViewModel.birthDate.value = this.timeInMillis.toString()
        birthDate = this.timeInMillis > Calendar.getInstance().timeInMillis
        mBinding.fieldBirthDate.alertWrong = birthDate
        checkDisableButton()
    }

    private fun String?.isValid(): Boolean {
        this?.let {
            val isValid = it.matches(Regex(getString(R.string.thai_alphabet)))
            someFieldError = !isValid
            return isValid
        } ?: kotlin.run {
            return false
        }
    }

    private fun String?.isEnglish(): Boolean {
        this?.let {
            val isValid = it.matches(Regex("^[a-zA-Z0-9-]*"))
            someFieldError = !isValid
            return isValid
        } ?: kotlin.run {
            return false
        }
    }

    private fun checkDisableButton() {
        mViewModel.apply {

//            mBinding.buttonRegisterDetail.disable = someFieldError
//            mBinding.buttonRegisterDetail.setOnButtonClickListener {
//                val fragment = LoadingFragment()
//                val args = Bundle()
//                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_DETAIL)
//                args.putParcelable(LoadingFragment.PAYLOAD, mViewModel.buildPayload())
//                fragment.arguments = args
//                parentFragmentManager.animateTransition()
//                    .add(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
//                    .hide(this@RegisterDetailFragment)
//                    .addToBackStack(BackStack.REGISTER_APP.name)
//                    .commit()
//            }

            if (
                firstName.value.isNullOrBlank() ||
                lastName.value.isNullOrBlank() ||
                birthDate.value.isNullOrBlank() ||
                cardIdNumber.value.isNullOrBlank() ||
                backCardId.value.isNullOrBlank() ||
                email.value.isNullOrBlank() ||
                        this@RegisterDetailFragment.firstName ||
                        this@RegisterDetailFragment.lastName ||
                        this@RegisterDetailFragment.idCard ||
                        this@RegisterDetailFragment.backCard ||
                        this@RegisterDetailFragment.email ||
                        this@RegisterDetailFragment.birthDate
            ) {
                mBinding.buttonRegisterDetail.disable = true
            } else {
                mBinding.buttonRegisterDetail.disable = someFieldError
                mBinding.buttonRegisterDetail.setOnButtonClickListener {
                    val fragment = LoadingFragment()
                    val args = Bundle()
                    args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_DETAIL)
                    args.putParcelable(LoadingFragment.PAYLOAD, mViewModel.buildPayload())
                    fragment.arguments = args
                    parentFragmentManager.animateTransition()
                        .add(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                        .hide(this@RegisterDetailFragment)
                        .addToBackStack(BackStack.REGISTER_APP.name)
                        .commit()
                }
            }


        }
    }

    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = Calendar.getInstance()
        val calendarEnd: Calendar = Calendar.getInstance()

        val year = 1919
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

        constraintsBuilderRange.setValidator(MyVPListFragment.RangeValidator(minDate, maxDate))

        return constraintsBuilderRange
    }

}


