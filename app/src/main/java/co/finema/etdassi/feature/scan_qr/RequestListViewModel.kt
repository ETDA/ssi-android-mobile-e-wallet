package co.finema.etdassi.feature.scan_qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.myvc.myvctab.detail.VCCardAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailViewModel
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import co.finema.etdassi.feature.scan_qr.usecase.CreateVPUseCase

class RequestListViewModel(
    private val vcDocumentRepository: VCDocumentRepository,
    private val createVPUseCase: CreateVPUseCase
    ): BaseViewModel() {

    private val _scanQrVcList = MutableLiveData<List<ScanQRListVC>>()
    val scanQrVcList: LiveData<List<ScanQRListVC>> get() = _scanQrVcList

    private val _reqVcData = MutableLiveData<ScanQRListVC>()
    val reqVcData: LiveData<ScanQRListVC> get() = _reqVcData

    private val _stepSelectVcList = MutableLiveData<List<ScanStepSelectVCList>>()
    val stepSelectVcList: LiveData<List<ScanStepSelectVCList>> get() = _stepSelectVcList

    val stepSelectButtonEnable = MutableLiveData<Boolean>()

    val onVCSelectedPosition = MutableLiveData<Int>()

    val requestMainPageButton = MutableLiveData<Boolean>()

    fun setVCRequestList(requestList: List<ScanQRListVC>) {
        if (_scanQrVcList.value != null) {
            requestMainPageButton.value = _scanQrVcList.value?.filter { it.vcIdPick !=null }?.size == _scanQrVcList.value?.size
            return
        }
        _scanQrVcList.value = requestList

    }

    fun setDataOnClick(reqVcData: ScanQRListVC) {
        _reqVcData.value = reqVcData
    }

    fun getStepSelectVcList(vcType: String) {
        val buildData = vcDocumentRepository.getVCDocByType(vcType)
        buildData.firstOrNull { it.cid == _reqVcData.value?.vcIdPick }?.isSelect = true
        _stepSelectVcList.value = buildData.filter { it.status == true }.sortedByDescending { it.dateString }

    }

    fun onRadioBoxClick(vcData: ScanStepSelectVCList) {
        _stepSelectVcList.value!!.firstOrNull { it.isSelect }?.let {
            it.isSelect = !it.isSelect
        }
        _stepSelectVcList.value!!.firstOrNull {it.cid == vcData.cid}?.isSelect = true

        stepSelectButtonEnable.value = _stepSelectVcList.value!!.firstOrNull { it.isSelect } != null


    }

    fun oldPosition(): Int? {
        return _stepSelectVcList.value?.indexOfFirst { it.isSelect }
    }

    fun setDataSelect() {
        _scanQrVcList.value!!.firstOrNull { it.id == _reqVcData.value?.id }?.vcIdPick = _stepSelectVcList.value?.first { it.isSelect }?.cid
    }

    fun checkResult() {
        if (_reqVcData.value?.vcIdPick != null) {
            onVCSelectedPosition.value = _scanQrVcList.value?.indexOfFirst { it.id == _reqVcData.value?.id }
        }
        requestMainPageButton.value = _scanQrVcList.value?.filter { it.vcIdPick !=null && it.isRequest }?.size == _scanQrVcList.value?.filter { it.isRequest }?.size

    }

    /**
     * Detail page vc
     */

    val vcDetailData = MutableLiveData<ScanStepSelectVCList?>()
    var vcDetailPosition = -1
    var isDetailSelect = false

    //TODO MOCK DATA
    private val _vcList = MutableLiveData<List<VCDetailViewModel.VCDetailAdapterModel>>()
    val vcList: LiveData<List<VCDetailViewModel.VCDetailAdapterModel>> get() = _vcList

    private val _vcDocumentCardStatus = MutableLiveData<List<VCCardAdapter.VCCardModel>>()
    private val _vcDocumentDescription = MutableLiveData<List<VCAttributeModel>>()
    val vcDocumentCardStatus: LiveData<List<VCCardAdapter.VCCardModel>> get() = _vcDocumentCardStatus
    val vcDocumentDescription: LiveData<List<VCAttributeModel>> get() = _vcDocumentDescription

    fun getVcDocumentByCid(cid: String) {
        val vcDoc = vcDocumentRepository.getVCDocument(cid)
        vcDoc.let {
            _vcDocumentCardStatus.value = listOf(VCCardAdapter.VCCardModel(
                vcType = it?.type?:"",
                vcStatus = it?.status ?:""
            ))
            it?.credentialSubject?.let { itemList -> _vcDocumentDescription.value = itemList}
        }
    }


    /**
     * Send VP
     */

    fun sendVP(requestingVPDocResponse: QRReaderUseCase.RequestingVPDocResponse, param: GenericListener<Boolean>) {
        if (scanQrVcList.value == null) return
        val cidList = scanQrVcList.value?.let { list ->
            val cidList: List<String> = list.filter { it.vcIdPick != null }.map {
                it.vcIdPick!!
            }
            cidList
        }
        if (cidList != null && requestingVPDocResponse.endpoint != null && requestingVPDocResponse.verifierDid != null &&
                requestingVPDocResponse.requestId != null) {
            createVPUseCase.launch(viewModelScope, CreateVPUseCase.Param(
                vpRequestId = requestingVPDocResponse.requestId,
                cidList = cidList,
                endpoint = requestingVPDocResponse.endpoint,
                verifierDid = requestingVPDocResponse.verifierDid,
                groupName = requestingVPDocResponse.name,
                createAt = requestingVPDocResponse.createAt
            )) { either ->
                either.either({
                    it.printStackTrace()
                    param.onFail(it.handleError())
                }, {
                    param.onSuccess(it)
                })
            }
        } else {
            param.onFail("ขอมูลไม่ครบถ้วน")
        }
    }



}

data class ScanQRListVC(
        val id: String?,
        val vcTypeName: String?,
        var vcIdPick: String? = null,
        val isRequest: Boolean = false
                       )

data class ScanStepSelectVCList(
    val cid: String? = null,
    var isSelect: Boolean = false,
    val vcTypeName: String? = null,
    val dateString: String? = null,
    val count: Int? = null,
    val status: Boolean? = false
                               )