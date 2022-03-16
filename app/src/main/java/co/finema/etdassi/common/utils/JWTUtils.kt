package co.finema.etdassi.common.utils

import co.finema.etdassi.common.data.JWTModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.data.VCAttributeType
import co.finema.etdassi.common.data.VCSchemaModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

object JWTUtils {
    fun decodedJWT(jwt: String): JWTModel? {
        val parts = jwt.split(".")
        return try {
            val charset = charset("UTF-8")
            val header = String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset)
            val payload = String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
//            "$header\n$payload"
            JWTModel(header = header, payload = payload)
        } catch (e: Exception) {
            null
        }
    }

    fun jwtConvertToSchemaModel(jwtModel: JWTModel?): VCSchemaModel? {
        return Gson().fromJson(jwtModel?.payload, VCSchemaModel::class.java)
    }

    fun credentialSubjectToAttributeModel(credentialSubject: JsonObject): List<VCAttributeModel> {
        val mapper = ObjectMapper()
        val map: Map<String, Any>? = mapper.readValue(credentialSubject.toString(), object : TypeReference<Map<String, Any>?>() {})
        return convertMapEntry(map, 0)
    }

    private fun convertMapEntry(map: Map<*, *>?, level: Int): List<VCAttributeModel> {
        val result = ArrayList<VCAttributeModel>()
        map?.forEach {
            val key = it.key as? String
            when (it.value) {
                /**
                 * เคส Key Value เป็น เช่น ชื่อโรงพยาบาล: ธนบุรี
                 */
                is String, is Int, is Double-> {
                    result.add(
                        VCAttributeModel(
                            title = key,
                            value = it.value.toString(),
                            type = VCAttributeType.VALUE,
                            level = level
                        )
                    )
                }
                /**
                 * เคส Value เป็น Object
                 */
                is Map<*,*> -> {
                    val items = convertMapEntry(it.value as Map<*, *>, level + 1)
                    val type =  items.firstOrNull { i -> i.type == VCAttributeType.ARRAY }?.let {
                        VCAttributeType.OBJECT_WITH_ARRAY
                    } ?: kotlin.run {
                        VCAttributeType.OBJECT
                    }
                    result.add(
                        VCAttributeModel(
                            title = key,
                            items = items,
                            type = type,
                            level = level
                        )
                    )
                }
                /**
                 * เคส Value List <Object, String, Any>
                 */
                is ArrayList<*> -> {
                    val arrayList = ArrayList<VCAttributeModel>()
                    (it.value as ArrayList<*>).forEach { item ->
                        if (item is Map<*, *>) {
                            val items = convertMapEntry(item, level + 1)
                            val type =  items.firstOrNull { i -> i.type == VCAttributeType.ARRAY }?.let {
                                VCAttributeType.OBJECT_WITH_ARRAY
                            } ?: kotlin.run {
                                VCAttributeType.OBJECT
                            }
                            arrayList.add(
                                VCAttributeModel(
                                    items = items,
                                    type = type,
                                    level = level
                                )
                            )
                        } else if (item is String){
                            arrayList.add(
                                VCAttributeModel(
                                    value = item.toString(),
                                    type = VCAttributeType.VALUE,
                                    level = level
                                )
                            )
                        }
                    }
                    result.add(
                        VCAttributeModel(
                            title = key,
                            items = arrayList,
                            type = VCAttributeType.ARRAY,
                            level = level
                        )
                    )
                }
            }
        }
        return result
    }
}