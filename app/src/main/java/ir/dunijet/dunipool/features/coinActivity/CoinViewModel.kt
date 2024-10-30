package ir.dunijet.dunipool.features.coinActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import ir.dunijet.dunipool.Model.Repository.RepositoryCoinlmpl
import ir.dunijet.dunipool.Model.DataClass.ChartData
import android.util.Log

class CoinViewModel(
    private val repositoryCoinlmpl: RepositoryCoinlmpl
) : ViewModel() {
    val _chartLivedata = MutableLiveData<Pair<List<ChartData.Data>, ChartData.Data?>>()

    fun getChartData(
        symbol: String,
        period: String,
    ) {
        repositoryCoinlmpl
            .getChartData(symbol, period)
            .subscribe(object : SingleObserver<ChartData> {
                override fun onSubscribe(d: Disposable) {
                    // در اینجا می‌توانید نشان دهید که درخواست در حال پردازش است
                    Log.d("CoinViewModel", "Request started for symbol: $symbol, period: $period")
                }

                override fun onError(e: Throwable) {
                    // مدیریت خطا
                    Log.e("CoinViewModel", "Error fetching chart data", e)
                    // می‌توانید در اینجا LiveData دیگری برای خطاها داشته باشید
                }

                override fun onSuccess(t: ChartData) {
                    val data1 = t.data
                    val data2 = t.data.maxByOrNull { it.close.toFloat() }
                    val returningData = Pair(data1, data2)
                    _chartLivedata.postValue(returningData)
                }
            })
    }
}
