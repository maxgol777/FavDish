package com.maxgol.favdish.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.DialogCustomListBinding
import com.maxgol.favdish.databinding.FragmentAllDishesBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.utils.Constants
import com.maxgol.favdish.view.activities.AddUpdateDishActivity
import com.maxgol.favdish.view.activities.MainActivity
import com.maxgol.favdish.view.adapters.CustomListItemAdapter
import com.maxgol.favdish.view.adapters.FavDishAdapter
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment(), DishDetails, DeleteDishAction {

    private lateinit var mBinding: FragmentAllDishesBinding
    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog: Dialog

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
        mFavDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = mFavDishAdapter


        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner, { dishes ->
            bindAllDishes(dishes)
        })
    }

    private fun bindAllDishes(dishes: List<FavDish>) {
        if (dishes.isNotEmpty()) {
            mBinding.rvDishesList.visibility = View.VISIBLE
            mBinding.tvNoDishesAddedYet.visibility = View.GONE
            mFavDishAdapter.dishesList(dishes)
        } else {
            mBinding.rvDishesList.visibility = View.GONE
            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
        }
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

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireContext())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        val adapter =
            CustomListItemAdapter(
                activity = requireActivity(),
                fragment = this,
                listItems = dishTypes,
                selection = Constants.FILTER_SELECTION
            )
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
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
        R.id.action_filer_dishes -> {
            filterDishesListDialog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()
        when (filterItemSelection) {
            Constants.ALL_ITEMS -> {
                mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) {
                    bindAllDishes(dishes = it)
                }
            }
            else -> {
                mFavDishViewModel.getFilteredList(filterItemSelection)
                    .observe(viewLifecycleOwner) { dishes ->
                        val nullOrEmpty = dishes.isNullOrEmpty()
                        setupFilteredDishesList(nullOrEmpty)
                        if (!nullOrEmpty) {
                            mFavDishAdapter.dishesList(dishes)
                        }
                    }
            }
        }
    }

    private fun setupFilteredDishesList(isEmpty: Boolean) {
        mBinding.rvDishesList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        mBinding.tvNoDishesAddedYet.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}