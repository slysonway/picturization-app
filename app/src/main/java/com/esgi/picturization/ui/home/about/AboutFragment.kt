package com.esgi.picturization.ui.home.about

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esgi.picturization.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onResume() {
        super.onResume()
        try {
            val packageInfo: PackageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            val version = packageInfo.versionName
            app_version.text = getString(R.string.about_version, getString(R.string.app_name), version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}