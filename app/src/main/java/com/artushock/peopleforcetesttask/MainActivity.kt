package com.artushock.peopleforcetesttask

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.artushock.peopleforcetesttask.databinding.ActivityMainBinding

const val CHANNEL_ID = "CHANNEL_ID"
const val PAGER_POSITION = "PAGER_POSITION"
const val AMOUNT_OF_FRAGMENTS = "AMOUNT_OF_FRAGMENTS"

class MainActivity : AppCompatActivity() {

    private var fragmentsCounter = 1

    private lateinit var binding: ActivityMainBinding
    private lateinit var mPager: ViewPager2
    private lateinit var mAdapter: SlideFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val amountOfFragments = intent.getIntExtra(AMOUNT_OF_FRAGMENTS, 1)
        fragmentsCounter = amountOfFragments

        val position = intent.getIntExtra(PAGER_POSITION, 1)

        mPager = binding.container
        mAdapter = SlideFragmentAdapter(this)
        mPager.adapter = mAdapter
        mPager.setCurrentItem(position, true)
    }

    inner class SlideFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        private val plusListener = getPlusButtonPressedListener()
        private val minusListener = getMinusButtonPressedListener()
        private val notificationListener = getNotificationButtonPressedListener()
        private val destroyFragmentListener = getDestroyFragmentListener()

        override fun getItemCount(): Int = fragmentsCounter

        override fun createFragment(position: Int): Fragment {
            val fragment =
                ScreenSlidePageFragment(
                    plusListener,
                    minusListener,
                    notificationListener,
                    destroyFragmentListener,
                )

            fragment.arguments = Bundle().apply {
                putInt(COUNT_ARG, position + 1)
            }
            return fragment
        }
    }

    private fun getDestroyFragmentListener() = object : ScreenSlidePageFragment.OnDestroyFragment {
        override fun fragmentDestroyed(number: Int) {
            removeNotification(number)
        }
    }

    private fun getNotificationButtonPressedListener() =
        object : ScreenSlidePageFragment.OnNotificationButtonPressed {
            override fun notificationButtonClicked(number: Int) {
                sendNotification(number)
            }
        }

    private fun getMinusButtonPressedListener() =
        object : ScreenSlidePageFragment.OnMinusButtonPressed {
            override fun minusButtonClicked() {
                val deletedPosition = fragmentsCounter
                fragmentsCounter--
                mAdapter.notifyItemRemoved(deletedPosition)
                mPager.setCurrentItem(fragmentsCounter, true)
            }
        }

    private fun getPlusButtonPressedListener() =
        object : ScreenSlidePageFragment.OnPlusButtonPressed {
            override fun plusButtonClicked() {
                fragmentsCounter++
                mAdapter.notifyItemInserted(fragmentsCounter)
                mPager.currentItem = fragmentsCounter
            }
        }

    private fun sendNotification(number: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(PAGER_POSITION, number - 1)
        intent.putExtra(AMOUNT_OF_FRAGMENTS, fragmentsCounter)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
            .setContentText("Notification $number")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(number, builder.build())
    }

    private fun removeNotification(number: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(number)
    }
}

