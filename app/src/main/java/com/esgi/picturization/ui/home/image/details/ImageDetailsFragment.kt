package com.esgi.picturization.ui.home.image.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.esgi.picturization.BuildConfig
import com.esgi.picturization.R
import com.esgi.picturization.data.network.download.DownloadResult
import com.esgi.picturization.data.network.download.downloadFile
import com.esgi.picturization.databinding.FragmentImageDetailsBinding
import com.esgi.picturization.ui.home.image.transform.list.filter.FilterListAdapter
import com.esgi.picturization.util.dismiss
import com.esgi.picturization.util.hide
import com.esgi.picturization.util.show
import com.esgi.picturization.util.toggle
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.android.synthetic.main.bottom_menu_details.view.*
import kotlinx.android.synthetic.main.details_image_layout.view.*
import kotlinx.android.synthetic.main.fragment_image_details.*
import kotlinx.android.synthetic.main.horizontal_progress_bar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 */
class ImageDetailsFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val facotry: ImageDetailsViewModelFactory by instance<ImageDetailsViewModelFactory>()
    private lateinit var viewModel: ImageDetailsViewModel

    private lateinit var filterListAdapter: FilterListAdapter
    private lateinit var recyclerFilterList: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentImageDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_details, container, false)
        viewModel = ViewModelProvider(this, facotry).get(ImageDetailsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        recyclerFilterList = binding.filterList
        recyclerFilterList.layoutManager = LinearLayoutManager(context)
        recyclerFilterList.setHasFixedSize(true)
        filterListAdapter = FilterListAdapter()
        recyclerFilterList.adapter = filterListAdapter

        binding.bottomMenu.linear_layout_details.setOnClickListener {
            binding.detailsLayout.toggle()
        }

        binding.bottomMenu.linear_layout_list_filter.setOnClickListener {
            recyclerFilterList.toggle()
            binding.detailsLayout.dismiss()
        }

        binding.imagePreview.setOnClickListener {
            binding.detailsLayout.dismiss()
            recyclerFilterList.dismiss()
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

                filterListAdapter.setData(args.image.filters)
                bottom_menu.badge.text = filterListAdapter.itemCount.toString()

                viewModel.image = args.image
            }
        }

        setDownloadButtonClickListener()
    }

    private fun setDownloadButtonClickListener() {
        val folder = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        val filename = "Test_download.png"
        val file = File(folder, filename)
        val uri = context?.let {
            FileProvider.getUriForFile(it, "${BuildConfig.APPLICATION_ID}.provider", file)
        }
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri?.path)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        bottom_menu.linear_layout_download.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.setDataAndType(uri, mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(Intent.EXTRA_TITLE, filename)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.let { context ->
                    //TODO change download URL
                    downloadFile(context, viewModel.image.urlUntreated, uri)
                    horizontal_progress_layout.show()
                }
            }
        }
    }

    private fun downloadFile(context: Context, url: String, file: Uri) {
        val ktor = HttpClient(Android)
        viewModel.setDownload(true)
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                viewModel.setDownload(false)
                                horizontal_progress_layout.progress_bar.progress = 0
                                viewFile(file)
                                //TODO SUCCESS DOWNLOAD
                                horizontal_progress_layout.hide()
                            }
                            is DownloadResult.Error -> {
                                viewModel.setDownload(false)
                                Toast.makeText(context, "Error while download File", Toast.LENGTH_LONG).show()
                            }
                            is DownloadResult.Progress -> {
                                horizontal_progress_layout.progress_bar.progress = it.progress
                            }
                        }
                    }
                }
            }
        }
    }

    private fun viewFile(uri: Uri) {
        context?.let {context ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val chooser = Intent.createChooser(intent, "Open with")

            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(context, "No suitable application to open file", Toast.LENGTH_LONG).show()
            }
        }
    }
}
