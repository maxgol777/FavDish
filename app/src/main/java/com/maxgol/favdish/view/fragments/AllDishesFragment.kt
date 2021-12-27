package com.maxgol.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.FragmentAllDishesBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.view.activities.AddUpdateDishActivity
import com.maxgol.favdish.view.activities.MainActivity
import com.maxgol.favdish.view.adapters.FavDishAdapter
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment(), DishDetails, DeleteDishAction {

    private lateinit var mBinding: FragmentAllDishesBinding

    private val mFavDishViewModel by viewModels<FavDishViewModel> {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val favDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = favDishAdapter


        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner, Observer { dishes ->
            if (dishes.isNotEmpty()) {
                mBinding.rvDishesList.visibility = View.VISIBLE
                mBinding.tvNoDishesAddedYet.visibility = View.GONE
                favDishAdapter.dishesList(dishes)
            } else {
                mBinding.rvDishesList.visibility = View.GONE
                mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
            }
        })
    }

    override fun deleteDish(favDish: FavDish) {

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(resources.getString(R.string.title_delete_dish))
            setMessage(resources.getString(R.string.msg_delete_dish_dialog, favDish.title))
            setIcon(android.R.drawable.ic_dialog_alert)
            setPositiveButton(resources.getString(android.R.string.ok)) { dialogInterface, _ ->
                mFavDishViewModel.delete(favDish)
                dialogInterface.dismiss()
            }
            setNegativeButton(resources.getString(android.R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun dishDetails(dish: FavDish) {
        findNavController().navigate(
            AllDishesFragmentDirections.actionNavigationAllDishesToNavigationDishDetails(
                dish
            )
        )
        (requireActivity() as? MainActivity)?.hideBottomNavigationView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.showBottomNavigationView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add_dish -> {
            startActivity(Intent(requireContext(), AddUpdateDishActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}