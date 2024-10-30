package ir.dunijet.dunipool.Model.Net

import io.reactivex.rxjava3.core.Single
import ir.dunijet.dunipool.Model.DataClass.ChartData
import ir.dunijet.dunipool.Model.DataClass.CoinsData
import ir.dunijet.dunipool.Model.DataClass.NewsData
import ir.dunijet.dunipool.Utils.API_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "popular"
    ): Single<NewsData>


    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") to_symbol :String = "USD" ,
        @Query("limit") limit_data :Int = 10
    ) :Single<CoinsData>


    @Headers(API_KEY)
    @GET("{period}")
    fun getChartData(
        @Path("period") period :String ,
        @Query("fsym") fromSymbol :String ,
        @Query("limit") limit :Int ,
        @Query("aggregate")  aggregate:Int ,
        @Query("tsym") toSymbol :String = "USD"
    ) :Single<ChartData>


}