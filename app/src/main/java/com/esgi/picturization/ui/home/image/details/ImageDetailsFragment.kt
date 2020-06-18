package com.esgi.picturization.ui.home.image.details

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.esgi.picturization.R
import com.esgi.picturization.databinding.FragmentImageDetailsBinding
import com.esgi.picturization.util.dismiss
import com.esgi.picturization.util.toggle
import kotlinx.android.synthetic.main.bottom_menu_details.view.*
import kotlinx.android.synthetic.main.details_image_layout.view.*
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

        binding.bottomMenu.linear_layout_details.setOnClickListener {
            binding.detailsLayout.toggle()
        }

        binding.imagePreview.setOnClickListener {
            binding.detailsLayout.dismiss()
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
                    .into(image_preview)
                details_layout.txt_created_at.text = SimpleDateFormat(
                    getString(R.string.date_format)
                ).format(args.image.createdAt)

                args.image.updatedAt?.let {
                    details_layout.txt_updated_at.text = SimpleDateFormat(
                        getString(R.string.date_format)
                    ).format(it)
                }

                details_layout.txt_treaty.text = args.image.treaty.toString()

                viewModel.image = args.image
            }
        }
    }

}
