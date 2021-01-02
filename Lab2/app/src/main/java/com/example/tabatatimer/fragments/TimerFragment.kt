package com.example.tabatatimer.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.tabatatimer.Constants.ARROW
import com.example.tabatatimer.Constants.COUNTER
import com.example.tabatatimer.Constants.CYCLE
import com.example.tabatatimer.Constants.CYCLES
import com.example.tabatatimer.Constants.FINISHED
import com.example.tabatatimer.Constants.ID
import com.example.tabatatimer.Constants.IMAGE
import com.example.tabatatimer.Constants.LEFT
import com.example.tabatatimer.Constants.MESSAGE
import com.example.tabatatimer.Constants.NEXT
import com.example.tabatatimer.Constants.PHASE
import com.example.tabatatimer.Constants.PLAY
import com.example.tabatatimer.Constants.PLAY_PAUSE
import com.example.tabatatimer.Constants.PREPARATION
import com.example.tabatatimer.Constants.PREV
import com.example.tabatatimer.Constants.REST
import com.example.tabatatimer.Constants.TICK
import com.example.tabatatimer.Constants.WORKOUT
import com.example.tabatatimer.PreferenceHelper
import com.example.tabatatimer.R
import com.example.tabatatimer.SequenceRepository
import com.example.tabatatimer.TimerService
import com.example.tabatatimer.viewmodels.TimerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by activityViewModels()
    private lateinit var timer: CountDownTimer
    private lateinit var navBar: BottomNavigationView
    private lateinit var actionBar: ActionMenuItemView
    private lateinit var cycles: TextView
    private lateinit var play: ImageButton
    private lateinit var phase: TextView
    private lateinit var countdown: TextView
    lateinit var receiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  PreferenceHelper.setTextSizeFragment(requireContext(), inflater)
                .inflate(R.layout.fragment_timer, container, false)
        val id = requireArguments().getInt(ID, 0)

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE
        actionBar = requireActivity().findViewById(R.id.action_add)
        actionBar.visibility = View.GONE

        cycles = view.findViewById(R.id.timer_cycles)
        val title: TextView = view.findViewById(R.id.timer_title)
        play = view.findViewById(R.id.timer_play)
        val next: ImageButton = view.findViewById(R.id.timer_next)
        val prev: ImageButton = view.findViewById(R.id.timer_prev)
        phase = view.findViewById(R.id.timer_phase)
        countdown = view.findViewById(R.id.timer_countdown)

        play.setOnClickListener(::onPlayClick)
        next.setOnClickListener(::onNextClick)
        prev.setOnClickListener(::onPrevClick)

        lifecycleScope.launch {
            val sequence = SequenceRepository.getInstance(requireContext()).get(id)

            if (sequence != null) {
                viewModel.title = sequence.title
                viewModel.preparation = sequence.preparation
                viewModel.workout = sequence.workout
                viewModel.rest = sequence.rest
                viewModel.cycles = sequence.cycles
                viewModel.colour = sequence.colour
            }
            else {
                requireActivity().supportFragmentManager.popBackStack()
            }

            cycles.text = resources.getString(
                R.string.cycles_count,
                1,
                viewModel.cycles
            )
            title.text = viewModel.title
            countdown.text = viewModel.preparation.toString()
            when(viewModel.colour) {
                0 -> {
                    view.setBackgroundColor(Color.BLUE)
                }
                1 -> {
                    view.setBackgroundColor(Color.RED)
                }
                2 -> {
                    view.setBackgroundColor(Color.GREEN)
                }
            }

            val filter = IntentFilter()
            filter.addAction(MESSAGE)
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent != null) {
                        val message = intent.getStringExtra(MESSAGE)
                        when (message) {
                            TICK -> {
                                countdown.text = intent.getLongExtra(LEFT, 1).toString()
                            }
                            PHASE -> {
                                phase.setText(intent.getIntExtra(PHASE, 0))
                            }
                            CYCLES -> {
                                cycles.text = resources.getString(
                                    R.string.cycles_count,
                                        intent.getIntExtra(CYCLE, 1),
                                        viewModel.cycles
                                )
                                phase.setText(R.string.preparation)
                            }
                            FINISHED -> {
                                phase.setText(R.string.finished)
                                play.setImageResource(android.R.drawable.ic_media_play)
                                countdown.text = "0"
                            }
                            ARROW -> {
                                countdown.text = intent.getIntExtra(COUNTER, 1).toString()
                            }
                            PLAY_PAUSE -> {
                                when {
                                    intent.getStringExtra(IMAGE) == PLAY -> {
                                        play.setImageResource(android.R.drawable.ic_media_play)
                                    }
                                    else -> {
                                        play.setImageResource(android.R.drawable.ic_media_pause)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            requireActivity().registerReceiver(receiver, filter)

            Intent(requireContext(), TimerService::class.java).also {
                val bundle = Bundle()
                bundle.putInt(PREPARATION, viewModel.preparation)
                bundle.putInt(WORKOUT, viewModel.workout)
                bundle.putInt(REST, viewModel.rest)
                bundle.putInt(CYCLES, viewModel.cycles)
                it.putExtras(bundle)
                requireContext().startService(it)
            }
        }

        return view
    }

    private fun onNextClick(v: View) {
        Intent(requireContext(), TimerService::class.java).also {
            val bundle = Bundle()
            bundle.putString(MESSAGE, NEXT)
            it.putExtras(bundle)
            requireContext().startService(it)
        }
    }

    private fun onPrevClick(v: View) {
        Intent(requireContext(), TimerService::class.java).also {
            val bundle = Bundle()
            bundle.putString(MESSAGE, PREV)
            it.putExtras(bundle)
            requireContext().startService(it)
        }
    }

    private fun onPlayClick(v: View) {
        if (phase.text == resources.getString(R.string.finished)) {
            phase.setText(R.string.preparation)
            cycles.text = resources.getString(
                R.string.cycles_count,
                    1,
                    viewModel.cycles
            )
        }
        Intent(requireContext(), TimerService::class.java).also {
            val bundle = Bundle()
            bundle.putString(MESSAGE, PLAY_PAUSE)
            it.putExtras(bundle)
            requireContext().startService(it)
        }
    }

    override fun onDetach() {
        navBar.visibility = View.VISIBLE
        actionBar.visibility = View.VISIBLE

        Intent(requireContext(), TimerService::class.java).also {
            if (play.drawable.constantState == ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_media_pause)!!.constantState) {
                val bundle = Bundle()
                bundle.putString(MESSAGE, PLAY_PAUSE)
                it.putExtras(bundle)
                requireContext().startService(it)
            }
            requireContext().stopService(it)
        }

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)

        super.onDetach()
    }

    companion object {

        fun newInstance(id: Int): TimerFragment {
            val fragment = TimerFragment()
            val args = Bundle()
            args.putInt(ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}