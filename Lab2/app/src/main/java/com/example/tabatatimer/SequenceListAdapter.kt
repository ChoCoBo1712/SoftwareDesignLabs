package com.example.tabatatimer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.room.Sequence

class SequenceListAdapter() : RecyclerView.Adapter<SequenceListAdapter.ViewHolder>() {

    var sequences: List<Sequence>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onItemClick: ((Sequence, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_sequence, parent, false)
        val viewHolder = ViewHolder(view)

        if (sequences != null) {
            viewHolder.itemView.setOnClickListener {
                onItemClick?.invoke(sequences!![viewHolder.adapterPosition], viewHolder.itemView)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (sequences != null)
        {
            val item = sequences!![position]
            with(holder) {
                title.text = item.title
                preparation.text = item.preparation.toString()
                workout.text = item.workout.toString()
                rest.text = item.rest.toString()
                cycles.text = item.cycles.toString()
                when(item.colour) {
                    0 -> {
                        itemView.setBackgroundColor(Color.BLUE)
                    }
                    1 -> {
                        itemView.setBackgroundColor(Color.RED)
                    }
                    2 -> {
                        itemView.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (sequences != null)
        {
            return sequences!!.size
        }
        return 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.adapter_title)
        val preparation: TextView = view.findViewById(R.id.adapter_preparation)
        val workout: TextView = view.findViewById(R.id.adapter_workout)
        val rest: TextView = view.findViewById(R.id.adapter_rest)
        val cycles: TextView = view.findViewById(R.id.adapter_cycles)
    }
}