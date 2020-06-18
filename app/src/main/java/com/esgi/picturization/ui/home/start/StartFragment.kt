package com.esgi.picturization.ui.home.start

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.data.models.Image
import com.esgi.picturization.databinding.FragmentStartBinding
import com.esgi.picturization.ui.home.image.list.ImageAdapter
import com.esgi.picturization.ui.home.image.list.OnRecycleListInteractionListener
import com.esgi.picturization.util.snackbar
import com.esgi.picturization.util.toast
import kotlinx.android.synthetic.main.fragment_start.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment(), KodeinAware, StartListener,
    OnRecycleListInteractionListener {
    override val kodein by kodein()
    private val factory: StartViewModelFactory by instance<StartViewModelFactory>()
    private lateinit var viewModel: StartViewModel

    private lateinit var recyclerImageList: RecyclerView
    private lateinit var imageListAdapter: ImageAdapter

    private var isFabOpen: Boolean = false
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container,false)
        viewModel = ViewModelProvider(this, factory).get(StartViewModel::class.java)
        viewModel.startListener = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        recyclerImageList = binding.imageList
        recyclerImageList.layoutManager = GridLayoutManager(requireContext(), 2)
        imageListAdapter =
            ImageAdapter(true)
        imageListAdapter.listener = this
        recyclerImageList.adapter = imageListAdapter



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
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), TAKE_PICTURE_PERMISSION_CODE)
            } else {
                takePicture()
            }
        }

        binding.floatingBtnGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_GALLERY_PERMISSION_CODE)
            } else {
                pickFromGalley()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipe_container.setOnRefreshListener {
            viewModel.getImage()
        }

        viewModel.getImage()

    }

    private fun pickFromGalley() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.type = "image/*"
        startActivityForResult(pickPhoto, PICK_GALLERY_CODE)
    }

    private fun takePicture() {
        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "Test New Picture")
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        imageUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val takePicture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(takePicture, TAKE_PICTURE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                TAKE_PICTURE_CODE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        imageUri?.let {
                            onTransformPicture(it)
                        } ?: run {
                            requireContext().toast(getString(R.string.error_open_picture))
                        }
                        //TODO add error for uri null
                    }
                }
                PICK_GALLERY_CODE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val selectedImage: Uri? = data.data
                        selectedImage?.let {
                            onTransformPicture(it)
                        } ?: run {
                            requireContext().toast(getString(R.string.error_open_picture))
                        }
                        //TODO add error for uri null
                    }
                }
            }
        }
    }

    private fun onTransformPicture(uri: Uri) {
        val realPath = getRealPathFromURIPath(uri, requireActivity())
        realPath?.let {
            val file = File(it)
            val action = StartFragmentDirections.actionStartFragmentToTransformPictureFragment(Image(file, ArrayList()))
            requireView().findNavController().navigate(action)
        }
    }

    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor? =
            activity.contentResolver.query(contentURI, proj, null, null, null)
        return if (cursor == null) {
            contentURI.getPath()
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
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

    override fun onListFragmentInteraction(position: Int) {
        val image = viewModel.imageList.value!![position]
        val action = StartFragmentDirections.actionStartFragmentToImageDetailsFragment(image)
        requireView().findNavController().navigate(action)
    }

    override fun onStarted() {
        swipe_container.isRefreshing = true
    }

    override fun onFinish() {
        swipe_container.isRefreshing = false
    }

    override fun onSuccess() {
        imageListAdapter.setData(viewModel.imageList.value!!)
    }

    override fun onError(message: String) {
            requireView().snackbar(message)
    }

    companion object {
        private const val TAKE_PICTURE_CODE = 0
        private const val PICK_GALLERY_CODE = 1

        private const val TAKE_PICTURE_PERMISSION_CODE = 1000
        private const val PICK_GALLERY_PERMISSION_CODE = 1001
    }
}
