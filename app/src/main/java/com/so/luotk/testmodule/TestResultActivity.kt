package com.so.luotk.testmodule

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.so.luotk.R
import com.so.luotk.client.MyClient
import com.so.luotk.client.ResponseObject
import com.so.luotk.databinding.ActivityTestResultBinding
import com.so.luotk.models.output.ObjecticeTestDetailsResult
import com.so.luotk.models.output.ObjectiveTestDetailsResponse
import com.so.luotk.models.output.TestQuestion
import com.so.luotk.models.output.TestSection
import com.so.luotk.utils.PreferenceHandler
import com.so.luotk.utils.Utilities
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class TestResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestResultBinding
    private val TAG: String = this.javaClass.simpleName
    private lateinit var handler: Handler
    private lateinit var percHandler: Handler
    private lateinit var secHandler: Handler
    private lateinit var runnable: Runnable
    private lateinit var srun: Runnable
    private lateinit var prun: Runnable
    private var answerList = mutableListOf<TestQuestion>()
    private var testDuration: Long = 0
    private lateinit var messageBody: String
    private var marksBody: String = ""
    private var answerBody: String = ""
    private var totalQuestions: Int? = null
    private lateinit var questionData: String
    private lateinit var testData: ObjecticeTestDetailsResult

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.restrictScreenShot(this)
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_SECURE)
        binding = ActivityTestResultBinding.inflate(layoutInflater)
        Utilities.setUpStatusBar(this)
        Utilities.setLocale(this)
        setContentView(binding.root)
        binding.totalMarks = 0.0
        binding.marksObtained = 0.0
        binding.testResultToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        intent?.let { extra ->
            val testId = extra.getStringExtra(PreferenceHandler.TESTID)
            hitService(testId)
        } ?: kotlin.run { Log.e(TAG, "onCreate: No intent") }

    }


    private fun checkSolutionValidity(): Boolean {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale("en", "in"))
        val formattedDate: String = df.format(c)
        val curentDate: Date = df.parse(formattedDate)
        if (testData.showTestResult == 0) {
            Log.e(TAG, "checkSolutionValidity: ${testData.showTestResult}")
            binding.btnViewSol.visibility = View.GONE
            return false
        } else {
            if (testData.whenToDisplayResult == null && testData.testResultDisplayDate == null && testData.testResultDisplayTime == null) {
                binding.btnViewSol.visibility = View.VISIBLE
                return true
            } else {
                if (testData.whenToDisplayResult != null && testData.whenToDisplayResult.equals("1")) {
                    Log.e(TAG, "checkSolutionValidity: $testData.whenToDisplayResult")
                    val endDate = df.parse(testData.submitDate + " " + testData.submitTime)
                    if (curentDate.equals(endDate) || curentDate.after(endDate)) {
                        binding.btnViewSol.visibility = View.VISIBLE
                        return true
                    } else binding.btnViewSol.visibility = View.GONE

                } else {
                    if (testData.testResultDisplayDate != null && testData.testResultDisplayTime != null) {
                        Log.e(TAG, "checkSolutionValidity: $testData.whenToDisplayResult")
                        val showResultDate = df.parse(testData.testResultDisplayDate + " " + testData.testResultDisplayTime)
                        if (curentDate.equals(showResultDate) || curentDate.after(showResultDate)) {
                            binding.btnViewSol.visibility = View.VISIBLE
                            return true
                        } else binding.btnViewSol.visibility = View.GONE

                    }
                }


            }
        }
        return false

    }

    private fun hitService(testId: String?) {
        binding.isLoading = true
        MyClient(this).getObjectiveTestDetails(testId, object : ResponseObject {
            override fun OnCallback(content: Any?, error: String?) {
                content?.let {
                    val response = content as ObjectiveTestDetailsResponse
                    if (response.status == 200) {
                        if (response.success!! && response.result != null) {
                            testData = response.result!!
                            binding.testData = testData
                            testData.sections?.let {
                                for (section: TestSection in it) {
                                    if (section.questionCount > 0) {
                                        section.questions?.let { it1 -> answerList.addAll(it1) }
                                    }

                                }
                                accessResult()
                            }


                        }

                    } else (Utilities.openUnauthorizedDialog(this@TestResultActivity))
                } ?: kotlin.run { Utilities.makeToast(this@TestResultActivity, getString(R.string.server_error)) }
                binding.isLoading = false
            }

        })
    }

    private fun accessResult() {
        if (checkSolutionValidity())
            binding.btnViewSol.setOnClickListener {
                startActivity(
                        Intent(
                                this,
                                TestActivity::class.java
                        ).putExtra(PreferenceHandler.DATA, Gson().toJson(testData).toString()
                        ).putExtra("isConfigChange", false
                        ))
            }
        var totalTimeTaken = 0
        var correct = 0
        var incorrect = 0
        var unAttempt = 0
        var correctMarks = 0.0
        var inCorrectmarks = 0.0
        var totalMarks = 0.0
        for (answer in answerList) {
            totalMarks += answer.markingCreteria?.correctMarks?.toDouble() ?: 0.0

            answer.answerStatus?.let {
                totalTimeTaken += it.time_taken
                if (it.status == 1) {
                    Log.e(TAG, "accessResult: your answer ${answer.options?.option1?.answer} option ${it.option}  ,correct option ${answer.options?.correct_option}")
                    if (answer.type == 3 || answer.type == 4) {
                        val firstTagRemoved = (answer.options?.option1?.answer)?.removePrefix("<p>")
                        val secondTagRemoved = firstTagRemoved?.removeSuffix("</p>")?.trim()
                        if (secondTagRemoved.equals(it.option, true)) {
                            correctMarks += answer.markingCreteria?.correctMarks?.toDouble()!!
                            correct++
                        } else {
                            inCorrectmarks += answer.markingCreteria?.wrongMarks?.toDouble()?.absoluteValue!!
                            incorrect++
                        }

                    } else {
                        if (answer.options?.correct_option.equals(it.option)) {
                            correctMarks += answer.markingCreteria?.correctMarks?.toDouble()!!
                            correct++
                        } else {
                            inCorrectmarks += answer.markingCreteria?.wrongMarks?.toDouble()?.absoluteValue!!
                            incorrect++
                        }

                    }
                } else
                    unAttempt++

            }
        }
        binding.totalMarks = totalMarks
        binding.marksObtained = String.format("%.3f", correctMarks - inCorrectmarks).toDouble()
        binding.total = answerList.size.toString()
        binding.correct = correct
        binding.incorrect = incorrect
        binding.unattempt = unAttempt
        var markgett = String.format("%.3f", correctMarks - inCorrectmarks).toDouble()

        totalQuestions = testData.totalQuestion
        marksBody = "$markgett/$totalMarks";
        answerBody = "$correct/$totalQuestions"
        setUpResult(totalTimeTaken, correct + incorrect, marksBody, answerBody)
    }

    private fun setUpResult(totalTimeTaken: Int, attempted: Int, marksBody: String, answerBody: String) {

        binding.resultScore.apply {

            max = binding.totalMarks?.roundToInt()?.times(100) ?: 0
            Log.e(TAG, "setUpResult: $max  ${binding.totalMarks}")
            progress = 0
        }
        binding.resultPercent.apply {
            max = 100 * 100
            progress = 0
        }
        binding.resultTime.apply {

            max = if (binding.unattempt!!.equals(binding.total!!)) 0 else totalTimeTaken * 100
            progress = 0
        }
        //score
        var score = 0
        handler = Handler(Looper.myLooper()!!)
        secHandler = Handler(Looper.myLooper()!!)
        percHandler = Handler(Looper.myLooper()!!)
        var marks = binding.marksObtained?.roundToInt() ?: 0
        runnable = Runnable {
            setScore(score, marks, binding.resultScore, runnable, handler)
            score++
            Log.e(TAG, "setUpResult: $marks   $score")
        }
        handler.postDelayed(runnable, 150)

        //percent
        var per = 0
        val avgPer = (binding.marksObtained!!.toFloat() / binding.totalMarks?.toFloat()!!) * 100
        binding.per = stringFormat(avgPer)
        percHandler = Handler(Looper.myLooper()!!)
        prun = Runnable {
            setScore(per, avgPer.toInt(), binding.resultPercent, prun, percHandler)
            per++
        }
        percHandler.postDelayed(prun, 150)

        //time
        var sec = 0
        val avgSec = (totalTimeTaken.toFloat() / attempted)
        binding.speed = stringFormat(avgSec)
        percHandler = Handler(Looper.myLooper()!!)
        srun = Runnable {
            setScore(sec, avgSec.toInt(), binding.resultTime, srun, secHandler)
            sec++
        }
        secHandler.postDelayed(srun, 150)
        messageBody = "Hey! I am improving every day by taking tests on this amazing app. I scored $marksBody" + " in test ${testData.topic}" +"\n"+ "To download this app, click"
    }

    private fun stringFormat(float: Float): String {
        return String.format("%.1f", float)
    }

    private fun setScore(
            score: Int,
            correct: Int,
            progressBar: ProgressBar,
            runnable: Runnable,
            handler: Handler
    ) {
        if (score <= correct) {
            val animator = ObjectAnimator.ofInt(
                    progressBar,
                    "progress",
                    progressBar.progress,
                    score * 100
            )
            animator.duration = 150
            animator.interpolator = DecelerateInterpolator()
            animator.start()
            handler.postDelayed(runnable, 150)
        } else {
            handler.removeCallbacks(runnable)
        }
    }
}
