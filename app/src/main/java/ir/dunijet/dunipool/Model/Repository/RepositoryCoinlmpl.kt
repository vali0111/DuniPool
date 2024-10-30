package ir.dunijet.dunipool.Model.Repository

import io.reactivex.rxjava3.core.Single
import ir.dunijet.dunipool.Utils.ALL
import ir.dunijet.dunipool.Model.Net.ApiService
import ir.dunijet.dunipool.Model.DataClass.ChartData
import ir.dunijet.dunipool.Utils.HISTO_DAY
import ir.dunijet.dunipool.Utils.HISTO_HOUR
import ir.dunijet.dunipool.Utils.HISTO_MINUTE
import ir.dunijet.dunipool.Utils.HOUR
import ir.dunijet.dunipool.Utils.HOURS24
import ir.dunijet.dunipool.Utils.MONTH
import ir.dunijet.dunipool.Utils.MONTH3
import ir.dunijet.dunipool.Utils.WEEK
import ir.dunijet.dunipool.Utils.YEAR

class RepositoryCoinlmpl(
    private val apiService: ApiService
) {
     fun getChartData(
         symbol: String,
         period: String,
     ):Single<ChartData>{
         var histoPeriod = ""
         var limit = 30
         var aggregate = 1

         when (period) {

             HOUR -> {
                 histoPeriod = HISTO_MINUTE
                 limit = 60
                 aggregate = 12
             }

             HOURS24 -> {
                 histoPeriod = HISTO_HOUR
                 limit = 24
             }

             MONTH -> {
                 histoPeriod = HISTO_DAY
                 limit = 30
             }

             MONTH3 -> {
                 histoPeriod = HISTO_DAY
                 limit = 90
             }

             WEEK -> {
                 histoPeriod = HISTO_HOUR
                 aggregate = 6
             }

             YEAR -> {
                 histoPeriod = HISTO_DAY
                 aggregate = 13
             }

             ALL -> {
                 histoPeriod = HISTO_DAY
                 aggregate = 30
                 limit = 2000
             }

         }
         return apiService.getChartData(histoPeriod, symbol, limit, aggregate)
     }

}