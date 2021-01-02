package com.example.tabatatimer.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.tabatatimer.Constants.ID
import com.example.tabatatimer.PreferenceHelper
import com.example.tabatatimer.R
import com.example.tabatatimer.SequenceRepository
import com.example.tabatatimer.room.Sequence
import com.example.tabatatimer.viewmodels.SequenceDetailViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SequenceDetailFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: SequenceDetailViewModel by activityViewModels()
    private lateinit var navBar: BottomNavigationView
    private lateinit var actionBar: ActionMenuItemView
    private lateinit var title: EditText
    private lateinit var preparation: EditText
    private lateinit var workout: EditText
    private lateinit var rest: EditText
    private lateinit var cycles: EditText

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = PreferenceHelper.setTextSizeFragment(requireContext(), inflater)
                .inflate(R.layout.fragment_sequence_detail, container, false)
        val id = requireArguments().getInt(ID, 0)

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE
        actionBar = requireActivity().findViewById(R.id.action_add)
        actionBar.visibility = View.GONE

        val button: Button = view.findViewById(R.id.sequence_submit_button)
        button.setOnClickListener(this::onSubmitClick)

        val colour: Spinner = view.findViewById(R.id.sequence_colour)
        val array = resources.getStringArray(R.array.colours)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colour.adapter = adapter

        title = view.findViewById(R.id.sequence_title_text)
        preparation = view.findViewById(R.id.sequence_preparation_text)
        workout = view.findViewById(R.id.sequence_workout_text)
        rest = view.findViewById(R.id.sequence_rest_text)
        cycles = view.findViewById(R.id.sequence_cycles_text)

        lifecycleScope.launch {
            val sequence = SequenceRepository.getInstance(requireContext()).get(id)
            if (sequence != null) {
                viewModel.id = sequence.id
                viewModel.title = sequence.title
                viewModel.preparation = sequence.preparation
                viewModel.workout = sequence.workout
                viewModel.rest = sequence.rest
                viewModel.cycles = sequence.cycles
                viewModel.colour = sequence.colour
            }
            else {
                viewModel.id = id
                viewModel.title = ""
                viewModel.preparation = 1
                viewModel.workout = 1
                viewModel.rest = 1
                viewModel.cycles = 1
                viewModel.colour = 0
            }

            title.setText(viewModel.title)
            preparation.setText(viewModel.preparation.toString())
            workout.setText(viewModel.workout.toString())
            rest.setText(viewModel.rest.toString())
            cycles.setText(viewModel.cycles.toString())
            colour.setSelection(viewModel.colour)
        }

        title.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.title = s.toString()
            }
        })
        preparation.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString() == "" -> {
                        viewModel.preparation = 0
                    }
                    s.toString().toInt() > 3600 -> {
                        preparation.setText(R.string.time_limit_prep)
                    }
                    else -> {
                        viewModel.preparation = s.toString().toInt()
                    }
                }
            }
        })
        workout.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString() == "" -> {
                        viewModel.workout = 1
                    }
                    s.toString().toInt() > 3600 -> {
                        workout.setText(R.string.time_limit)
                    }
                    else -> {
                        viewModel.workout = s.toString().toInt()
                    }
                }
            }
        })
        rest.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString() == "" -> {
                        viewModel.rest = 0
                    }
                    s.toString().toInt() > 3600 -> {
                        rest.setText(R.string.time_limit)
                    }
                    else -> {
                        viewModel.rest = s.toString().toInt()
                    }
                }
            }
        })
        cycles.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString() == "" -> {
                        viewModel.cycles = 1
                    }
                    s.toString().toInt() > 99 -> {
                        cycles.setText(R.string.cycles_max)
                    }
                    else -> {
                        viewModel.cycles = s.toString().toInt()
                    }
                }
            }
        })
        colour.onItemSelectedListener = this

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        viewModel.colour = pos
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDetach() {
        navBar.visibility = View.VISIBLE
        actionBar.visibility = View.VISIBLE

        super.onDetach()
    }

    private fun onSubmitClick(v: View)
    {
        if (title.text.toString() == "") {
            Toast.makeText(requireContext(), resources.getString(R.string.enter_title), Toast.LENGTH_SHORT).show()
            return
        }
        val sequence = Sequence(
                id = viewModel.id,
                title = viewModel.title,
                preparation = viewModel.preparation,
                workout = viewModel.workout,
                rest = viewModel.rest,
                cycles = viewModel.cycles,
                colour = viewModel.colour
        )
        lifecycleScope.launch {
            SequenceRepository.getInstance(requireContext()).insert(sequence)
        }
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {

        fun newInstance(id: Int): SequenceDetailFragment {
            val fragment = SequenceDetailFragment()
            val args = Bundle()
            args.putInt(ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}