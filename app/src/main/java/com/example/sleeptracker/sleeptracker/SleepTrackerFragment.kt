package com.example.sleeptracker.sleeptracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.sleeptracker.R
import com.example.sleeptracker.database.SleepDatabase
import com.example.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {
    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
   //TODO: add an observer for navigateToSleepQuality.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false)
        //TODO: get a reference to the application context.
        val application = requireNotNull(this.activity).application
        //TODO: Define a dataSource
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        //TODO: Create an instance of the viewModelFactory
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        //TODO: Get a reference to the SleepTrackerViewModel
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory).get(SleepTrackerViewModel::class.java)
        //TODO: Adding grid layout manager
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager
        //TODO:navigate and pass along the ID of the current night, and then call doneNavigating():
        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer {
                night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })
        //TODO:
        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })
        // use binding to associate adapter with the RecyclerView:

        //TODO: Have the listener display the nightId in a toast message when the user clicks the item in the grid.
        val adapter = SleepNightAdapter(SleepNightAdapter.SleepNightListener { nightId ->
            Toast.makeText(context, "${nightId}", Toast.LENGTH_LONG).show()
        })
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.nights
            .observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //TODO: setting current activity as lifecycle owner
        binding.setLifecycleOwner(this)
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        return binding.root
    }



}
