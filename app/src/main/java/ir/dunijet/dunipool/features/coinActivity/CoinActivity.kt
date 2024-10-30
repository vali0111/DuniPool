package ir.dunijet.dunipool.features.coinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Html
import android.widget.Toast
import androidx.annotation.RequiresApi
import ir.dunijet.dunipool.R
import ir.dunijet.dunipool.Model.DataClass.ChartData
import ir.dunijet.dunipool.Model.DataClass.CoinAboutItem
import ir.dunijet.dunipool.Model.DataClass.CoinsData
import ir.dunijet.dunipool.Utils.ALL
import ir.dunijet.dunipool.Utils.BASE_URL_TWITTER
import ir.dunijet.dunipool.Utils.HOUR
import ir.dunijet.dunipool.Utils.HOURS24
import ir.dunijet.dunipool.Utils.MONTH
import ir.dunijet.dunipool.Utils.MONTH3
import ir.dunijet.dunipool.Utils.WEEK
import ir.dunijet.dunipool.Utils.YEAR
import ir.dunijet.dunipool.databinding.ActivityCoinBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class CoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoinBinding
    private lateinit var dataThisCoin: CoinsData.Data
    private lateinit var dataThisCoinAbout: CoinAboutItem
    private val coinViewModel: CoinViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // اطمینان از وجود داده‌ها در Intent
        intent.getBundleExtra("bundle")?.let { fromIntent ->
            dataThisCoin = fromIntent.getParcelable("bundle1") ?: throw IllegalArgumentException("Invalid coin data")
            dataThisCoinAbout = fromIntent.getParcelable("bundle2") ?: CoinAboutItem("", "", "", "", "")
        } ?: throw IllegalArgumentException("Bundle is null")

        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName

        initUi()
        observeViewModel() // فقط یک بار ثبت می‌کنیم
    }

    private fun initUi() {
        initChartUi()
        initStatisticsUi()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initAboutUi()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initAboutUi() {
        binding.layoutAbout.apply {
            txtWebsite.text = dataThisCoinAbout.coinWebsite
            txtGithub.text = dataThisCoinAbout.coinGithub
            txtReddit.text = dataThisCoinAbout.coinReddit
            txtTwitter.text = "@" + dataThisCoinAbout.coinTwitter
            txtAboutCoin.text = Html.fromHtml(dataThisCoinAbout.coinDesc, Html.FROM_HTML_MODE_COMPACT)

            txtWebsite.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinWebsite) }
            txtGithub.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinGithub) }
            txtReddit.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinReddit) }
            txtTwitter.setOnClickListener { openWebsiteDataCoin("$BASE_URL_TWITTER${dataThisCoinAbout.coinTwitter}") }
        }
    }

    private fun openWebsiteDataCoin(url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "آدرس وب سایت معتبر نیست", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initStatisticsUi() {
        binding.layoutStatistics.apply {
            tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
            tvTodaysHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
            tvTodayLowAmount.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
            tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
            tvAlgorithm.text = dataThisCoin.coinInfo.algorithm
            tvTotalVolume.text = dataThisCoin.dISPLAY.uSD.tOTALVOLUME24H
            tvAvgMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
            tvSupplyNumber.text = dataThisCoin.dISPLAY.uSD.sUPPLY
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {
        var period: String = HOUR
        requestAndShowChart(period)

        binding.layoutChart.radioGroupMain.setOnCheckedChangeListener { _, checkedId ->
            period = when (checkedId) {
                R.id.radio_12h -> HOUR
                R.id.radio_1d -> HOURS24
                R.id.radio_1w -> WEEK
                R.id.radio_1m -> MONTH
                R.id.radio_3m -> MONTH3
                R.id.radio_1y -> YEAR
                R.id.radio_all -> ALL
                else -> HOUR // مقدار پیش‌فرض
            }
            requestAndShowChart(period)
        }

        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = " " + dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        binding.layoutChart.txtChartChange2.text = if (dataThisCoin.coinInfo.fullName == "BUSD") {
            "0%"
        } else {
            dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }

        val taghir = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        if (taghir > 0) {
            setChartChangeColor(R.color.colorGain, "▲")
        } else if (taghir < 0) {
            setChartChangeColor(R.color.colorLoss, "▼")
        }

        binding.layoutChart.sparkviewMain.setScrubListener {
            binding.layoutChart.txtChartPrice.text = it?.let { "$ ${(it as ChartData.Data).close}" } ?: dataThisCoin.dISPLAY.uSD.pRICE
        }
    }

    private fun setChartChangeColor(colorRes: Int, upDown: String) {
        binding.layoutChart.apply {
            txtChartChange2.setTextColor(ContextCompat.getColor(root.context, colorRes))
            txtChartUpdown.setTextColor(ContextCompat.getColor(root.context, colorRes))
            txtChartUpdown.text = upDown
            sparkviewMain.lineColor = ContextCompat.getColor(root.context, colorRes)
        }
    }

    private fun observeViewModel() {
        coinViewModel._chartLivedata.observe(this) {
            val chartAdapter = ChartAdapter(it.first, it.second?.open.toString())
            binding.layoutChart.sparkviewMain.adapter = chartAdapter
        }
    }

    private fun requestAndShowChart(period: String) {
        coinViewModel.getChartData(dataThisCoin.coinInfo.name, period)
    }
}
