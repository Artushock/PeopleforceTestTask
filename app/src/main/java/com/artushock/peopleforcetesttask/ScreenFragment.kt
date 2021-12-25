package com.artushock.peopleforcetesttask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artushock.peopleforcetesttask.databinding.FragmentLayoutBinding

const val COUNT_ARG = "COUNT_ARG"

class ScreenSlidePageFragment(
    private val plusButtonClickedListener: OnPlusButtonPressed,
    private val minusButtonClickedListener: OnMinusButtonPressed,
    private val notificationButtonClickedListener: OnNotificationButtonPressed,
    private val destroyFragmentListener: OnDestroyFragment,
) : Fragment() {

    private var _binding: FragmentLayoutBinding? = null
    private val binding get() = _binding!!

    private var number: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val plusButton = binding.fragmentLayoutPlusButton
        val minusButton = binding.fragmentLayoutMinusButton
        val notificationButton = binding.fragmentLayoutCreateNewNotificationButton

        arguments?.takeIf { it.containsKey(COUNT_ARG) }?.apply {
            number = getInt(COUNT_ARG)
            binding.fragmentLayoutTextView.text = number.toString()

            if (number <= 1) minusButton.visibility = View.GONE
            else minusButton.visibility = View.VISIBLE
        }

        plusButton.setOnClickListener {
            plusButtonClickedListener.plusButtonClicked()
        }

        minusButton.setOnClickListener {
            minusButtonClickedListener.minusButtonClicked()
        }

        notificationButton.setOnClickListener {
            notificationButtonClickedListener.notificationButtonClicked(number)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyFragmentListener.fragmentDestroyed(number)
    }

    interface OnPlusButtonPressed {
        fun plusButtonClicked()
    }

    interface OnMinusButtonPressed {
        fun minusButtonClicked()
    }

    interface OnNotificationButtonPressed {
        fun notificationButtonClicked(number: Int)
    }

    interface OnDestroyFragment {
        fun fragmentDestroyed(number: Int)
    }
}
