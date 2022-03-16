package co.finema.etdassi.common.data

import com.fasterxml.jackson.core.util.RequestPayload

data class JWTModel(
    val header: String?,
    val payload: String?
)
