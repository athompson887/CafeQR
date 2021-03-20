package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.athompson.cafe.R
import com.athompson.cafe.adapters.SimpleMenuAdapter
import com.athompson.cafe.adapters.SimpleVenueAdapter
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.firestore.FireStoreMenu
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.activities.AddMenuActivity
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication.Companion.selectedCafeQrMenu
import com.athompson.cafelib.shared.CafeQRApplication.Companion.selectedVenue
import kotlinx.android.synthetic.main.add_item.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var cafeQrMenus: ArrayList<CafeQrMenu> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private lateinit var simpleMenuAdapter:SimpleMenuAdapter
    private lateinit var simpleVenueAdapter:SimpleVenueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        dashboardViewModel.text.observe(viewLifecycleOwner, {
            //textView.text = it
        })


        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        binding.addMenu.setOnClickListener{
            startActivity(Intent(activity, AddMenuActivity::class.java))
        }
        binding.addMenuButton.setOnClickListener{
            startActivity(Intent(activity, AddMenuActivity::class.java))
        }
        binding.addVenue.setOnClickListener{
            startActivity(Intent(activity, AddVenuesActivity::class.java))
        }
        binding.addVenueButton.setOnClickListener{
            startActivity(Intent(activity, AddVenuesActivity::class.java))
        }


        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
        getVenuesList()
    }



    private fun setupRecycler()
    {
        simpleVenueAdapter = SimpleVenueAdapter(requireContext(),venues)
        simpleMenuAdapter = SimpleMenuAdapter(requireContext(),cafeQrMenus)
        binding.recyclerVenues.setLayoutManagerVertical()
        binding.recyclerVenues.itemAnimator = DefaultItemAnimator()
        binding.recyclerVenues.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.HORIZONTAL
            )
        )
        binding.recyclerVenues.adapter = simpleVenueAdapter


        binding.recyclerMenus.setLayoutManagerVertical()
        binding.recyclerMenus.itemAnimator = DefaultItemAnimator()
        binding.recyclerMenus.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.HORIZONTAL
            )
        )
        binding.recyclerMenus.adapter = simpleMenuAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getVenuesList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreVenue().getVenueItemsList(::successVenuesList, ::failureVenueList)
    }

    private fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus",e)
        binding.recyclerVenues.hide()
        binding.noVenuesView.show()
        hideProgressDialog()
    }

    private fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {
        if (venuesList.size > 0) {
            venues.clear()
            venues.addAll(venuesList)
            setSelectedVenue(venues[0])
            binding.venuesView.show()
            binding.noVenuesView.hide()
        } else {
            binding.venuesView.hide()
            binding.noVenuesView.show()
        }
        getMenusList()
        simpleVenueAdapter.dataChanged(venues)
        hideProgressDialog()
    }

    private fun getMenusList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreMenu().getMenuList(::successfulCafeQrMenuList,::failureCafeQrMenuList)
    }

    private fun successfulCafeQrMenuList(menuList: ArrayList<CafeQrMenu>) {
        if (menuList.size > 0) {
            cafeQrMenus.clear()
            cafeQrMenus.addAll(menuList)
            setSelectedMenu(cafeQrMenus[0])
            binding.menusView.show()
            binding.noMenusView.hide()
        } else {
            binding.menusView.hide()
            binding.noMenusView.show()
        }
        simpleMenuAdapter.dataChanged(cafeQrMenus)
        hideProgressDialog()
    }

    private fun failureCafeQrMenuList(e: Exception) {
        hideProgressDialog()
    }

    private fun setSelectedMenu(cqrMeny: CafeQrMenu) {
        selectedCafeQrMenu = cqrMeny
        val s = selectedCafeQrMenu
        if(s!=null) {
            binding.selectedMenuName.text = s.name
            binding.selectedMenDescription.text = s.description
        }
        getVenuesList()
    }

    private fun setSelectedVenue(venue: Venue) {
        selectedVenue = venue
        val s = selectedVenue
        if(s!=null) {
            GlideLoader(requireContext()).loadImagePicture(venue.imageUrl, binding.selectedImage)
            binding.selectedVenueName.text = s.name
            binding.selectedVenueTown.text = s.description
        }
    }
}