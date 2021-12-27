package com.maxgol.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.maxgol.favdish.databinding.ItemCustomListBinding
import com.maxgol.favdish.view.activities.AddUpdateDishActivity
import com.maxgol.favdish.view.fragments.AllDishesFragment

class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment? = null,
    private val listItems: List<String>,
    private val selection: String
) : RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {


    class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvText = view.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCustomListBinding.inflate(LayoutInflater.from(activity), null, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item
        holder.itemView.setOnClickListener {
            if (activity is AddUpdateDishActivity) {
                activity.selectedListItem(item, selection)
            }
            if (fragment is AllDishesFragment) {
                fragment.filterSelection(item)
            }
        }
    }

    override fun getItemCount(): Int = listItems.size
}