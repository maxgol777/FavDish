package com.maxgol.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.maxgol.favdish.R
import com.maxgol.favdish.databinding.FragmentDishDetailsBinding
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    private var mBinding: FragmentDishDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        Log.i("Dish Title", args.dishDetails.title)
        Log.i("Dish Type", args.dishDetails.type)
        try {
            Glide.with(requireActivity())
                .load(args.dishDetails.image)
                .centerCrop()
                .into(mBinding!!.ivDishImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        with(mBinding!!) {
            tvTitle.text = args.dishDetails.title
            tvType.text = args.dishDetails.type.capitalize(Locale.ROOT)
            tvCategory.text = args.dishDetails.category
            tvIngredients.text = args.dishDetails.ingredients
            tvCookingDirection.text = args.dishDetails.directionToCook
            tvCookingTime.text = resources.getString(
                R.string.lbl_estimate_cooking_time,
                args.dishDetails.cookingTime
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}