package com.maxgol.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.view.activities.AddUpdateDishActivity
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory
import com.maxgol.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

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
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_all_dishes, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Dish Title", "OnCreated")
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner, Observer { dishes ->
            dishes?.let {
                for (item in dishes) {
                    Log.i("Dish Title", "${item.id} :: ${item.title}")
                }
            }
        })
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