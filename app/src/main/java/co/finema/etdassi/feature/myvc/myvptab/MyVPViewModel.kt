package co.finema.etdassi.feature.myvc.myvptab

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.data.VPDocumentRepository
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import co.finema.etdassi.feature.myvc.myvptab.MyVPAdapter.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyVPViewModel(
    private val vpDocumentRepository: VPDocumentRepository
): BaseViewModel() {

    private val _vpList = vpDocumentRepository.getVPDocumentList()
    val vpList: LiveData<List<VPDocumentAdapterModel>> = Transformations.map(_vpList) { vpDocList ->
        vpDocList.map {
            val type = object : TypeToken<List<String>>() {}.type
            VPDocumentAdapterModel(
                id = it.id,
                groupName = it.name,
                date = it.sendAt,
                vpCount = Gson().fromJson<List<String>>(it.vpIdList, type)?.size.toString()
            )
        }
    }

//    fun filterByDate(start: Long, end: Long): List<VPDocumentAdapterModel>? {
//        return _vpList.value?.filter {
//            val sdf = object : SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") {}
//            val time = sdf.parse(it.date!!)?.time
//            time in start..end
//        }
//    }

//    fun resetFilter() {
//        _vpList.value = _vpList.value
//    }

}