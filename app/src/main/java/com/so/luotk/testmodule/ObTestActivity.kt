package com.so.luotk.testmodule

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.so.luotk.R
import com.so.luotk.adapter.TestSectionsAdapter
import com.so.luotk.client.MyClient
import com.so.luotk.client.ResponseObject
import com.so.luotk.databinding.ActivityObTestBinding
import com.so.luotk.databinding.ConfirmStartDialogBinding
import com.so.luotk.fragments.batches.AssignmentTestVideoListFragment
import com.so.luotk.models.output.ObjectiveTestDetailsResponse
import com.so.luotk.models.output.TestSection
import com.so.luotk.utils.PreferenceHandler
import com.so.luotk.utils.Utilities
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ObTestActivity : AppCompatActivity() {
    private var testId: String? = "0"
    lateinit var binding: ActivityObTestBinding
    lateinit var sectionAdapter: TestSectionsAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var isEmptyTest = false
    var isTestSubmitted = false
    override fun onStart() {
        super.onStart()
        if (isTestSubmitted)
            onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.restrictScreenShot(this)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SECURE)
        binding = ActivityObTestBinding.inflate(layoutInflater)
        Utilities.setUpStatusBar(this)
        setContentView(binding.root)
        binding.isLoading = true
        intent?.let {
            testId = it.getStringExtra(PreferenceHandler.TESTID)
        }
        binding.toolbar.navigationIcon = Utilities.setNavigationIconColorBlack(this)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (Utilities.checkInternet(this))
            hitgetDetailsService()
        else
            Utilities.makeToast(this, getString(R.string.internet_connection_error))
        sectionAdapter = TestSectionsAdapter(object : TestSectionsAdapter.StartClickListener {
            override fun onClick(position: Int) {
                // start(position)
                Log.e("TAG", "onClickListenerCalled")
                val distanceInPixels: Int
                val firstVisibleChild: View = binding.sectionsRecycler.getChildAt(0)
                val itemHeight = firstVisibleChild.height
                val currentPosition: Int = binding.sectionsRecycler.getChildAdapterPosition(firstVisibleChild)
                val p = Math.abs(position - currentPosition)
                distanceInPixels = if (p > 5) (p - (p - 5)) * itemHeight else p * itemHeight
                Log.e("TAG", "onClick:position  $position    distance $distanceInPixels")
                (binding.sectionsRecycler.layoutManager as LinearLayoutManager).scrollVerticallyBy(distanceInPixels, binding.sectionsRecycler.Recycler(), RecyclerView.State())
            }

        })
        binding.sectionsRecycler.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = sectionAdapter
            setHasFixedSize(false)
        }


        binding.activity = this
    }

    override fun onBackPressed() {
        Log.e("TAG", "onBackPressed:  isTestSubmitted $isTestSubmitted")
        if (isTestSubmitted)
            setResultData()
        else finish()
    }

    fun opnStartTestWarningDialog() {
        var dialogBinding = ConfirmStartDialogBinding.inflate(LayoutInflater.from(this), null, false)
        var dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.apply {
            setContentView(dialogBinding.root)
            if (!isFinishing)
                show()
        }
        dialogBinding.btnCancel.setOnClickListener {
            if (!isFinishing)
                dialog.dismiss()
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.startTstBtn.setOnClickListener {
            if (!isFinishing)
                dialog.dismiss()
            start()
        }
    }

    private fun setResultData() {
        val intent = Intent()
        intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true)
        intent.putExtra(PreferenceHandler.IS_FROM, "test")
        setResult(AssignmentTestVideoListFragment.REQUEST_UPDATE_SUBMIT_STATUS, intent)
        finish()
    }

    private fun hitgetDetailsService() {
        MyClient(this).getObjectiveTestDetails(testId, object : ResponseObject {
            override fun OnCallback(content: Any?, error: String?) {
                content?.let {
                    val response = it as ObjectiveTestDetailsResponse
                    if (response.success!! && response.status!!.equals(200)) {
                        binding.testData = response.result
                        binding.submitted = (response.result?.testStatus == 1)

                        binding.testStatus = if (response.result?.testStatus == 1) getString(R.string.txt_submitted) else getString(R.string.pending)
                        binding.quest = response.result?.totalQuestion.toString()
                        binding.duration = response.result?.duration.toString()
                        binding.testName.text = response.result?.topic
                        if (!binding.submitted) checkTestValidty()
                        response.result?.sections?.let {

                            it1 ->


                            val filterList = mutableListOf<TestSection>()
                            for (section: TestSection in it1) {
                                if (section.questionCount > 0)
                                    filterList.add(section)
                            }
                            if (filterList.size == 0)
                                binding.isEmptyTest = true
                            sectionAdapter.updateList(filterList)

                        } ?: kotlin.run { binding.isEmptyTest = false }
                    }

                }
                        ?: kotlin.run { Utilities.makeToast(this@ObTestActivity, getString(R.string.server_error)) }
                binding.isLoading = false
            }

        })
    }

    private fun checkTestValidty() {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale("en", "in"))
        val formattedDate: String = df.format(c)
        Log.e("TAG", "checkTestValidty: formatted current date $formattedDate")
        val curentDate: Date = df.parse(formattedDate)
        val startDate = df.parse(binding.testData?.startDate + " " + binding.testData?.startTime)
        val endDate = df.parse(binding.testData?.submitDate + " " + binding.testData?.submitTime)
        Log.e("TAG", "checkTestValidty: startdate :${binding.testData?.startDate + " " + binding.testData?.startTime} endDate ${binding.testData?.submitDate + " " + binding.testData?.submitTime} ")
        Log.e("TAG", "checkTestValidty: currentDate :$curentDate   startDate: $startDate endDate:  $endDate")
        if (curentDate.before(startDate)) {
            binding.isTestValid = false
            binding.testStatus = getString(R.string.to_be_started)
        } else if (curentDate.after(endDate)) {
            binding.isTestValid = false
            binding.testStatus = getString(R.string.expired)
        } else {

            binding.isTestValid = true
        }

    }

    private fun getTimeInMillis(duration: String): Long? {
        val timeList = duration.split(" ")
        return TimeUnit.HOURS.toMillis(timeList[0].toLong()) +
                TimeUnit.MINUTES.toMillis(timeList[2].toLong())
    }

    fun start() {
        startActivityForResult(
                Intent(
                        this,
                        TestActivity::class.java
                ).putExtra(PreferenceHandler.DATA, Gson().toJson(binding.testData).toString()
                ).putExtra("isConfigChange", false
                ), 1)
        /*    finish()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "onActivityResult: $resultCode ")
        if (resultCode == 1) {
            isTestSubmitted = true
            Log.e("TAG", "onActivityResult: testSubmitted= $isTestSubmitted ")
        }
    }
}