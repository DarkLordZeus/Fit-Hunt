package com.example.fithunt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fithunt.databinding.WorkoutTrainingItemviewBinding
import com.example.fithunt.fragments.Workout_training_fragmentDirections

class TrainingExcercisesAdapter(val arrayList: ArrayList<Training_details>):RecyclerView.Adapter<TrainingExcercisesAdapter.ViewHolder>() {
    inner class ViewHolder(val binding:WorkoutTrainingItemviewBinding)
        :RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): TrainingExcercisesAdapter.ViewHolder{
        val binding = WorkoutTrainingItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        val training = arrayList[position]
        holder.binding.apply{
            Glide.with(holder.itemView).load(training.imageview).centerCrop().into(trainingpic)
            textViewtrainingname.text=training.text
            holder.itemView.setOnClickListener {
                it.findNavController().navigate(Workout_training_fragmentDirections.actionWorkoutTrainingFragmentToExcerciseWebView(training.link.toString()))
            }
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    private var onItemClickListener:((Training_details)->Unit)?=null

    fun setOnItemClickListener(listener:(Training_details)->Unit){
        onItemClickListener = listener
    }
}