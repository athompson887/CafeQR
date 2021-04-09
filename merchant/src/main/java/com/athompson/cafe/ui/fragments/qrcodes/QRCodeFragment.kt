package com.athompson.cafe.ui.fragments.qrcodes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentQrcodeBinding
import com.athompson.cafe.databinding.SimpleVenueDisplayCardBinding
import com.athompson.cafe.extensions.ViewExtensions.setImage
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.models.Venue

class QRCodeFragment : BaseFragment() {

    private lateinit var qrCodeViewModel: QRCodeViewModel
    private lateinit var binding: FragmentQrcodeBinding
    private var venues: ArrayList<Venue> = ArrayList()
    private lateinit var venuesViewPagerAdapter: VenuesViewPagerAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentQrcodeBinding.bind(view)
        venuesViewPagerAdapter = VenuesViewPagerAdapter()
        binding.viewPager.adapter = venuesViewPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                println(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                println(position)
            }


            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                toolBarTitle(venues.get(position).name)
                toolBarSubTitle(venues.get(position).location)
                qrCodeViewModel.generateBarCode(venues.get(position))
            }

        })
        getVenuesList()
    }

    private fun getVenuesList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreVenue().getAll(::successVenuesList, ::failureVenueList)
    }

    private fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {

        if (venuesList.isNullOrEmpty()) {
            noVenue()
        } else {
            venues.clear()
            venues.addAll(venuesList)
            venuesViewPagerAdapter.dataChanged()
        }
        hideProgressDialog()
    }

    private fun noVenue() {
        venues.clear()
        venues.add(Venue("fake_venue", "", "", "", ""))
        venuesViewPagerAdapter.dataChanged()
        hideProgressDialog()
    }

    private fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus", e)
        noVenue()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        qrCodeViewModel = ViewModelProvider(this).get(QRCodeViewModel::class.java)
        qrCodeViewModel.qrImage.observe(viewLifecycleOwner, {
            binding.qrImage.setImageBitmap(it)
            binding.qrImage.show()
        })
        return inflater.inflate(R.layout.fragment_qrcode, container, false)
    }

    inner class VenuesViewPagerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return VenueViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.simple_venue_display_card,
                    parent,
                    false
                )
            )
        }

        @SuppressLint("NotifyDataSetChanged")
        fun dataChanged() {
            notifyDataSetChanged()
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val venue = venues[position]

            if (holder is VenueViewHolder) {

                holder.binding.image.setImage(venue.imageUrl, R.drawable.cafe_image)
                holder.binding.selectedVenueName.text = venue.name
                holder.binding.selectedVenueTown.text = venue.location
            }
        }

        override fun getItemCount(): Int {
            return venues.size
        }


        inner class VenueViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val binding: SimpleVenueDisplayCardBinding = SimpleVenueDisplayCardBinding.bind(mView)

            override fun toString(): String {
                return super.toString() + " '"
            }
        }
    }
}