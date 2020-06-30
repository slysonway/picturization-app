package com.esgi.picturization.ui.home.treated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.databinding.FragmentTreatedListBinding
import com.esgi.picturization.ui.home.image.list.ImageAdapter
import com.esgi.picturization.ui.home.image.list.OnRecycleListInteractionListener
import com.esgi.picturization.util.snackbar
import kotlinx.android.synthetic.main.fragment_start.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TreatedListFragment : Fragment(), KodeinAware, TreatedListener, OnRecycleListInteractionListener {
    override val kodein by kodein()
    private val factory: TreatedListViewModelFactory by instance<TreatedListViewModelFactory>()
    private lateinit var viewModel: TreatedListViewModel

    private lateinit var recyclerImageList: RecyclerView
    private lateinit var imageListAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTreatedListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_treated_list, container, false)
        viewModel = ViewModelProvider(this, factory).get(TreatedListViewModel::class.java)
        viewModel.treatedListener = this
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        recyclerImageList = binding.imageList
        recyclerImageList.layoutManager = GridLayoutManager(requireContext(), 2)
        imageListAdapter = ImageAdapter(true)
        imageListAdapter.listener = this
        recyclerImageList.adapter = imageListAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipe_container.setOnRefreshListener {
            viewModel.getImage()
        }
        viewModel.getImage()
    }

    override fun onStarted() {
        swipe_container.isRefreshing = true
    }

    override fun onFinish() {
        swipe_container.isRefreshing = false
    }

    override fun onError(message: String) {
        requireView().snackbar(message)
    }

    override fun onSuccess() {
        imageListAdapter.setData(viewModel.imageList)
    }

    override fun onListFragmentInteraction(position: Int) {
        val image = viewModel.imageList[position]
        val action = TreatedListFragmentDirections.actionTreatedListFragmentToImageDetailsFragment(image)
        requireView().findNavController().navigate(action)
    }
}