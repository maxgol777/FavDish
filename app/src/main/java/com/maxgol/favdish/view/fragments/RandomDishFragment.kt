package com.maxgol.favdish.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.FragmentRandomDishBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.model.entities.RandomDish
import com.maxgol.favdish.utils.Constants
import com.maxgol.favdish.utils.Constants.getFromHtml
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory
import com.maxgol.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {
    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private val mFavDishViewModel by viewModels<FavDishViewModel> {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private var mBinding: FragmentRandomDishBinding? = null
    var addedToFavorites = false
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_custom_progress)
            show()
        }
    }

    private fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromApi()
        randomDishViewModelObserver()
        mBinding!!.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
    }

    private fun randomDishViewModelObserver() = with(mRandomDishViewModel) {
        randomDishResponse.observe(viewLifecycleOwner) { randomDishResponse ->
            stopRefreshing()
            addedToFavorites = false
            if (randomDishResponse == null) return@observe
            Log.i("Random Dish Response", "${randomDishResponse.recipes[0]}")
            setRandomDishResponseInUi(randomDishResponse.recipes[0])
        }

        randomDishLoadingError.observe(viewLifecycleOwner) { dataError ->
            stopRefreshing()
            addedToFavorites = false
            if (dataError == null) return@observe
            Log.e("Random Dish API Error", "$dataError")
        }

        loadRandomDish.observe(viewLifecycleOwner) { loadRandomDish ->
            if (loadRandomDish == null) return@observe
            if (loadRandomDish == true && !mBinding!!.srlRandomDish.isRefreshing) {
                showCustomProgressDialog()
            } else {
                hideProgressDialog()
            }
            Log.i("Random Dish Loading", "$loadRandomDish")
        }
    }

    private fun stopRefreshing() {
        if (mBinding!!.srlRandomDish.isRefreshing) {
            mBinding!!.srlRandomDish.isRefreshing = false
        }
    }

    private fun setRandomDishResponseInUi(recipe: RandomDish.Recipe) {
        with(mBinding!!) {
            Glide.with(requireActivity())
                .load(recipe.image)
                .centerCrop()
                .into(ivDishImage)

            val dishType = if (recipe.dishTypes.isNotEmpty()) recipe.dishTypes[0] else ""
            val ingredients = recipe.extendedIngredients
                .map { ingredient -> ingredient.original + ", \n" }
                .fold("") { total, item -> total + item }.dropLast(3)

            tvTitle.text = recipe.title
            tvType.text = dishType
            tvCategory.text = "Other"
            tvIngredients.text = ingredients
            tvCookingDirection.text = recipe.instructions.getFromHtml()
            tvCookingTime.text = resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )
            ivFavoriteDish.setOnClickListener {
                if (addedToFavorites) {
                    showToast(R.string.msg_already_added_to_favorites)
                } else {
                    val favDishDetails = FavDish(
                        image = recipe.image,
                        imageSource = Constants.DISH_IMAGE_SOURCE_ONLINE,
                        title = recipe.title,
                        type = dishType,
                        category = "Other",
                        ingredients = ingredients,
                        cookingTime = recipe.readyInMinutes.toString(),
                        directionToCook = recipe.instructions,
                        favoriteDish = true,
                    )
                    favoriteDrawableSelected(addedToFavorites)
                    mFavDishViewModel.insert(favDishDetails)
                    addedToFavorites = true
                    favoriteDrawableSelected(addedToFavorites)
                    showToast(R.string.msg_added_to_favorites)
                }
            }
            favoriteDrawableSelected(addedToFavorites)
        }
    }

    private fun favoriteDrawableSelected(selected: Boolean) {
        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (selected) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
            )
        )
    }

    private fun showToast(messageId: Int) {
        Toast.makeText(
            requireContext(),
            resources.getString(messageId),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}