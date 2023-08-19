package com.rummy.sulung.network.userInfo

import android.os.Parcel
import android.os.Parcelable

data class UserInfoResponse(
    val diaryCount: Int,
    val email: String,
    val id: Int,
    val lastDiaryDays: Int,
    val loginType: String,
    val nickName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?:  "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(diaryCount)
        parcel.writeString(email)
        parcel.writeInt(id)
        parcel.writeInt(lastDiaryDays)
        parcel.writeString(loginType)
        parcel.writeString(nickName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfoResponse> {
        override fun createFromParcel(parcel: Parcel): UserInfoResponse {
            return UserInfoResponse(parcel)
        }

        override fun newArray(size: Int): Array<UserInfoResponse?> {
            return arrayOfNulls(size)
        }
    }
}
