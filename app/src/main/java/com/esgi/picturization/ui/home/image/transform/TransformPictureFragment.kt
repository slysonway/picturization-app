package com.esgi.picturization.ui.home.image.transform

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.data.models.FilterEnum
import com.esgi.picturization.databinding.FragmentTransformPictureBinding
import com.esgi.picturization.util.hide
import com.esgi.picturization.util.show
import kotlinx.android.synthetic.main.fragment_transform_picture.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


/**
 * A simple [Fragment] subclass.
 */
class TransformPictureFragment : Fragment(), KodeinAware, TransformListener, OnRecyclerListInteractionListener {
    override val kodein by kodein()
    private val factory: TransformPictureViewModelFactory by instance<TransformPictureViewModelFactory>()
    private lateinit var viewModel: TransformPictureViewModel
    private lateinit var filterListAdapter: FilterListAdapter
    private lateinit var recyclerFilterList: RecyclerView
    private lateinit var dialogBuilder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTransformPictureBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transform_picture, container, false)
        viewModel = ViewModelProvider(this, factory).get(TransformPictureViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.transformListener = this
        binding.lifecycleOwner = this

        recyclerFilterList = binding.itemFilterList
        recyclerFilterList.layoutManager = LinearLayoutManager(context)
        recyclerFilterList.setHasFixedSize(true)
        filterListAdapter = FilterListAdapter()
        filterListAdapter.listener = this
        recyclerFilterList.adapter = filterListAdapter

        dialogBuilder = initFilterListDialog()


        binding.btnAddFilter.setOnClickListener {
            dialogBuilder.show()
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

    private fun initFilterListDialog(): AlertDialog.Builder {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.title_dialog_filter))
        val arrayAdapter = ArrayAdapter<FilterEnum>(requireContext(), android.R.layout.select_dialog_singlechoice)
        enumValues<FilterEnum>().forEach {
            arrayAdapter.add(it)
        }

        dialogBuilder.setAdapter(arrayAdapter, DialogInterface.OnClickListener { _, which ->
            val filter = arrayAdapter.getItem(which)
            viewModel.filterList.add(filter!!)
            filterListAdapter.setData(viewModel.filterList)
            val builderInner = AlertDialog.Builder(requireContext())
            builderInner.setTitle(getString(R.string.title_dialog_filter_added))
            builderInner.setMessage(getString(R.string.message_dialog_filter_added, filter.name))
            builderInner.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            builderInner.show()
        })
        return dialogBuilder
    }

    override fun onListFragmentInteraction(position: Int) {
        viewModel.filterList.removeAt(position)
        filterListAdapter.setData(viewModel.filterList)
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onFinish() {
        progress_bar.hide()
    }

    override fun onError() {
        TODO("Not yet implemented")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

}
