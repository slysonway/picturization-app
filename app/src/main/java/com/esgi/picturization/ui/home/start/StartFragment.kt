package com.esgi.picturization.ui.home.start

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.R
import com.esgi.picturization.databinding.FragmentStartBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() , KodeinAware {
    override val kodein by kodein()
    private val factory: StartViewModelFactory by instance<StartViewModelFactory>()
    private lateinit var viewModel: StartViewModel

    private var isFabOpen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container,false)
        viewModel = ViewModelProvider(this, factory).get(StartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        val fabRotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        val fabRotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)

        binding.floatingBtnMain.setOnClickListener {
            if (isFabOpen) {
                binding.run {
                    floatingBtnMain.startAnimation(fabRotateBackward)
                    floatingBtnTakePicture.startAnimation(fabClose)
                    floatingTxtTakePicture.startAnimation(fabClose)
                    floatingBtnGallery.startAnimation(fabClose)
                    floatingTxtGallery.startAnimation(fabClose)
                }
                isFabOpen = false
            } else {
                binding.run {
                    floatingBtnMain.startAnimation(fabRotateForward)
                    floatingBtnTakePicture.startAnimation(fabOpen)
                    floatingTxtTakePicture.startAnimation(fabOpen)
                    floatingBtnGallery.startAnimation(fabOpen)
                    floatingTxtGallery.startAnimation(fabOpen)
                }
                isFabOpen = true
            }
        }

        binding.floatingBtnTakePicture.setOnClickListener {
            val takePicture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicture, 0)
        }

        binding.floatingBtnGallery.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, 1)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode) {
            0 -> null
            1 -> null
        }
    }

}
