package ir.dunijet.dunipool.Model.Repository

import io.reactivex.rxjava3.core.Single
import ir.dunijet.dunipool.Model.Net.ApiService
import ir.dunijet.dunipool.Model.DataClass.CoinsData
import ir.dunijet.dunipool.Model.DataClass.NewsData

class Repositorylmpl(
    private val  apiService: ApiService
) {

    fun getNews(): Single<NewsData> {
        return apiService.getTopNews()
    }

     fun getCoinsList(): Single<CoinsData> {
         return apiService.getTopCoins()
     }
}