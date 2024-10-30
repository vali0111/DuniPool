package ir.dunijet.dunipool.features.marketActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ir.dunijet.dunipool.Model.DataClass.CoinsData
import ir.dunijet.dunipool.databinding.ActivityMarketBinding
import ir.dunijet.dunipool.features.coinActivity.CoinActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {
    private lateinit var binding: ActivityMarketBinding
    private lateinit var adapter: MarketAdapter
    private lateinit var dataNews: ArrayList<Pair<String, String>>
    private val marketViewModel: MarketViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.layoutToolbar.toolbar.title = "Dunipool Market"

        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            initUi()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)
        }

        // بارگذاری اطلاعات درباره سکه‌ها
        marketViewModel.loadAboutDataFromAssets(applicationContext)

        initUi()
    }

    private fun initUi() {
        marketViewModel.getNews()
        marketViewModel.getCoinsList()

        // Observe news data
       marketViewModel._newsLivedata.observe(this) { result ->
            result?.let {
                dataNews = it
                refreshNews()
            }
        }

        // Observe coins data
        marketViewModel._coinsLivedata.observe(this) { result ->
            showDataInRecycler(cleanData(result))
        }

        // Observe aboutDataMap
        marketViewModel.aboutDataMap.observe(this) { aboutData ->
            // در اینجا می‌توانید اقداماتی بر اساس aboutData انجام دهید، اگر لازم باشد
        }
    }

    private fun refreshNews() {
        if (dataNews.isNotEmpty()) {
            val randomAccess = (0 until dataNews.size).random()
            binding.layoutNews.txtNews.text = dataNews[randomAccess].first
            binding.layoutNews.imgNews.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
                startActivity(intent)
            }
            binding.layoutNews.txtNews.setOnClickListener {
                refreshNews()
            }
        }
    }

    private fun cleanData(data: List<CoinsData.Data>): List<CoinsData.Data> {
        return data.filter { it.rAW != null || it.dISPLAY != null }
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        adapter = MarketAdapter(ArrayList(data), this)
        binding.layoutWatchlist.recyclerMain.adapter = adapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
    }

    override fun onCoinItemClicked(dataCoin: CoinsData.Data) {
        try {
            marketViewModel.aboutDataMap.value?.let { aboutDataMap ->
                val aboutData = aboutDataMap[dataCoin.coinInfo.name]
                println(aboutDataMap.values.size)
                if (aboutData != null) {
                    val intent = Intent(this, CoinActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("bundle1", dataCoin)
                    bundle.putParcelable("bundle2", aboutData)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "اطلاعات موجود نیست", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "اطلاعات موجود نیست", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("MarketActivity", "Error while handling coin item click", e)
            Toast.makeText(this, "خطا در بارگذاری اطلاعات", Toast.LENGTH_SHORT).show()
        }
    }

}
