package ir.dunijet.dunipool.di

import ir.dunijet.dunipool.Model.Repository.RepositoryCoinlmpl
import ir.dunijet.dunipool.Model.Repository.Repositorylmpl
import ir.dunijet.dunipool.Model.Net.ApiServiceSingleton
import ir.dunijet.dunipool.features.coinActivity.CoinViewModel
import ir.dunijet.dunipool.features.marketActivity.MarketViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val myModules = module {
    single { Repositorylmpl(get()) }
    single { ApiServiceSingleton.apiService!! } // اگر apiService Singleton دارید
    single { RepositoryCoinlmpl(get()) } // تزریق Repository
    viewModel { CoinViewModel(get()) } // تزریق ViewModel
    viewModel { MarketViewModel(get()) } // تزریق Repository به ViewModel

}

