package com.esgi.picturization.ui.home.image.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide

import com.esgi.picturization.R
import kotlinx.android.synthetic.main.fragment_image_details.*
import kotlinx.android.synthetic.main.fragment_image_details.view.*
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class ImageDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_details, container, false)

        view.btn_close.setOnClickListener {
            requireView().findNavController().navigateUp()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            if (!bundle.isEmpty) {
                val args = ImageDetailsFragmentArgs.fromBundle(bundle)
                Glide.with(requireView()).load(args.image.url).into(image_preview)
                txt_created_at.text = SimpleDateFormat(getString(R.string.date_format)).format(args.image.createdAt)
                args.image.updatedAt?.let {
                    txt_updated_at.text = SimpleDateFormat(getString(R.string.date_format)).format(it)
                }
                txt_treaty.text = args.image.treaty.toString()
                val filter = args.image.filters.values.toString()
                txt_filter.text = filter
            }
        }
    }

}
