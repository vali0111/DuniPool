package ir.dunijet.dunipool.features.marketActivity

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import com.google.gson.Gson
import ir.dunijet.dunipool.Model.Repository.Repositorylmpl
import ir.dunijet.dunipool.Model.DataClass.CoinAboutData
import ir.dunijet.dunipool.Model.DataClass.CoinAboutItem
import ir.dunijet.dunipool.Model.DataClass.CoinsData
import ir.dunijet.dunipool.Model.DataClass.NewsData

class MarketViewModel(
    private val repositorylmpl: Repositorylmpl
) : ViewModel() {
    val _newsLivedata = MutableLiveData<ArrayList<Pair<String, String>>>()
    val _coinsLivedata = MutableLiveData<List<CoinsData.Data>>()
    val aboutDataMap = MutableLiveData<Map<String, CoinAboutItem>>()
    private val disposables = CompositeDisposable()

    fun getNews() {
        repositorylmpl
            .getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<NewsData> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.e("MarketViewModel", "Error fetching news", e)
                    _newsLivedata.postValue(ArrayList()) // یا می‌توانید یک حالت خطا را مدیریت کنید
                }

                override fun onSuccess(t: NewsData) {
                    val newsList = ArrayList<Pair<String, String>>()
                    t.data.forEach {
                        newsList.add(Pair(it.title, it.url))
                    }
                    _newsLivedata.postValue(newsList)
                }
            })
    }

    fun getCoinsList() {
        repositorylmpl
            .getCoinsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<CoinsData> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.e("MarketViewModel", "Error fetching coins", e)
                    _coinsLivedata.postValue(emptyList()) // یا مدیریت خطا
                }

                override fun onSuccess(t: CoinsData) {
                    _coinsLivedata.postValue(t.data)
                }
            })
    }

    fun loadAboutDataFromAssets(applicationContext: Context) {
        try {
            val fileInString = applicationContext.assets.open("currencyinfo.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val dataAboutAll = gson.fromJson(fileInString, CoinAboutData::class.java)

            val aboutMap = mutableMapOf<String, CoinAboutItem>()
            dataAboutAll.forEach {
                aboutMap[it.currencyName] = CoinAboutItem(
                    it.info.web,
                    it.info.github,
                    it.info.twt,
                    it.info.desc,
                    it.info.reddit
                )
            }
            aboutDataMap.postValue(aboutMap)
        } catch (e: Exception) {
            Log.e("MarketViewModel", "Error loading about data", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
