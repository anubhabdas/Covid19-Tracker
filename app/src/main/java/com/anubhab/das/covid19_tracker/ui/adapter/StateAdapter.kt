package com.anubhab.das.covid19_tracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anubhab.das.covid19_tracker.R
import com.anubhab.das.covid19_tracker.model.StatewiseItem
import com.anubhab.das.covid19_tracker.utils.SpannableDelta

class StateAdapter(val list: MutableList<StatewiseItem>) :
    RecyclerView.Adapter<StateWiseDataHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateWiseDataHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false)
        return StateWiseDataHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StateWiseDataHolder, position: Int) {
        val currentItem = list[position];
        holder.confirmed.text =
            SpannableDelta(
                "${currentItem.confirmed} \n ↑${currentItem.deltaconfirmed ?: 0}",
                "#FF0000",
                currentItem.confirmed?.length ?: 0
            )
        holder.active.text =
            SpannableDelta(
                "${currentItem.active} \n ↑${currentItem.active ?: 0}",
                "#f5c311",
                currentItem.active?.length ?: 0
            )
        holder.recovered.text =
            SpannableDelta(
                "${currentItem.recovered} \n ↑${currentItem.deltarecovered ?: 0}",
                "#76e846",
                currentItem.recovered?.length ?: 0
            )
        holder.death.text = SpannableDelta(
            "${currentItem.deaths} \n ↑${currentItem.deltadeaths ?: 0}",
            "#cacccb",
            currentItem.deaths?.length ?: 0
        )
        holder.state.text = currentItem.state
    }

}

class StateWiseDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val confirmed: TextView = itemView.findViewById(R.id.confirmedTv)
    val active: TextView = itemView.findViewById(R.id.activeTv)
    val recovered: TextView = itemView.findViewById(R.id.recoveredTv)
    val death: TextView = itemView.findViewById(R.id.deceasedTv)
    val state: TextView = itemView.findViewById(R.id.stateTv)
}
