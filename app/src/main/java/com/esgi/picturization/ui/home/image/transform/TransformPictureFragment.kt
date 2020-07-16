package com.esgi.picturization.ui.home.image.transform

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.esgi.picturization.R
import com.esgi.picturization.data.models.Filter
import com.esgi.picturization.data.models.FilterEnum
import com.esgi.picturization.data.models.FilterParameterEnum
import com.esgi.picturization.databinding.FragmentTransformPictureBinding
import com.esgi.picturization.ui.home.image.transform.list.choice.FilterChoiceListAdapter
import com.esgi.picturization.ui.home.image.transform.list.choice.OnFilterChoiceListInteractionListener
import com.esgi.picturization.ui.home.image.transform.list.filter.FilterListAdapter
import com.esgi.picturization.ui.home.image.transform.list.filter.OnFilterListInteractionListener
import com.esgi.picturization.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.bichromatic_option_layout.view.*
import kotlinx.android.synthetic.main.bottom_menu_tranform.view.*
import kotlinx.android.synthetic.main.dual_picker_layout.view.*
import kotlinx.android.synthetic.main.fragment_transform_picture.*
import kotlinx.android.synthetic.main.radial_blur_options_layout.view.*
import kotlinx.android.synthetic.main.recycler_list_layout.view.*
import kotlinx.android.synthetic.main.slider_layout.view.*
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.IOException


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

    private lateinit var filterConfig: List<Filter>

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


        recyclerFilterList = binding.filterList.list
        recyclerFilterList.layoutManager = LinearLayoutManager(context)
        recyclerFilterList.setHasFixedSize(true)
        filterListAdapter =
            FilterListAdapter(false)
        filterListAdapter.listener = this
        recyclerFilterList.adapter = filterListAdapter

        binding.bottomMenu.linear_layout_filter.setOnClickListener {
            recyclerChoiceList.toggle()
            //recyclerFilterList.dismiss()
            binding.filterList.dismiss()
            binding.slider.dismiss()
            binding.dualPicker.dismiss()
            binding.radialBlurOption.dismiss()
            binding.bichromaticOption.dismiss()
        }

        binding.bottomMenu.linear_layout_list_filter.setOnClickListener {
            //recyclerFilterList.toggle()
            recyclerChoiceList.dismiss()
            binding.filterList.toggle()
            binding.slider.dismiss()
            binding.dualPicker.dismiss()
            binding.radialBlurOption.dismiss()
            binding.bichromaticOption.dismiss()
        }

        binding.imagePreview.setOnClickListener {
            recyclerChoiceList.dismiss()
            //recyclerFilterList.dismiss()
            binding.filterList.dismiss()
            binding.slider.dismiss()
            binding.dualPicker.dismiss()
            binding.radialBlurOption.dismiss()
            binding.bichromaticOption.dismiss()
        }

        binding.bottomMenu.linear_layout_send.setOnClickListener {
            recyclerChoiceList.dismiss()
            //recyclerFilterList.dismiss()
            binding.filterList.dismiss()
            if (filterListAdapter.itemCount < 1) {
                emptyFilterDialog()
            } else {
                viewModel.sendImage()
            }
        }

        binding.slider.simple_slider.addOnChangeListener { _, value, _ ->
            binding.slider.simple_slider_value.text = value.toInt().toString()
        }

        binding.slider.add_filter.setOnClickListener {
            viewModel.currentFilter?.let {
                val toAdd = Filter(it.name, it.parameter)
                toAdd.parameter[0].value = binding.slider.simple_slider_value.text.toString()
                addFilter(toAdd)
             }
            binding.slider.dismiss()
        }

        binding.dualPicker.first_btn.setOnClickListener {view ->
            viewModel.currentFilter?.let {
                if (it.name == FilterEnum.ASCII_IMAGE_CONVERSION) {
                    // if first button then false = 0
                    val toAdd = it
                    toAdd.parameter[0].value = "0"
                } else {
                    val toAdd = it
                    toAdd.parameter[0].value = view.first_btn.text.toString()
                }
                addFilter(it)
            }
            binding.dualPicker.dismiss()
        }

        binding.dualPicker.second_btn.setOnClickListener { view ->
            viewModel.currentFilter?.let {
                if (it.name == FilterEnum.ASCII_IMAGE_CONVERSION) {
                    // if first button then false = 0
                    it.parameter[0].value = "1"
                } else {
                    it.parameter[0].value = view.second_btn.text.toString()
                }
                addFilter(it)
            }
            binding.dualPicker.dismiss()
        }

        binding.radialBlurOption.blur_intensity.simple_slider.addOnChangeListener { _, value, _ ->
            binding.radialBlurOption.blur_intensity.simple_slider_value.text = value.toInt().toString()
        }

        binding.radialBlurOption.checkBox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radialBlurOption.checkBox2.isChecked = false
            }
        }

        binding.radialBlurOption.checkBox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radialBlurOption.checkBox1.isChecked = false
            }
        }

        binding.radialBlurOption.blur_intensity.add_filter.setOnClickListener {
            if (!binding.radialBlurOption.checkBox1.isChecked && !binding.radialBlurOption.checkBox2.isChecked) {
                emptyCheckBoxDialog()
            } else {
                viewModel.currentFilter?.let {
                    val toAdd = Filter(it.name, it.parameter)
                    toAdd.parameter.forEach { filterParameter ->
                        if (filterParameter.name == FilterParameterEnum.intensity) {
                            filterParameter.value = binding.radialBlurOption.blur_intensity.simple_slider_value.text.toString()
                        } else if (filterParameter.name == FilterParameterEnum.orientation) {
                            if (binding.radialBlurOption.checkBox1.isChecked) {
                                filterParameter.value = binding.radialBlurOption.checkBox1.text.toString();
                            } else {
                                filterParameter.value = binding.radialBlurOption.checkBox2.text.toString();
                            }
                        }
                    }
                    addFilter(toAdd)
                }
                binding.radialBlurOption.dismiss()
            }
        }

        binding.bichromaticOption.color_picker1.setOnClickListener {
            showColorPickerDialog(it)
        }

        binding.bichromaticOption.color_picker2.setOnClickListener {
            showColorPickerDialog(it)
        }

        binding.bichromaticOption.add_bichromatic.setOnClickListener {
            viewModel.currentFilter?.let {
                val toAdd = Filter(it.name, it.parameter)
                toAdd.parameter.forEach { filterParameter ->
                    if (filterParameter.name == FilterParameterEnum.color_filter_1) {
                        val background = binding.bichromaticOption.color_picker1.background
                        val color = (background as ColorDrawable).color
                        val hexColor = String.format("0x%06X", (0xFFFFFF and color))
                        filterParameter.value = hexColor
                    } else if (filterParameter.name == FilterParameterEnum.color_filter_2) {
                        val background = binding.bichromaticOption.color_picker2.background
                        val color = (background as ColorDrawable).color
                        val hexColor = String.format("0x%06X", (0xFFFFFF and color))
                        filterParameter.value = hexColor
                    }
                }
                addFilter(toAdd)
            }
            bichromatic_option.dismiss()
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

    private fun addFilter(filter: Filter) {
        viewModel.filterList.add(filter)
        filterListAdapter.setData(viewModel.filterList)
        bottom_menu.badge.text = filterListAdapter.itemCount.toString()
        successAddFilterDialog(filter.name.title)
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
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    private fun emptyCheckBoxDialog() {
        AlertDialog
            .Builder(requireContext())
            .setTitle(R.string.title_dialog_empty_checkbox)
            .setMessage(R.string.message_dialog_no_checkbox)
            .setPositiveButton("OK") { dialog, _ ->  dialog.dismiss() }
            .show()
    }

    override fun onFilterListener(position: Int) {
        viewModel.filterList.removeAt(position)
        filterListAdapter.setData(viewModel.filterList)
        bottom_menu.badge.text = filterListAdapter.itemCount.toString()
    }

    override fun onFilterChoiceListener(filter: FilterEnum) {
        val filterDetails = loadConfigFilter()?.first { it.name == filter }!!
        viewModel.currentFilter = filterDetails
        if (filterDetails.parameter.size > 1) {
            //TODO find what to do
            if (filterDetails.name == FilterEnum.RADIAL_BLUR) {
                filterDetails.parameter.forEach {
                    if (it.name == FilterParameterEnum.intensity) {
                        val maxVal = it.value.split("-")
                        radial_blur_option.blur_intensity.simple_slider.valueTo = maxVal[1].toFloat()
                        radial_blur_option.blur_intensity.title_slider.text = getString(it.name.title)
                    } else if (it.name == FilterParameterEnum.orientation) {
                        val value = it.value.split(",")
                        radial_blur_option.checkBox1.text = value[0]
                        radial_blur_option.checkBox2.text = value[1]
                        radial_blur_option.title_orientation.text = getString(it.name.title)
                    }
                }
                radial_blur_option.toggle()
            }
            if (filterDetails.name == FilterEnum.BICHROMATIC) {
                bichromatic_option.title_bichromatic_option.text = getString(filterDetails.name.title)
                bichromatic_option.toggle()
            }
        } else if (filterDetails.parameter[0].name == FilterParameterEnum.intensity) {
            val maxVal = filterDetails.parameter[0].value.split("-")
            slider.simple_slider.valueTo = maxVal[1].toFloat()
            slider.title_slider.text = getString(filterDetails.parameter[0].name.title)
            slider.toggle()
        } else if (filterDetails.parameter[0].name == FilterParameterEnum.size) {
            val value = filterDetails.parameter[0].value.split(",")
            dual_picker.first_btn.text = value[0]
            dual_picker.second_btn.text = value[1]
            dual_picker.title_dual_picker.text = getString(filterDetails.parameter[0].name.title)
            dual_picker.toggle()
        } else if (filterDetails.parameter[0].name == FilterParameterEnum.quality_reduction) {
            val maxVal = filterDetails.parameter[0].value.split("-")
            slider.simple_slider.valueTo = maxVal[1].toFloat()
            slider.title_slider.text = getString(filterDetails.parameter[0].name.title)
            slider.toggle()
        } else if (filterDetails.parameter[0].name == FilterParameterEnum.colored_chars) {
            dual_picker.first_btn.text = "True"
            dual_picker.second_btn.text = "False"
            dual_picker.title_dual_picker.text = getString(filterDetails.parameter[0].name.title)
            dual_picker.toggle()
        }
        recyclerChoiceList.toggle()
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

    private fun getJsonDataFromAsset(context: Context): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(Constants.FILTER_CONFIG_PATH).bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun loadConfigFilter(): List<Filter>? {
        val jsonString = getJsonDataFromAsset(requireContext())
        jsonString?.let {
            val gson = Gson()
            val filterListType = object : TypeToken<List<Filter>>() {}.type
             return gson.fromJson(jsonString.trimIndent(), filterListType)
        }
        return null
    }

    private fun showColorPickerDialog(view: View) {
        ChromaDialog.Builder()
            .initialColor(Color.WHITE)
            .colorMode(ColorMode.RGB)
            .onColorSelected(object : ColorSelectListener {
                override fun onColorSelected(color: Int) {
                    view.setBackgroundColor(color)
                }
            })
            .create()
            .show(requireActivity().supportFragmentManager, "Color Picker")
    }


}
