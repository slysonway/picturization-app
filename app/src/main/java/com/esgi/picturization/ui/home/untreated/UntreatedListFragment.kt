package com.esgi.picturization.ui.home.untreated

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
import com.esgi.picturization.databinding.FragmentUntreatedListBinding
import com.esgi.picturization.ui.home.image.list.ImageAdapter
import com.esgi.picturization.ui.home.image.list.OnRecycleListInteractionListener
import com.esgi.picturization.util.hide
import com.esgi.picturization.util.show
import com.esgi.picturization.util.snackbar
import kotlinx.android.synthetic.main.fragment_untreated_list.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class UntreatedListFragment : Fragment(), KodeinAware, UntreatedListener, OnRecycleListInteractionListener {
    override val kodein by kodein()
    private val factory: UntreatedListViewModelFactory by instance<UntreatedListViewModelFactory>()
    private lateinit var viewModel: UntreatedListViewModel

    private lateinit var recyclerImageList: RecyclerView
    private lateinit var imageListAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentUntreatedListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_untreated_list, container, false)
        viewModel = ViewModelProvider(this, factory).get(UntreatedListViewModel::class.java)
        viewModel.untreatedListener = this
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        recyclerImageList = binding.imageList
        recyclerImageList.layoutManager = GridLayoutManager(requireContext(), 2)
        imageListAdapter = ImageAdapter()
        imageListAdapter.listener = this
        recyclerImageList.adapter = imageListAdapter
        // Inflate the layout for this fragment
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
        if (viewModel.imageList.isNullOrEmpty()) {
            txt_empty_list_untreated.show()
        } else {
            txt_empty_list_untreated.hide()
        }
        imageListAdapter.setData(viewModel.imageList)
    }

    override fun onListFragmentInteraction(position: Int) {
        val image = viewModel.imageList[position]
        val action = UntreatedListFragmentDirections.actionUntreatedListFragmentToImageDetailsFragment(image)
        requireView().findNavController().navigate(action)
    }

}
