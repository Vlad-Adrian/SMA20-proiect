package com.vlad.passKeeper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vlad.passKeeper.R

class PasswordsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pass_fragment, container, false)
        return view
    }

    companion object {
        //companion object helps call functions just by ClassName.FunctionName
        fun newInstance(): PasswordsFragment {
            return PasswordsFragment()
        }
    }
}