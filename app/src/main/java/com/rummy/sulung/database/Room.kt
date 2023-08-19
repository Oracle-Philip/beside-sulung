package com.rummy.sulung.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.rummy.sulung.network.converter.DiaryDetailDrinkConverter
import com.rummy.sulung.network.converter.DiaryListItemConverter
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.response.ReadDiaryYearMonthResponse
import com.rummy.sulung.network.response.ReadStoreListResponse
import java.util.concurrent.Executors

@Dao
interface SulungDao {
    /*
        유저정보
     */

    /*
        다이어리 정보
     */
    @Query("SELECT * FROM DIARYLISTYEARMONTH")
    fun getDiaryListYearMonthInfo() : LiveData<ReadDiaryYearMonthResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiaryListYearMonth(readDiaryYearMonthResponse: ReadDiaryYearMonthResponse)
    @Query("DELETE FROM DIARYLISTYEARMONTH")
    fun deleteDiaryListYearMonth()

    @Query("SELECT * FROM DIARYLIST")
    fun getDiaryListInfo() : LiveData<ReadDiaryListResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiaryList(readDiaryListResponse: ReadDiaryListResponse)
    @Query("DELETE FROM DIARYLIST")
    fun deleteDiaryList()

    @Query("SELECT * FROM DIARYDETAIL")
    fun getDiaryDetailInfo() : LiveData<ReadDiaryDetailResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiaryDetail(readDiaryDetailResponse: ReadDiaryDetailResponse)
    @Query("DELETE FROM DIARYDETAIL")
    fun deleteDiaryDetail()

    /*
        술창고 정보
     */
    @Query("SELECT * FROM STORELIST")
    fun getStoreListInfo() : LiveData<ReadStoreListResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoreList(readStoreListResponse: ReadStoreListResponse)
    @Query("DELETE FROM STORELIST")
    fun deleteStoreList()
}

@Database(version = 9, entities = [ReadDiaryYearMonthResponse::class, ReadDiaryDetailResponse::class, ReadDiaryListResponse::class, ReadStoreListResponse::class])
@TypeConverters(DiaryListItemConverter::class, DiaryDetailDrinkConverter::class)
abstract class SulungDatabase: RoomDatabase(){
    abstract val sulungDao: SulungDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: SulungDatabase
        private val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getDatabase(context: Context) : SulungDatabase {
            synchronized(SulungDatabase::class.java){
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SulungDatabase::class.java,
                        "sulung-cache")
                        //.addCallback(sRoomDatabaseCallback)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}