package co.finema.etdassi.feature.myvc.myvctab

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.databinding.DialogFilterBinding
import co.finema.etdassi.databinding.FragmentMyVcListBinding
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyVCListFragment: BaseFragment<MyVCListViewModel, FragmentMyVcListBinding>(MyVCListViewModel::class.java) {

    val TAG = this.javaClass.simpleName

    override val mViewModel: MyVCListViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc_list

    override fun init() {
        super.init()
        mBinding.editTextFiled.setText("VC List")

        mBinding.filter.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                val dialog = Dialog(requireContext())
                val dialogBinding = DataBindingUtil.inflate<DialogFilterBinding>(
                    LayoutInflater.from(requireContext()), R.layout.dialog_filter, null, false
                )
                dialog.setContentView(dialogBinding.root)
                dialog.window?.apply {
                    setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    setGravity(Gravity.CENTER)
                    val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
                    setBackgroundDrawable(InsetDrawable(ColorDrawable(resources.getColor(android.R.color.transparent, null)), margin, 0, margin, 0))
                }

                dialogBinding.apply {
                    checkboxAll.isChecked = mViewModel.filterAll
                    checkboxActive.isChecked = mViewModel.filterActive
                    checkboxDeactive.isChecked = mViewModel.filterDeActive
                }


                dialogBinding.filterButtonSubmit.apply {
                    buttonText = "กรอง"
                    setOnButtonClickListener {
                        mViewModel.apply {
                            filterAll = dialogBinding.checkboxAll.isChecked
                            filterActive = dialogBinding.checkboxActive.isChecked
                            filterDeActive = dialogBinding.checkboxDeactive.isChecked
                            doFilter()
                        }
                        Log.d(TAG, "filterAll = ${mViewModel.filterAll} , filterActive = ${mViewModel.filterActive} , " +
                                "filterDeActive = ${mViewModel.filterDeActive}")
                        var filterCount = 0
                        if (mViewModel.filterAll) filterCount += 1
                        if (mViewModel.filterActive) filterCount += 1
                        if (mViewModel.filterDeActive) filterCount += 1
                        mBinding.filterCount = if (filterCount!=0) filterCount.toString() else null
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }
        }

        val titleAdapter = TitleAdapter()
        val mAdapter = MyVCListAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
            titleAdapter.submitList(listOf(TitleAdapterDataModel((it.size))))
        })


    }


}