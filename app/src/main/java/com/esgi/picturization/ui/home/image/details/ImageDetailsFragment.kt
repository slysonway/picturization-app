package com.esgi.picturization.ui.home.image.details

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.esgi.picturization.R
import com.esgi.picturization.databinding.FragmentImageDetailsBinding
import com.esgi.picturization.databinding.FragmentUntreatedListBinding
import kotlinx.android.synthetic.main.fragment_image_details.*
import kotlinx.android.synthetic.main.fragment_image_details.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class ImageDetailsFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val facotry: ImageDetailsViewModelFactory by instance<ImageDetailsViewModelFactory>()
    private lateinit var viewModel: ImageDetailsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentImageDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_details, container, false)
        viewModel = ViewModelProvider(this, facotry).get(ImageDetailsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.btnClose.setOnClickListener {
            requireView().findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        arguments?.let { bundle ->
            if (!bundle.isEmpty) {
                val args = ImageDetailsFragmentArgs.fromBundle(bundle)
                Glide.with(requireView())
                    .load(args.image.urlUntreated)
                    .placeholder(circularProgressDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(image_preview)
                txt_created_at.text = SimpleDateFormat(getString(R.string.date_format)).format(args.image.createdAt)
                args.image.updatedAt?.let {
                    txt_updated_at.text = SimpleDateFormat(getString(R.string.date_format)).format(it)
                }
                txt_treaty.text = args.image.treaty.toString()
                val filter = args.image.filters.values.toString()
                txt_filter.text = filter

                viewModel.image = args.image
            }
        }
    }

}
