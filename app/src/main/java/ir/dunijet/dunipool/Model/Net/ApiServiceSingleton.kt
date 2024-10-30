package ir.dunijet.dunipool.Model.Net

import ir.dunijet.dunipool.Utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceSingleton {
    var apiService: ApiService? = null
        get() {
            if (field == null) {
                val retrofit = Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()

                field = retrofit.create(ApiService::class.java)
            }
            return field
        }
}