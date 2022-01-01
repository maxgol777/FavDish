package com.maxgol.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.FragmentDishDetailsBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.utils.Constants
import com.maxgol.favdish.utils.Constants.getFromHtml
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    private var mBinding: FragmentDishDetailsBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    private var mFavDishDetails: FavDish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"
                mFavDishDetails?.let {
                    Log.v("PPP", "onOptionsItemSelected")
                    val image =
                        if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) it.image else ""
                    val cookingInstructions = it.directionToCook.getFromHtml()

                    extraText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

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
        mFavDishDetails = args.dishDetails
        try {
            Glide.with(requireActivity())
                .load(args.dishDetails.image)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("TAG", "ERROR loading image", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource ?: return false
                        Palette.from(resource.toBitmap()).generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                        }
                        return false
                    }
                })
                .into(mBinding!!.ivDishImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        with(mBinding!!) {
            tvTitle.text = args.dishDetails.title
            tvType.text = args.dishDetails.type.capitalize(Locale.ROOT)
            tvCategory.text = args.dishDetails.category
            tvIngredients.text = args.dishDetails.ingredients
            tvCookingDirection.text = args.dishDetails.directionToCook.getFromHtml()
            tvCookingTime.text = resources.getString(
                R.string.lbl_estimate_cooking_time,
                args.dishDetails.cookingTime
            )
            setFavoriteDishImage(args.dishDetails.favoriteDish)
            ivFavoriteDish.setOnClickListener {
                with(args.dishDetails) {
                    favoriteDish = !favoriteDish
                    mFavDishViewModel.update(this)
                    setFavoriteDishImage(favoriteDish)
                    showFavoriteDishToast(favoriteDish)
                }
            }
        }
    }

    private fun setFavoriteDishImage(isFavoriteDish: Boolean) =
        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (isFavoriteDish) {
                    R.drawable.ic_favorite_selected
                } else {
                    R.drawable.ic_favorite_unselected
                }
            )
        )

    private fun showFavoriteDishToast(isFavoriteDish: Boolean) = Toast.makeText(
        requireContext(),
        if (isFavoriteDish) {
            R.string.msg_added_to_favorites
        } else {
            R.string.msg_removed_from_favorite
        },
        Toast.LENGTH_SHORT
    ).show()

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}