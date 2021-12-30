package com.maxgol.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maxgol.favdish.databinding.FragmentRandomDishBinding
import com.maxgol.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var mBinding: FragmentRandomDishBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromApi()
        randomDishViewModelObserver()
    }

    private fun randomDishViewModelObserver() = with(mRandomDishViewModel) {
        randomDishResponse.observe(viewLifecycleOwner) { randomDishResponse ->
            if (randomDishResponse == null) return@observe
            Log.i("Random Dish Response", "${randomDishResponse.recipes[0]}")
        }

        randomDishLoadingError.observe(viewLifecycleOwner) { dataError ->
            if (dataError == null) return@observe
            Log.e("Random Dish API Error", "$dataError")
        }

        loadRandomDish.observe(viewLifecycleOwner) { loadRandomDish ->
            if (loadRandomDish == null) return@observe
            Log.i("Random Dish Loading", "$loadRandomDish")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}