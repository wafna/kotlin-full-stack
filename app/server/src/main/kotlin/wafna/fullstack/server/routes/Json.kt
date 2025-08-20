package wafna.fullstack.server.routes

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*
import kotlin.time.Instant
import wafna.fullstack.api.domain.DataBlockRecords
import wafna.fullstack.domain.DataBlock
import wafna.fullstack.domain.DataRecord
import wafna.fullstack.domain.User
import wafna.fullstack.server.domain.AuthResult
import wafna.fullstack.server.domain.HomePageData
import kotlin.time.ExperimentalTime

@Suppress("unused")
private object InstantAdapter {
    @OptIn(ExperimentalTime::class)
    @ToJson
    fun toJson(value: Instant): String = value.toEpochMilliseconds().toString()

    @OptIn(ExperimentalTime::class)
    @FromJson
    fun fromJson(value: String): Instant = Instant.fromEpochMilliseconds(value.toLong())
}

@Suppress("unused")
private object UuidAdapter {
    @ToJson
    fun toJson(value: UUID): String = value.toString()

    @FromJson
    fun fromJson(value: String): UUID = UUID.fromString(value)
}

private val moshi: Moshi =
    Moshi.Builder()
        .add(InstantAdapter)
        .add(UuidAdapter)
        .addLast(KotlinJsonAdapterFactory())
        .build()


/** Notational convenience for JSON serialization. */
interface JsonInvoke<T> {
    operator fun invoke(json: String): T

    operator fun invoke(obj: T?): String?
}

private val <T> JsonAdapter<T>.adapt: JsonInvoke<T>
    get() =
        serializeNulls().run {
            object : JsonInvoke<T> {
                override operator fun invoke(json: String): T = fromJson(json)!!

                override operator fun invoke(obj: T?): String = toJson(obj)!!
            }
        }

/**
 *  Json adapters.
 */
@Suppress("unused")
@OptIn(ExperimentalStdlibApi::class)
object Json {
    val user = moshi.adapter<User>().adapt
    val userList = moshi.adapter<List<User>>().adapt
    val dataBlock = moshi.adapter<DataBlock>().adapt
    val dataBlockList = moshi.adapter<List<DataBlock>>().adapt
    val dataRecord = moshi.adapter<DataRecord>().adapt
    val dataRecordList = moshi.adapter<List<DataRecord>>().adapt
    val dataBlockRecords = moshi.adapter<DataBlockRecords>().adapt

    val homePage = moshi.adapter<HomePageData>().adapt
    val authResult = moshi.adapter<AuthResult>().adapt
}

/* For top level returns */

fun DataBlock.toJson(): String = Json.dataBlock(this)!!

fun DataBlockRecords?.toJson(): String? = Json.dataBlockRecords(this)

fun HomePageData.toJson(): String = Json.homePage(this)!!

fun AuthResult.toJson(): String = Json.authResult(this)!!