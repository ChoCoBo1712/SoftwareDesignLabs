package com.example.tabatatimer.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.*
import com.example.tabatatimer.room.Sequence
import kotlinx.coroutines.launch
import java.util.*

class SequenceListFragment : Fragment() {

    private lateinit var sequenceListAdapter: SequenceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = PreferenceHelper.setTextSizeFragment(requireContext(), inflater)
                .inflate(R.layout.fragment_sequence_list, container, false)
        sequenceListAdapter = SequenceListAdapter()

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = sequenceListAdapter
            }
            view.addItemDecoration(DividerItemDecoration(view.context, 1))
        }

        SequenceRepository.getInstance(requireContext()).getAll().observe(viewLifecycleOwner, { newValue ->
            sequenceListAdapter.sequences = newValue
        })

        sequenceListAdapter.onItemClick = { sequence: Sequence, itemView: View ->
            val popup = PopupMenu(requireContext(), itemView)
            val menuInflater: MenuInflater = popup.menuInflater
            menuInflater.inflate(R.menu.item_popup, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_start -> {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                TimerFragment.newInstance(sequence.id)
                            )
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.item_edit -> {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, SequenceDetailFragment.newInstance(sequence.id))
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.item_delete -> {
                        lifecycleScope.launch {
                            SequenceRepository.getInstance(requireContext()).delete(sequence)
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        return view
    }

    companion object {

        fun newInstance(): SequenceListFragment {
            return SequenceListFragment()
        }
    }
}