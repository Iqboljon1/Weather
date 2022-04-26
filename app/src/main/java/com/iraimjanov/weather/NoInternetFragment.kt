package com.iraimjanov.weather

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.iraimjanov.weather.databinding.FragmentNoInternetBinding
import com.iraimjanov.weather.datas.NetworkHelper

class NoInternetFragment : Fragment() {
    private lateinit var binding: FragmentNoInternetBinding
    private lateinit var networkHelper: NetworkHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNoInternetBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())

        binding.imageIcRefresh.setOnClickListener {
            if (networkHelper.isNetworkConnected()) {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }
}