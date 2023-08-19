package com.rummy.sulung.network.response


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "DIARYDETAIL")
data class ReadDiaryDetailResponse(
    @PrimaryKey
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "drink")
    val drink: Drink?,
    @field:Json(name = "emotion")
    val emotion: Int?,
    @field:Json(name = "diaryDt")
    val diaryDt: Long?,
    @field:Json(name = "content")
    val content: String?,
    @field:Json(name = "imageUrls")
    val imageUrls: List<String?>?,
    @field:Json(name = "tag")
    val tag: String?,
    @field:Json(name = "createdAt")
    val createdAt: Long?,
    @field:Json(name = "updatedAt")
    val updatedAt: Long?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readSerializable() as? Drink,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeSerializable(drink)
        parcel.writeValue(emotion)
        parcel.writeValue(diaryDt)
        parcel.writeString(content)
        parcel.writeStringList(imageUrls)
        parcel.writeString(tag)
        parcel.writeValue(createdAt)
        parcel.writeValue(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReadDiaryDetailResponse> {
        override fun createFromParcel(parcel: Parcel): ReadDiaryDetailResponse {
            return ReadDiaryDetailResponse(parcel)
        }

        override fun newArray(size: Int): Array<ReadDiaryDetailResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class Drink(
    @field:Json(name = "type")
    val type: Int?,
    @field:Json(name = "count")
    val count: Double?,
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "unit")
    val unit: String?,
) : Serializable
