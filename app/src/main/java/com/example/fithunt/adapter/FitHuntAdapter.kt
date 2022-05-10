package com.example.fithunt.adapter

import android.graphics.Typeface
import android.view.*
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fithunt.FitHuntroomdatabase.FitHuntEntity
import com.example.fithunt.R
import com.example.fithunt.Util.Stopwatch
import com.example.fithunt.databinding.TrackitemBinding
import java.text.SimpleDateFormat
import java.util.*


//inrecyclebin tells if recyclerview is called in RECYCLEBIN FRAGMENT OR IN ELSE FRAGMENT
class FitHuntAdapter() : RecyclerView.Adapter<FitHuntAdapter.ViewHolder>(){

    inner class ViewHolder(val binding:TrackitemBinding)
        :RecyclerView.ViewHolder(binding.root){

    }

    companion object DiffCallback: DiffUtil.ItemCallback<FitHuntEntity>(){
        override fun areItemsTheSame(oldItem:FitHuntEntity,newItem:FitHuntEntity):Boolean{
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem:FitHuntEntity,newItem:FitHuntEntity):Boolean{
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ViewHolder{
        val binding = TrackitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        val track = differ.currentList[position]
        holder.binding.apply{
            if(track.bitmap!=null)
            Glide.with(holder.itemView).load(track.bitmap).into(ivRunImage)
            else
                Glide.with(holder.itemView).load(R.drawable.indorrexcercises).into(ivRunImage)

            val avgspeed="${track.avgspeedinKMH}km/h"
            val dateformat = SimpleDateFormat("dd.MMM.yy", Locale.getDefault())
            tvDate.text = track.timestamp
            tvAvgSpeed.text = avgspeed
            tvCalories.text ="${track.caloriesburnt}kcal"
            tvDistance.text = ("${track.distanceinMeters/1000f}km").toString()
            tvTime.text = Stopwatch.getFormattedStopWatchTime(track.timeinmills)
            typeofexcercise.text = track.excercisetype

        }


    }

    override fun getItemCount():Int{
        return differ.currentList.size
    }
}
