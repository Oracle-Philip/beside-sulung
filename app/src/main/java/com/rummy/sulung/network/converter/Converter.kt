package com.rummy.sulung.network.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.rummy.sulung.network.response.Drink
import com.rummy.sulung.network.response.Item

class DiaryListItemConverter{
    @TypeConverter fun listToJson(value: List<Item>?) : String? = Gson().toJson(value)
    @TypeConverter fun jsonToList(value: String?) : List<Item>? = Gson().fromJson(value, Array<Item>::class.java)?.toList()
}

class DiaryDetailDrinkConverter{
    @TypeConverter fun listToJson(value: Drink?) : String? = Gson().toJson(value)
    @TypeConverter fun jsonToList(value: String?) : Drink? = Gson().fromJson(value, Drink::class.java)
    @TypeConverter fun listToJson2(value: List<String?>?) : String? = Gson().toJson(value)
    @TypeConverter fun jsonToList2(value: String?) : List<String?>? = Gson().fromJson(value, Array<String?>::class.java)?.toList()
}

/*
class StoreListItemConverter{
    @TypeConverter fun listToJson(value: List<StoreItem>?) : String? = Gson().toJson(value)
    @TypeConverter fun jsonToList(value: String?) : List<StoreItem>? = Gson().fromJson(value, Array<StoreItem>::class.java)?.toList()
}

class StoreDiaryListItemConverter{
    @TypeConverter fun listToJson(value: List<StoreDiary>?) : String? = Gson().toJson(value)
    @TypeConverter fun jsonToList(value: String?) : List<StoreDiary>? = Gson().fromJson(value, Array<StoreDiary>::class.java)?.toList()
}*/
