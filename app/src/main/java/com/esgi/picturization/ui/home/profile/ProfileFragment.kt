package com.esgi.picturization.ui.home.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.R
import com.esgi.picturization.databinding.FragmentProfileBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ProfileFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance<UserViewModelFactory>()

    private lateinit var viewModel: UserViewModel

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }



}
