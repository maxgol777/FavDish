package com.maxgol.favdish.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxgol.favdish.R
import com.maxgol.favdish.databinding.ItemDishLayoutBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.utils.Constants
import com.maxgol.favdish.view.activities.AddUpdateDishActivity
import com.maxgol.favdish.view.fragments.AllDishesFragment
import com.maxgol.favdish.view.fragments.DeleteDishAction
import com.maxgol.favdish.view.fragments.DishDetails
import com.maxgol.favdish.view.fragments.FavoriteDishesFragment

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
            if (fragment is DishDetails) {
                fragment.dishDetails(dish)
            }
        }

        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_dish -> {
                        val intent =
                            Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                        intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                        fragment.requireActivity().startActivity(intent)
                    }
                    R.id.action_delete_dish -> {
                        (fragment as? DeleteDishAction)?.deleteDish(dish)
                    }
                }
                true
            }
            popup.show()
        }
        holder.ibMore.visibility = when (fragment) {
            is AllDishesFragment -> View.VISIBLE
            is FavoriteDishesFragment -> View.GONE
            else -> View.GONE
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
        val ibMore = view.ibMore
    }
}