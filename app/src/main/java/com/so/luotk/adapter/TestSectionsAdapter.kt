package com.so.luotk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.so.luotk.databinding.ItemSectionListBinding
import com.so.luotk.models.output.TestSection

class TestSectionsAdapter(var listener: StartClickListener) : RecyclerView.Adapter<TestSectionsAdapter.ViewHolder>() {
    var sectionList = mutableListOf<TestSection>()


    class ViewHolder(var binding: ItemSectionListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: TestSection, listener: StartClickListener, position: Int) {
            binding.sectionData = section
            binding.pos = position
            binding.showInstructions = false
            binding.btnInst.setOnClickListener(View.OnClickListener {
                listener.onClick(position)
                binding.showInstructions = !binding.showInstructions
                binding.rotate = 180
                binding.instText.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null)
            })


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSectionListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(
                sectionList.get(position), listener, position
        )


    }

    fun updateList(sectionList: List<TestSection>) {
        this.sectionList.addAll(sectionList)
        notifyDataSetChanged()
    }

    interface StartClickListener {
        fun onClick(position: Int)
    }


    override fun getItemCount() =
            sectionList.size

}