package co.finema.etdassi.common.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import co.finema.etdassi.feature.scan_qr.usecase.CreateVPUseCase

data class VPDocument(
    val id: String,
    val name: String?,
    val verifierDid: String?,
    val createdAt: String?,
    val sendAt: String?,
    val vpIdList: List<String>?,
    val jwt: String,
    val source: CreateVPUseCase.Response
)