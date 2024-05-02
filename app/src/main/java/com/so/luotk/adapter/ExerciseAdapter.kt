package com.so.luotk.adapter


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.so.luotk.R
import com.so.luotk.databinding.ExerciseAdapterLayoutBinding
import com.so.luotk.models.AnswerStatus
import com.so.luotk.models.output.OptionModel
import com.so.luotk.models.output.TestQuestion


class ExerciseAdapter(
        val item: TypeClickListener, private val updateItemListener: UpdateItemListener
) :
        RecyclerView.Adapter<ExerciseAdapter.MyViewHolder>() {
    val list = mutableListOf<TestQuestion>()
    var showSol = false

    interface TypeClickListener {
        fun onClick(position: Int, type: Int, result: TestQuestion?)
    }

    interface UpdateItemListener {
        fun onClick(position: Int, result: TestQuestion)
    }

    class MyViewHolder(private val binding: ExerciseAdapterLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
        var mWebViw: WebView? = null
        init {
            this.mWebViw = binding.mWebView
            binding.mWebView.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    return true
                }
            })
            binding.mWebView.setLongClickable(false)
        }
        fun bind(
                result: TestQuestion,
                position: Int,
                item: TypeClickListener,
                size: Int,
                update: UpdateItemListener,
                showSol: Boolean,
                context: Context
        ) {
            binding.notClickableLayout.layoutParams = binding.scrollableQuestionLayout.layoutParams
            binding.edtFib.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                    binding.state = if (s?.length ?: 0 > 0) 1 else 0
                    Log.e("TAG", "afterTextChanged:1 ${binding.state}  ${binding.data?.quesStat}   $s")

                    if (!showSol) {
                        val ques = binding.data!!
                        if (binding.state == 1) {
                            if (binding.data?.quesStat == 3)
                                binding.data!!.quesStat = 2
                            else if (binding.data!!.quesStat == 2)
                                binding.data!!.quesStat = 2
                            else binding.data!!.quesStat = 1
                            binding.data!!.answerStatus =
                                    AnswerStatus(ques.id!!, 1, ques.answerStatus?.time_taken!!, s.toString(), 0)
                        } else {
                            if (binding.data?.quesStat == 2)
                                binding.data!!.quesStat = 3
                            else if (binding.data!!.quesStat == 3)
                                binding.data!!.quesStat = 3
                            else binding.data!!.quesStat = 0
                            binding.data!!.answerStatus =
                                    AnswerStatus(ques.id!!, 0, ques.answerStatus?.time_taken!!, " ")
                        }

                    }
                    toggleButton(if (binding.state == 1 && position == size - 1) context.getString(R.string.submit) else if (binding.state == 1) context.getString(R.string.next_btn)  else context.getString(R.string.skip) )
                    Log.e("TAG", "afterTextChanged:2 ${binding.state}  ${binding.data?.quesStat}   $s")


                }


            })
            binding.pos = position
            binding.data = result
            binding.click = item
            binding.update = update
            binding.sol = showSol
            if (binding.data?.type == 1 || binding.data?.type == 2) {
                binding.state = result.answerStatus?.let {
                    if (it.option.isEmpty())
                        -1
                    else
                        it.option.toInt()

                } ?: -1

            } else {
                val firstTagRemoved = (binding.data?.options?.option1?.answer)?.removePrefix("<p>")
                val secondTagRemoved = firstTagRemoved?.removeSuffix("</p>")?.trim()
                binding.answerText = secondTagRemoved
                if (binding.data?.answerStatus?.status == 1) {
                    binding.edtFib.setText(binding.data?.answerStatus?.option)
                } else binding.edtFib.setText("")
            }



            Log.e("TAG", "bind: ${binding.state}")
            if (showSol) {
                if (position == size - 1)
                    binding.button = context.getString(R.string.finish)
                else
                    binding.button = context.getString(R.string.next_btn)
            } else {
                if (position == size - 1)
                    binding.button = context.getString(R.string.submit)
                else {
                    binding.button =
                            if (binding.data?.answerStatus?.status == 0) context.getString(R.string.skip)  else context.getString(R.string.next_btn)
                }
            }


            binding.exerciseSolution.setOnClickListener {
                if (binding.button == context.getString(R.string.next_btn) ) {
                    item.onClick(position, 0, binding.data)
                } else if (binding.button == context.getString(R.string.submit) ) {
                    item.onClick(position, 3, binding.data)
                } else if (binding.button == context.getString(R.string.finish) ) {
                    item.onClick(position, 4, binding.data)
                } else
                    item.onClick(position, 1, binding.data)
            }
            binding.option1.setOnClickListener {
                optionClick(0, position == size - 1, binding.data?.options?.option1, context)
            }
            binding.option2.setOnClickListener {
                optionClick(1, position == size - 1, binding.data?.options?.option2, context)
            }
            binding.option3.setOnClickListener {
                optionClick(2, position == size - 1, binding.data?.options?.option3, context)
            }
            binding.option4.setOnClickListener {
                optionClick(3, position == size - 1, binding.data?.options?.option4, context)
            }
            binding.executePendingBindings()
        }

        private fun optionClick(i: Int, isLastItem: Boolean, optionNum: OptionModel?, context: Context) {
            val ques = binding.data!!
            Log.e("TAG", "optionClick:before ${binding.data?.answerStatus?.option} ${binding.data?.quesStat}")
            if (binding.state == i) {
                binding.state = -1
                binding.data!!.answerStatus = AnswerStatus(ques.id!!, 0, 0, "-1")
                if (binding.data?.quesStat == 2)
                    binding.data!!.quesStat = 3
                else binding.data!!.quesStat = 0
            } else {

                binding.state = i
                binding.data!!.answerStatus =
                        optionNum?.answerId?.let { AnswerStatus(ques.id!!, 1, ques.answerStatus?.time_taken!!, "${i}", it) }
                if (binding.data?.quesStat == 3)
                    binding.data!!.quesStat = 2
                else binding.data!!.quesStat = 1
            }
            Log.e("TAG", "optionClick:after ${binding.data?.answerStatus?.option} ${binding.data?.quesStat}")
            toggleButton(if (binding.state == i && isLastItem) context.getString(R.string.submit) else if (binding.state == i) context.getString(R.string.next_btn) else context.getString(R.string.skip))
        }

        private fun toggleButton(title: String) {
            binding.button = title
        }
    }

    fun showSolution(isShow: Boolean) {
        showSol = isShow
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
                ExerciseAdapterLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position], position, item, itemCount, updateItemListener, showSol, holder.itemView.getContext())
        list.get(position).question?.let { holder.mWebViw?.loadData(it, "text/html", "UTF-8") }
    }

    fun updateList(items: List<TestQuestion>, isShow: Boolean) {
        list.addAll(items)
        showSol = isShow
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, result: TestQuestion) {
        list[position] = result
        /*      notifyDataSetChanged()*/
    }

    override fun getItemCount() = list.size
    fun getListItem(position: Int) = list[position]
    fun getTotalList() = list

}