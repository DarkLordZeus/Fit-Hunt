package com.example.fithunt.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fithunt.FitHuntroomdatabase.FitHuntRoomViewmodel
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.adapter.FitHuntAdapter
import com.example.fithunt.databinding.FragmentSettingBinding
import com.example.fithunt.databinding.FragmentStatisticsBinding
import com.example.fithunt.dialog.Dialogsort
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding?=null
    private val binding get()=_binding!!
    lateinit var trackadapter :FitHuntAdapter
    private val roomViewmodel: FitHuntRoomViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentStatisticsBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.VISIBLE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.VISIBLE
        val sharedPreferences=context?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        utilvalues.listforadapter.value=sharedPreferences?.getInt("typeofsort",2)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setuprecyclerview()
        utilvalues.listforadapter.observe(viewLifecycleOwner, Observer {
            when(it)
            {
                1->roomViewmodel.readallTracksSortedbySpeed.observe(viewLifecycleOwner, Observer {
                    trackadapter.differ.submitList(it)
                })
                2->roomViewmodel.readallTracksSortedbyDate.observe(viewLifecycleOwner, Observer {
                    trackadapter.differ.submitList(it)
                })
                3->roomViewmodel.readallTracksSortedbyDistance.observe(viewLifecycleOwner, Observer {
                    trackadapter.differ.submitList(it)
                })
                4->roomViewmodel.readallTracksSortedbyDuration.observe(viewLifecycleOwner, Observer {
                    trackadapter.differ.submitList(it)
                })
                5->roomViewmodel.readallTracksSortedbyCalories.observe(viewLifecycleOwner, Observer {
                    trackadapter.differ.submitList(it)
                })
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.maintoolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        return when(item.itemId){
            R.id.sort -> {
                Dialogsort(requireContext()).show()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun setuprecyclerview() {
        trackadapter= FitHuntAdapter()
        binding.rvRuns.adapter=trackadapter
        binding.rvRuns.layoutManager= LinearLayoutManager(requireContext())
        binding.rvRuns.addItemDecoration(
            DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}
