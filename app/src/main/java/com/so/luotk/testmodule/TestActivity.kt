package com.so.luotk.testmodule

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.so.luotk.R
import com.so.luotk.adapter.ExerciseAdapter
import com.so.luotk.adapter.ExerciseLayoutAdapter
import com.so.luotk.client.MyClient
import com.so.luotk.client.ResponseObject
import com.so.luotk.databinding.ActivityTestBinding
import com.so.luotk.databinding.LayoutTimeOutDialogBinding
import com.so.luotk.databinding.SubmitTestDialogBinding
import com.so.luotk.databinding.TestBackWarningBinding
import com.so.luotk.models.AnswerStatus
import com.so.luotk.models.output.KtServerResponse
import com.so.luotk.models.output.ObjecticeTestDetailsResult
import com.so.luotk.models.output.TestQuestion
import com.so.luotk.models.output.TestSection
import com.so.luotk.utils.PreferenceHandler
import com.so.luotk.utils.Utilities
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    private val TAG = this.javaClass.simpleName
    private var position = 0
    var currentItem = 0
    private var combinedList = mutableListOf<TestQuestion>()
    private var firstQuestionIndexList = mutableListOf<Int>()
    private var sectionNames = mutableListOf<String>()
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var listAdapter: ExerciseLayoutAdapter
    private lateinit var gridAdapter: ExerciseLayoutAdapter
    lateinit var submitDialogBinding: SubmitTestDialogBinding
    private var testData = ObjecticeTestDetailsResult()
    private var timeLeft: Long? = 0
    private var countDownTimer: CountDownTimer? = null
    private var queSec: Long = 1000
    var testId = " "
    private var timeFormat = " "
    private var isConfigChange = false
    var dontChangeSpinner = false
    var isTestSubmitted = false
    lateinit var spinner_adapter: ArrayAdapter<String>
    lateinit var submitDialog: Dialog
    private val questionHandler: Handler = Handler(Looper.myLooper()!!)
    private val quesRunnable: Runnable = Runnable {
        startQuestionTimer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentNightMode: Int = applicationContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        Utilities.restrictScreenShot(this)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SECURE)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlackGray)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        submitDialogBinding = SubmitTestDialogBinding.inflate(layoutInflater, null, false)
        submitDialog = Dialog(this)
        submitDialog.setContentView(submitDialogBinding.root)
        submitDialogBinding.markedForReview = "0"
        submitDialogBinding.totalQues = "0"
        submitDialogBinding.answeredQues = "0"
        submitDialogBinding.timeLeft = "0"
        binding.currentPosition = 0
        binding.time = getString(R.string.test_solution)
        binding.levelColor = "#808080"
        exerciseAdapter = ExerciseAdapter(object : ExerciseAdapter.TypeClickListener {
            override fun onClick(position: Int, type: Int, result: TestQuestion?) {
                when (type) {

                    0, 1 -> {
                        dontChangeSpinner = true
                        nextPage(position, result!!)
                        gridAdapter.updatePositionList(position, result)
                        listAdapter.updatePositionList(position, result)
                        binding.ans = getAttemptedCount()
                    }
                    2 -> {
                        dontChangeSpinner = true
                        previousPage(position, result!!)
                        gridAdapter.updatePositionList(position, result)
                        listAdapter.updatePositionList(position, result)
                        binding.ans = getAttemptedCount()
                    }
                    3 -> {
                        dontChangeSpinner = true
                        result?.answerStatus?.time_taken = TimeUnit.MILLISECONDS.toSeconds(queSec).toInt()
                        exerciseAdapter.updateItem(position, result!!)
                        binding.ans = getAttemptedCount()
                        openDialog()

                    }
                    4 -> {
                        finish()
                    }
                }

            }
        }, object : ExerciseAdapter.UpdateItemListener {
            override fun onClick(position: Int, result: TestQuestion) {

            }

        })
        gridAdapter = ExerciseLayoutAdapter(2, object : ExerciseLayoutAdapter.QuestionClickListener {
            override fun onclick(position: Int) {
                openPage(position)
            }

        })
        listAdapter = ExerciseLayoutAdapter(1, object : ExerciseLayoutAdapter.QuestionClickListener {
            override fun onclick(position: Int) {
                openPage(position)
            }

        })
        binding.exercisePager.apply {
            adapter = exerciseAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        intent?.let {
            testData = Gson().fromJson(it.getStringExtra(PreferenceHandler.DATA), ObjecticeTestDetailsResult::class.java)
            isConfigChange = it.getBooleanExtra("isConfigChange", false)
            binding.isLoading = true
            testData.let { it1 ->
                setData()
            }

        }
        binding.activity = this
        binding.spinnerSection.setSelection(position)
        binding.exercisePager.isUserInputEnabled = false
        binding.exercisePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItem = position
                binding.currentPosition = currentItem
                val question = exerciseAdapter.getListItem(position)
                Log.e("TAG", "page changed: ${question.answerStatus?.option} ${question.quesStat}")
                binding.bookmark = (question.quesStat == 3 || question.quesStat == 2)
                queSec = TimeUnit.SECONDS.toMillis(
                        question.answerStatus?.time_taken?.toLong()
                                ?: 0)
                getLevel(question.level ?: 0)
                var i = 0
                for (index in firstQuestionIndexList) {

                    if (index == position) {
                        binding.spinnerSection.setSelection(i)
                        break
                    }
                    if (index > position) {
                        binding.spinnerSection.setSelection(i - 1)
                        break
                    }
                    i++

                }
                binding.negMark = question.markingCreteria?.wrongMarks.toString()
                binding.posMark = question.markingCreteria?.correctMarks.toString()
                gridAdapter.updatePositionList(position, question)
                listAdapter.updatePositionList(position, question)
                Handler(Looper.myLooper()!!).postDelayed(Runnable { dontChangeSpinner = false }, 2000)
            }

        })

        binding.spinnerSection.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                (parent.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(this@TestActivity, R.color.textColorWhite))
                if (!dontChangeSpinner) {
                    binding.exercisePager.setCurrentItem(firstQuestionIndexList[position], false)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        binding.exerciseGridRecycler.apply {
            layoutManager = GridLayoutManager(context, 5)
            setHasFixedSize(true)
            adapter = gridAdapter
        }
        binding.exerciseListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = listAdapter
        }


    }

    private fun getAttemptedCount(): Int? {
        var count = 0
        var reviewCount = 0
        for (item in exerciseAdapter.list) {
            if (item.quesStat == 1 || item.quesStat == 2)
                count++
            if (item.quesStat == 2 || item.quesStat == 3)
                reviewCount++

        }
        submitDialogBinding.answeredQues = count.toString()
        submitDialogBinding.markedForReview = reviewCount.toString()
        return count
    }

    fun toggleBookMark() {
        when (exerciseAdapter.list[currentItem].quesStat) {
            0 -> exerciseAdapter.list[currentItem].quesStat = 3
            1 -> exerciseAdapter.list[currentItem].quesStat = 2
            2 -> exerciseAdapter.list[currentItem].quesStat = 1
            3 -> exerciseAdapter.list[currentItem].quesStat = 0
        }
        binding.bookmark = !binding.bookmark
        if (binding.bookmark)
            Utilities.makeToast(this, getString(R.string.marked_review))
        else Utilities.makeToast(this, getString(R.string.removed_review))
        exerciseAdapter.notifyDataSetChanged()
        binding.exercisePager.setCurrentItem(currentItem, false)
    }

    private fun setData() {
        binding.showSol = (testData.testStatus == 1)
        if (testData.testStatus == 1)
            exerciseAdapter.showSol = true
        testData.sections?.let {
            var total = 0

            for (section: TestSection in it) {
                if (section.questionCount > 0) {
                    firstQuestionIndexList.add(total)
                    total += section.questionCount
                    sectionNames.add(section.sectionName!!)
                    section.questions?.let { it1 -> combinedList.addAll(it1) }
                }

            }

            binding.isOneSection = sectionNames.size == 1

            binding.sectionName = sectionNames[0]

            spinner_adapter = ArrayAdapter(this@TestActivity,
                    R.layout.section_spinner_item, sectionNames)
            spinner_adapter.setDropDownViewResource(R.layout.section_spinner_item)
            binding.spinnerSection.adapter = spinner_adapter
            exerciseAdapter.updateList(combinedList, testData.testStatus == 1)
            listAdapter.updateList(combinedList, firstQuestionIndexList, sectionNames)
            gridAdapter.updateList(combinedList, firstQuestionIndexList, sectionNames)
            if (isConfigChange) {
                combinedList.clear()
                var type = object : TypeToken<List<TestQuestion>>() {}.type
                combinedList = Gson().fromJson(intent.getStringExtra("combinedList"), type)
                timeLeft = intent.getLongExtra(PreferenceHandler.TEST_DURATION, 0)
            }
            if (testData.testStatus != 1) {
                val time = getTimeInMillis(testData.duration)
                if (!isConfigChange)
                    timeLeft = time
                binding.exercisePager.setCurrentItem(firstQuestionIndexList[position], false)
                startTimer()
                countDownTimer?.start()
                questionHandler.postDelayed(quesRunnable, 0)
            }
            submitDialogBinding.totalQues = combinedList.size.toString()
            binding.total = combinedList.size
            binding.ans = 0
        }
        binding.isLoading = false

    }

    fun getLevel(level: Int) {

        when (level) {
            1 -> {
                binding.level = "Easy"
                binding.levelColor = "#61CA14"
            }
            2 -> {
                binding.level = "Medium"
                binding.levelColor = "#FF952A"
            }
            3 -> {
                binding.level = "Hard"
                binding.levelColor = "#CC1D1D"
            }
        }
    }

    private fun getTimeInMillis(duration: String): Long? {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale("en", "in"))
        val df2 = SimpleDateFormat("hh:mm a", Locale("en", "in"))
        val formattedDate: String = df.format(c)
        val formattedTime: String = df2.format(c)
        val curentDate: Date = df.parse(formattedDate)
        val currentTime = df2.parse(formattedTime)
        val endDate = df.parse(testData.submitDate)
        val endTime = df2.parse(testData.submitTime)
        var durationMillis: Long = 0

        val timeList = duration.split(" ")
        if ((timeList[0]).toInt() > 0) {
            timeFormat = "HH:mm:ss"

        } else timeFormat = "mm:ss"
        durationMillis = TimeUnit.HOURS.toMillis(timeList[0].toLong()) +
                TimeUnit.MINUTES.toMillis(timeList[2].toLong())
        if (curentDate.equals(endDate)) {
            val diff = (endTime?.time?.minus(currentTime.time))
            if (diff != null) {
                if (diff > 0) {
                    if (diff < durationMillis)
                        durationMillis = diff
                }
            }
        }
        return durationMillis
    }

    private fun openTestFinishDialog(isFinish: Boolean) {
        pauseQuestionTime()
        var dialog = Dialog(this)
        var dialogBinding = LayoutTimeOutDialogBinding.inflate(LayoutInflater.from(this), null, false)
        if (isFinish) {
            dialogBinding.timeOutText.visibility = View.GONE
        }
        dialog.apply {
            setContentView(dialogBinding.root)
            if (!isFinishing)
                show()
            setCancelable(false)
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.btnOk.setOnClickListener {
            if (!isFinishing)
                dialog.dismiss()
            finish()

        }
    }

    private fun startQuestionTimer() {
        val date = Date(queSec)
        val format = SimpleDateFormat("mm:ss", Locale("en", "in"))
        format.timeZone = TimeZone.getTimeZone("UTC")
        binding.qTime = format.format(date)
        queSec += 1000
        questionHandler.postDelayed(quesRunnable, 1000)
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeft!!, 1000) {
            override fun onTick(timeMills: Long) {
                timeLeft = timeMills
                val date = Date(timeMills)
                val format = SimpleDateFormat(timeFormat, Locale("en", "in"))
                format.timeZone = TimeZone.getTimeZone("UTC")
                binding.time = format.format(date)
                submitDialogBinding.timeLeft = format.format(date)
            }

            override fun onFinish() {
                if (!isTestSubmitted) {
                    submitTest(false)
                }

            }

        }

    }


    fun openPage(pos: Int) {
        showListView()
        val result = exerciseAdapter.getListItem(currentItem)
        result.answerStatus?.time_taken = TimeUnit.MILLISECONDS.toSeconds(queSec).toInt()
        exerciseAdapter.updateItem(currentItem, result)
        binding.exercisePager.setCurrentItem(pos, false)

    }

    fun nextPage(position: Int, result: TestQuestion) {
        result.answerStatus?.time_taken = TimeUnit.MILLISECONDS.toSeconds(queSec).toInt()
        exerciseAdapter.updateItem(position, result)
        binding.exercisePager.setCurrentItem(position + 1, false)

    }

    fun previousPage(position: Int, result: TestQuestion) {
        result.answerStatus?.time_taken = TimeUnit.MILLISECONDS.toSeconds(queSec).toInt()
        exerciseAdapter.updateItem(position, result)
        binding.exercisePager.setCurrentItem(position - 1, false)
        /*  exerciseAdapter.updateItem(position, result)*/
    }

    fun showListView() {
        binding.listToggle = !binding.listToggle
    }

    fun gridView() {
        binding.listFlipper.showNext()
        binding.viewFlipper.showNext()
    }

    fun listView() {
        binding.listFlipper.showPrevious()
        binding.viewFlipper.showPrevious()
    }

    fun openDialog() {
        if (binding.showSol) {
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.want_to_quit))
                setNegativeButton(
                        getString(R.string.no)
                ) { dialog, _ ->
                    if (!isFinishing)
                        dialog?.dismiss()
                }
                setPositiveButton(
                        getString(R.string.yes)
                ) { dialog, _ ->
                    if (!isFinishing)
                        dialog?.dismiss()
                    finish()
                }

                create()
                window.setBackgroundDrawableResource(R.drawable.round_corners_drawable)
                show()

            }
        } else {
            if (!isFinishing)
                submitDialog.show()

            submitDialogBinding.submitTstBtn.setOnClickListener {

                if (!isFinishing)
                    submitDialog.dismiss()
                submitTest(true)
            }
            submitDialogBinding.btnCancel.setOnClickListener {

                if (!isFinishing)
                    submitDialog.dismiss()
            }
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(submitDialog.window?.attributes)
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            submitDialog.window?.attributes = layoutParams
            submitDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }

/*    private fun abortTest() {
        pauseQuestionTime()
        AlertDialog.Builder(this).apply {
            setTitle("Abort Test")
            setMessage("Are you sure you want to abort test? ")
            setNegativeButton(
                    "No"
            ) { dialog, _ ->
                dialog?.dismiss()
                resumeQuestionTime()
            }
            setPositiveButton(
                    "Abort"
            ) { dialog, _ ->
                finish()
                dialog?.dismiss()
            }
            create()
            show()
        }
    }*/

    private fun resumeQuestionTime() {
        startTimer()
        countDownTimer?.start()
        questionHandler.postDelayed(quesRunnable, 1000)
    }

    private fun pauseQuestionTime() {
        countDownTimer?.cancel()
        questionHandler.removeCallbacks(quesRunnable)
    }

    private fun submitTest(isFinish: Boolean) {
        pauseQuestionTime()
        Log.e(TAG, "submitTest: $currentItem")
        getAttemptedCount()
        if (exerciseAdapter.list[currentItem].answerStatus?.status == 1)
            exerciseAdapter.list[currentItem].answerStatus?.time_taken = TimeUnit.MILLISECONDS.toSeconds(queSec).toInt()
        binding.isLoading = true
        val combinedAnsList = mutableListOf<AnswerStatus?>()
        val adapterList = exerciseAdapter.list
        Log.e(TAG, "submitTest: ${adapterList.size}")
        for (answer in adapterList) {
            Log.e(TAG, "submitTest: loop $answer")
            if (answer.type == 3 || answer.type == 4)
                answer.answerStatus?.fk_answerId = 0
/*            else answer.answerStatus?.fk_answerId = -1*/
            if (!combinedAnsList.contains(answer.answerStatus))
                combinedAnsList.add(answer.answerStatus)
        }
        Log.e(TAG, "submitTest: $combinedAnsList")
        MyClient(this).submitObTestAns(testData.id.toString(), Gson().toJson(combinedAnsList).toString(), object : ResponseObject {
            override fun OnCallback(content: Any?, error: String?) {
                content?.let {
                    val response: KtServerResponse = content as KtServerResponse
                    if (response.status == 200 && response.success) {
                        if (response.result != null && response.result.equals("success", true)) {
                            timeLeft = 0
                            isTestSubmitted = true
                            setResult(1)
                            Log.e(TAG, "OnCallback in TestActivity: resultcode= 1")
                            openTestFinishDialog(isFinish)
                        }
                    } else if (response.result != null)
                        Utilities.makeToast(this@TestActivity, response.result)

                }
                        ?: kotlin.run { Utilities.makeToast(this@TestActivity, getString(R.string.server_error)) }
                binding.isLoading = false
            }

        })

    }

    fun opnTestWarningDialog() {
        var dialogBinding = TestBackWarningBinding.inflate(LayoutInflater.from(this), null, false)
        var dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.apply {
            setContentView(dialogBinding.root)
            if (!isFinishing)
                show()
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.backToTstBtn.setOnClickListener {
            if (!isFinishing)
                dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        if (binding.showSol)
            super.onBackPressed()
        else
            opnTestWarningDialog()
    }

/*    fun pause() {
        pauseQuestionTime()
        val binding = ResumeDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            create()
            show()
            setCancelable(false)
        }
        binding.abort.setOnClickListener {
            abortTest()
            dialog.dismiss()
        }
        binding.resume.setOnClickListener {
            resumeQuestionTime()
            dialog.dismiss()
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.windowAnimations = R.style.DialogAnimation_2
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.resume_dialog_bc, null
                )
        )
    }*/

    //Auto Submitted On Home Button Click From Phone

//    override fun onStop() {
//        Log.e(TAG, "onStop: ")
//        if (!isTestSubmitted && !binding.showSol)
//            submitTest(true)
//        super.onStop()
//
//    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isTestSubmitted = true
        pauseQuestionTime()
        startActivity(
                Intent(
                        this,
                        TestActivity::class.java
                ).putExtra(PreferenceHandler.DATA, Gson().toJson(testData).toString()
                ).putExtra("isConfigChange", true
                ).putExtra("combinedList", Gson().toJson(exerciseAdapter.list).toString()
                ).putExtra(PreferenceHandler.TEST_DURATION, timeLeft
                )
        )
    }


}