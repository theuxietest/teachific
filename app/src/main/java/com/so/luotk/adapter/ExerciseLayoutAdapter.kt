package com.so.luotk.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.so.luotk.R
import com.so.luotk.databinding.ExerciseGridAdapterLayoutBinding
import com.so.luotk.databinding.ExerciseListAdapterLayoutBinding
import com.so.luotk.models.output.TestQuestion

class ExerciseLayoutAdapter(
        private val type: Int,
        private val clickListener: QuestionClickListener
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }


    interface QuestionClickListener {
        fun onclick(position: Int)
    }

    private val list = mutableListOf<TestQuestion>()
    private val indexList = mutableListOf<Int>()
    private val sectionNames = mutableListOf<String>()
    val positionList = mutableListOf<Int>()
    var context: Context? = null

    class GridViewHolder(private val binding: ExerciseGridAdapterLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(
                data: TestQuestion,
                position: Int,
                clickListener: QuestionClickListener,
                list: List<Int>,
                indexList: MutableList<Int>,
                sectionNames: MutableList<String>,
                context: Context?
        ) {
            var i = 0
            for (index in indexList) {
                if (index == position) {
                    binding.sectionName = sectionNames[i]
                    break
                }
                if (index > position) {
                    binding.sectionName = sectionNames[i - 1]
                    break
                }
                if (index < position && i == (indexList.size - 1)) {
                    binding.sectionName = sectionNames[i]
                    break
                }
                i++
            }
            binding.data = data
            binding.pos = position
            binding.click = clickListener
            binding.list = list
            binding.drawable = ContextCompat.getDrawable(context!!, setBackGroundDrawable(data))
            binding.executePendingBindings()
        }

        fun setBackGroundDrawable(data: TestQuestion): Int {
            when (data.quesStat) {

                0 -> {
                    return R.drawable.gray_border_bg
                }

                1 -> {
                    return R.drawable.right_marks
                }
                2 -> {
                    return R.drawable.ic_bookmark_and_answered
                }
                3 -> {
                    return R.drawable.yellow_border_back
                }

            }
            return R.drawable.edit_border
        }
    }


    class ListViewHolder(private val binding: ExerciseListAdapterLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(
                data: TestQuestion, position: Int, clickListener: QuestionClickListener,
                list: List<Int>,
                indexList: MutableList<Int>,
                sectionNames: MutableList<String>,
                context: Context?
        ) {
            Log.e("TAG", "bind: ${indexList.contains(position)}, $position")

            if (indexList.contains(position))
                binding.sectionName = sectionNames[indexList.indexOf(position)]
            else
                binding.sectionName = ""

            binding.data = data
            binding.click = clickListener
            binding.pos = position
            binding.list = list
            binding.drawable = ContextCompat.getDrawable(context!!, setBackGroundDrawable(data))
            binding.executePendingBindings()
        }

        fun setBackGroundDrawable(data: TestQuestion): Int {
            when (data.quesStat) {

                0 -> {
                    return R.drawable.gray_border_bg
                }

                1 -> {
                    return R.drawable.right_marks
                }
                2 -> {
                    return R.drawable.ic_bookmark_and_answered
                }
                3 -> {
                    return R.drawable.yellow_border_back
                }

            }
            return R.drawable.edit_border
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (type == 1)
            VIEW_TYPE_ONE
        else
            VIEW_TYPE_TWO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == VIEW_TYPE_ONE)
            return ListViewHolder(
                    ExerciseListAdapterLayoutBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
        else
            return GridViewHolder(
                    ExerciseGridAdapterLayoutBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
    }

    fun updateList(items: List<TestQuestion>, firstIndexList: List<Int>, sectionNames: List<String>) {
        list.addAll(items)
        indexList.addAll(firstIndexList)
        Log.e("TAG", "updateList: $firstIndexList")
        this.sectionNames.addAll(sectionNames)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListViewHolder)
            holder.bind(list[position], position, clickListener, positionList, indexList, sectionNames, context)
        else
            (holder as GridViewHolder).bind(list[position], position, clickListener, positionList, indexList, sectionNames, context)
    }

    override fun getItemCount() = list.size
    fun updatePositionList(pos: Int, data: TestQuestion) {
        list[pos] = data
        notifyDataSetChanged()

    }

}