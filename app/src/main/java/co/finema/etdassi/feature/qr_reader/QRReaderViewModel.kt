package co.finema.etdassi.feature.qr_reader

import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase

class QRReaderViewModel(
    private val qrReaderUseCase: QRReaderUseCase
): BaseViewModel() {


    fun getGetVPDoc(
        textResultModel: QRTextResultModel,
        param: GenericListener<QRReaderUseCase.RequestingVPDocResponse>
    ) {
        qrReaderUseCase.launch(viewModelScope, QRReaderUseCase.Param(
            url = textResultModel.endpoint
        )) { either ->
            either.either({
                param.onFail(it.handleError())
                it.printStackTrace()
            }, {
                param.onSuccess(it)
            })
        }
    }
}