package com.rummy.sulung.network.service

import com.rummy.sulung.network.ApiGenerator
import com.rummy.sulung.network.request.*
import com.rummy.sulung.network.response.*
import com.rummy.sulung.network.signin.SigninRequest
import com.rummy.sulung.network.response.SigninResponse
import com.rummy.sulung.network.userInfo.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface SulungApiService {
//    eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNjczMDQ0NjA3LCJleHAiOjQ4MjY2NDQ2MDd9.Jbko6Y99IyGwMsscb1hc1BfFk45mgF2QzzIuHPFlIIs
    @PUT("/user")
    suspend fun editUser(@Body request: modNickNameRequest)
        : Response<ModNickNameResponse>
    @POST("/user/terms")
    suspend fun regTerms(@Body request: TermsConcentedRequest)
        : Response<TermsConcentedResponse>
    @POST("/signin/kakao")
    suspend fun signin(@Body request: SigninRequest)
            : Response<SigninResponse>
    @GET("/user-info")
    suspend fun userInfo()
            : Response<UserInfoResponse>
    @DELETE("/user")
    suspend fun deleteUser()
            : Response<DeleteUserResponse>

    @GET("/diary/store/monthly")
    suspend fun store_list_monthly(
        @Query ("drinkType") drinkType: String? = null,
        @Query ("emotion") emotion: String? = null,
        @Query ("date") date: String,
        @Query ("size") size: Int? = 80
    ) : Response<GetStoreMonthlyResponse>

    /**
     * @date 2023 4 16
     * @desc 년월 리스트 조회, 월별조회
     */
    @GET("/diary/year-month")
    suspend fun diary_year_month()
            : Response<ReadDiaryYearMonthResponse>

    @GET("/diary/list/monthly")
    suspend fun diary_list_monthly(
        @Query ("date") date: String,
        @Query ("size") size: Int? = 10,
        @Query ("sort") sort: String? = "NEWEST"
    ) : Response<ReadDiaryListResponse>

    @POST("/diary")
    suspend fun regEasyDiary(@Body request: DiaryEasyRegRequest)
            : Response<RegDiaryEasilyResponse>
    @DELETE("/diary/{id}")
    suspend fun deleteDiary(
        @Path("id") id: Int
    ) : Response<DeleteDiaryResponse>
    @Multipart
    @PUT("/diary")
    suspend fun regDetailDiary(
        @Part ("content") content: String,
        @Part ("diaryDt") diaryDt : Long,
        @Part ("drinkCount") drinkCount : Double,
        @Part ("drinkUnit") drinkUnit : String,
        @Part ("drinkName") drinkName : String,
        @Part ("drinkType") drinkType : Int,
        @Part ("emotion") emotion : Int,
        @Part ("id") id : Int,
        @Part imageFiles : List<MultipartBody.Part?>,
        @Part ("tag") tag : String
    ) : Response<RegDiaryDetailResponse>
    @GET("/diary/{id}")
    suspend fun diaryDetail(
        @Path("id") id: Int
    ) : Response<ReadDiaryDetailResponse>
    @GET("/diary/list")
    suspend fun diaryListFirstPage(
        @Query ("sort") sort: String = "OLDEST"
    ): Response<ReadDiaryListResponse>
    @GET("/diary/list")
    suspend fun diaryList(
        @Query ("preCursor") preCursor: Int?,
        @Query ("nextCursor") nextCursor: Int?,
        @Query ("size") size: Int = 80,
        @Query ("sort") sort: String = "OLDEST"
    ): Response<ReadDiaryListResponse>
    @GET("/diary/store")
    suspend fun storeList(
        @Query ("drinkType") drinkType: String?,
        @Query ("emotion") emotion: String?,
        @Query ("preCursor") preCursor: Int?,
        @Query ("nextCursor") nextCursor: Int?,
        @Query ("size") size: Int?
    ) : Response<ReadStoreListResponse>

    @GET("/diary/store")
    suspend fun storeListArray(
        @Query ("drinkType") drinkType: Array<Int>?,
        @Query ("emotion") emotion: Array<Int>?,
        @Query ("preCursor") preCursor: Int?,
        @Query ("nextCursor") nextCursor: Int?,
        @Query ("size") size: Int?
    ) : Response<ReadStoreListResponse>


    companion object {
        val instance = ApiGenerator()
            .generate(SulungApiService::class.java)
    }
}