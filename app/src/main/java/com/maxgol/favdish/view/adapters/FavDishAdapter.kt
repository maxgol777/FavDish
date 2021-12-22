package com.maxgol.favdish.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxgol.favdish.databinding.ItemDishLayoutBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.view.fragments.AllDishesFragment

class FavDishAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false
        )
        return ViewHolder((binding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        holder.tvTitle.text = dish.title
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.itemView.setOnClickListener {
            if (fragment is AllDishesFragment) {
                fragment.dishDetails()
            }
        }
    }

    override fun getItemCount(): Int = dishes.size

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
    }
}