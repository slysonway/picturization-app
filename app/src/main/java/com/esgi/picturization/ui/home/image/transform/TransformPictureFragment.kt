package com.esgi.picturization.ui.home.image.transform

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.esgi.picturization.R
import com.esgi.picturization.data.models.FilterEnum
import com.esgi.picturization.databinding.FragmentTransformPictureBinding
import com.esgi.picturization.ui.home.image.transform.list.choice.FilterChoiceListAdapter
import com.esgi.picturization.ui.home.image.transform.list.choice.OnFilterChoiceListInteractionListener
import com.esgi.picturization.ui.home.image.transform.list.filter.FilterListAdapter
import com.esgi.picturization.ui.home.image.transform.list.filter.OnFilterListInteractionListener
import com.esgi.picturization.util.hide
import com.esgi.picturization.util.show
import com.esgi.picturization.util.snackbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import kotlinx.android.synthetic.main.bottom_menu_tranform.view.*
import kotlinx.android.synthetic.main.fragment_transform_picture.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


/**
 * A simple [Fragment] subclass.
 */
class TransformPictureFragment : Fragment(), KodeinAware, TransformListener,
    OnFilterListInteractionListener, OnFilterChoiceListInteractionListener {
    override val kodein by kodein()
    private val factory: TransformPictureViewModelFactory by instance<TransformPictureViewModelFactory>()
    private lateinit var viewModel: TransformPictureViewModel
    private lateinit var filterListAdapter: FilterListAdapter
    private lateinit var recyclerFilterList: RecyclerView

    private lateinit var choiceListAdapter: FilterChoiceListAdapter
    private lateinit var recyclerChoiceList: RecyclerView

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTransformPictureBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transform_picture, container, false)
        viewModel = ViewModelProvider(this, factory).get(TransformPictureViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.transformListener = this
        binding.lifecycleOwner = this

        recyclerChoiceList = binding.filterChoiceList
        recyclerChoiceList.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerChoiceList.setHasFixedSize(true)
        choiceListAdapter =
            FilterChoiceListAdapter()
        choiceListAdapter.listener = this
        recyclerChoiceList.adapter = choiceListAdapter


        recyclerFilterList = binding.filterList
        recyclerFilterList.layoutManager = LinearLayoutManager(context)
        recyclerFilterList.setHasFixedSize(true)
        filterListAdapter =
            FilterListAdapter()
        filterListAdapter.listener = this
        recyclerFilterList.adapter = filterListAdapter

        binding.bottomMenu.linear_layout_filter.setOnClickListener {
            filtersToggle(recyclerChoiceList)
            dismissList(recyclerFilterList)
        }

        binding.bottomMenu.linear_layout_list_filter.setOnClickListener {
            filtersToggle(recyclerFilterList)
            dismissList(recyclerChoiceList)
        }

        binding.imagePreview.setOnClickListener {
            dismissList(recyclerChoiceList)
            dismissList(recyclerFilterList)
        }

        binding.bottomMenu.linear_layout_send.setOnClickListener {
            dismissList(recyclerChoiceList)
            dismissList(recyclerFilterList)
            if (filterListAdapter.itemCount < 1) {
                emptyFilterDialog()
            } else {
                viewModel.sendImage()
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            if (!bundle.isEmpty) {
                val args = TransformPictureFragmentArgs.fromBundle(bundle)
                viewModel.image.value = args.image
            }
        }
    }

    private fun addFilter(filter: FilterEnum) {
        viewModel.filterList.add(filter)
        filterListAdapter.setData(viewModel.filterList)
        bottom_menu.badge.text = filterListAdapter.itemCount.toString()

        successAddFilterDialog(filter.title)
    }

    private fun emptyFilterDialog() {
        AlertDialog
            .Builder(requireContext())
            .setTitle(R.string.title_dialog_empty_filter)
            .setMessage(R.string.message_dialog_empty_filter)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()}
            .show()
    }

    private fun successAddFilterDialog(filterName: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.title_dialog_filter_added))
        dialogBuilder.setMessage(getString(R.string.message_dialog_filter_added, getString(filterName)))
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })
        dialogBuilder.show()
    }

    override fun onFilterListener(position: Int) {
        viewModel.filterList.removeAt(position)
        filterListAdapter.setData(viewModel.filterList)
        bottom_menu.badge.text = filterListAdapter.itemCount.toString()
    }

    override fun onFilterChoiceListener(filter: FilterEnum) {
        addFilter(filter)
        filtersToggle(recyclerChoiceList)
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onFinish() {
        progress_bar.hide()
    }

    override fun onError(message: String) {
        requireView().snackbar(message)
    }

    override fun onSuccess() {
        requireView().snackbar(getString(R.string.message_success_send))
        requireView().findNavController().navigateUp()
    }

    private fun filtersToggle(layout: RecyclerView) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 500
        transition.addTarget(layout)

        if (layout.visibility == View.INVISIBLE) {
            TransitionManager.beginDelayedTransition(requireView() as ViewGroup, transition)
            layout.visibility = View.VISIBLE
        } else {
            TransitionManager.beginDelayedTransition(requireView() as ViewGroup, transition)
            layout.visibility = View.INVISIBLE
        }
    }

    private fun dismissList(layout: RecyclerView) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 500
        transition.addTarget(layout)
        TransitionManager.beginDelayedTransition(requireView() as ViewGroup, transition)
        layout.visibility = View.INVISIBLE
    }


}
