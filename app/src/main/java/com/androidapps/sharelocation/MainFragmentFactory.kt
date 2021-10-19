package com.androidapps.sharelocation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.androidapps.sharelocation.view.NameFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MainFragmentFactory {

    @ExperimentalCoroutinesApi
    class MainFragmentFactory
    @Inject
    constructor(
        private val someString: String
    ) : FragmentFactory() {

        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return when (className) {

                NameFragment::class.java.name -> {
                    val fragment =
                        NameFragment()
                    fragment
                }

                else -> super.instantiate(classLoader, className)
            }
        }
    }
}