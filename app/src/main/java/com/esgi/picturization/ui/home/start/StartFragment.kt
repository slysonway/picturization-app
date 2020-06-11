package com.esgi.picturization.ui.home.start

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.esgi.picturization.util.toast
import kotlinx.android.synthetic.main.fragment_start.*
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
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), TAKE_PICTURE_PERMISSION_CODE)
            } else {
                takePicture()
            }
        }

        binding.floatingBtnGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_GALLERY_PERMISSION_CODE)
            } else {
                pickFromGalley()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun pickFromGalley() {
        val pickPhoto = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.type = "image/*"
        startActivityForResult(pickPhoto, PICK_GALLERY_CODE)
    }

    private fun takePicture() {
        val takePicture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, TAKE_PICTURE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                TAKE_PICTURE_CODE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                        image_view.setImageBitmap(bitmap)
                        Log.d(this::class.java.simpleName, "Camera Result")
                    }
                }
                PICK_GALLERY_CODE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val selectedImage: Uri? = data.data
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        if (selectedImage != null) {
                            val cursor = requireActivity().contentResolver.query(
                                selectedImage,
                                filePathColumn,
                                null,
                                null,
                                null
                            )
                            if (cursor != null) {
                                cursor.moveToFirst()
                                val columnIndex  = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath = cursor.getString(columnIndex)
                                val bitmap =  BitmapFactory.decodeFile(picturePath)
                                image_view.setImageBitmap(bitmap)
                                Log.d(this::class.java.simpleName, "Gallery Result")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            TAKE_PICTURE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    requireContext().toast("Permission Denied")
                }
            }
            PICK_GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGalley()
                } else {
                    requireContext().toast("Permission Denied")
                }
            }
        }
    }


    companion object {
        private const val TAKE_PICTURE_CODE = 0
        private const val PICK_GALLERY_CODE = 1

        private const val TAKE_PICTURE_PERMISSION_CODE = 1000
        private const val PICK_GALLERY_PERMISSION_CODE = 1001
    }

}
