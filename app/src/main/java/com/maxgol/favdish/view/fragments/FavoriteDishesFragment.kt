package com.maxgol.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.FragmentFavoriteDishesBinding
import com.maxgol.favdish.view.adapters.FavDishAdapter
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {
    private val mFavDishViewModel by viewModels<FavDishViewModel> {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    private var mBinding: FragmentFavoriteDishesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding!!) {
            rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
            val favDishAdapter = FavDishAdapter(this@FavoriteDishesFragment)
            rvDishesList.adapter = favDishAdapter
            mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
                if (dishes.isNotEmpty()) {
                    rvDishesList.visibility = View.VISIBLE
                    tvNoDishesAddedYet.visibility = View.GONE
                    favDishAdapter.dishesList(dishes)
                } else {
                    rvDishesList.visibility = View.GONE
                    tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}