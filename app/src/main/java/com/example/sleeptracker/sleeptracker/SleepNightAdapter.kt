package com.example.sleeptracker.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sleeptracker.R
import com.example.sleeptracker.database.SleepNight
import com.example.sleeptracker.databinding.ItemListSleepNightBinding
import convertDurationToFormatted
import convertNumericQualityToString

class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallBacks()) {
    /* ViewHolder that holds a single [TextView],A ViewHolder holds a view for the recycle view as well as
    * providing additional information such as where on the screen it was last drawn while scrolling  */
    class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    // find views
    class ViewHolder private constructor(val binding: ItemListSleepNightBinding): RecyclerView.ViewHolder(binding.root){
        val res = itemView.context.resources

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding= ItemListSleepNightBinding.inflate(layoutInflater,parent,false)


                return ViewHolder(binding)
            }
        }

        fun bind(item: SleepNight) {
            binding.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_launcher_background
            })
        }
    }
   /** var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       // TODO:Get the LayoutInflater from parent.context and inflate R.layout.text_item_view
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    class SleepNightDiffCallBacks : DiffUtil.ItemCallback<SleepNight>(){
        override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            return oldItem.nightId == newItem.nightId
        }

        override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            return oldItem == newItem
        }

    }




}