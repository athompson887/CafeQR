package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.athompson.cafe.R
import com.athompson.cafe.adapters.SimpleMenuAdapter
import com.athompson.cafe.adapters.VenuesViewPagerAdapter
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.firestore.FireStoreMenu
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.activities.AddMenuActivity
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.Venue


class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding
    private var cafeQrMenus: ArrayList<CafeQrMenu> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private lateinit var simpleMenuAdapter:SimpleMenuAdapter
    private lateinit var venuesViewPagerAdapter: VenuesViewPagerAdapter
    private var called = false
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
        binding = FragmentDashboardBinding.bind(view)
        binding.addMenu.setOnClickListener{
            startActivity(Intent(activity, AddMenuActivity::class.java))
        }
        binding.addMenuButton.setOnClickListener{
            startActivity(Intent(activity, AddMenuActivity::class.java))
        }
    //    binding.addVenue.setOnClickListener{
     //       startActivity(Intent(activity, AddVenuesActivity::class.java))
     //   }
     //   binding.addVenueButton.setOnClickListener{
     //       startActivity(Intent(activity, AddVenuesActivity::class.java))
       // }


        setupRecycler()
        if(!called) {
            called = true
            getVenuesList()
        }
    }


    private fun setupRecycler()
    {
        venuesViewPagerAdapter = VenuesViewPagerAdapter(requireContext(),venues,cafeQrMenus)
        simpleMenuAdapter = SimpleMenuAdapter(requireContext(),cafeQrMenus)

        binding.viewPager.adapter = venuesViewPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // override desired callback functions
        })


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


    private fun getVenuesList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreVenue().getVenueItemsList(::successVenuesList, ::failureVenueList)
    }

    private fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {
        if (venuesList.isNullOrEmpty()) {
            noVenue()
        }
        else
        {
            venues.clear()
            venues.addAll(venuesList)
        }
        getMenusList()
        hideProgressDialog()
    }

    private fun noVenue()
    {
        venues.clear()
        venues.add(Venue("fake_venue","","","",""))
        venuesViewPagerAdapter.dataChanged()
        hideProgressDialog()
    }

    private fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus",e)
        noVenue()
    }

    private fun getMenusList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreMenu().getMenuList(::successfulCafeQrMenuList,::failureCafeQrMenuList)
    }

    private fun successfulCafeQrMenuList(menuList: ArrayList<CafeQrMenu>) {
        if (menuList.size > 0) {
            cafeQrMenus.clear()
            cafeQrMenus.addAll(menuList)
            showMenu()
            venuesViewPagerAdapter.dataChanged()
        } else {
            showNoMenu()
        }
        simpleMenuAdapter.dataChanged(cafeQrMenus)
        hideProgressDialog()
    }
    private fun failureCafeQrMenuList(e: Exception) {
        hideProgressDialog()
        showNoMenu()
    }
    private fun showMenu()
    {
        binding.menusView.show()
        binding.noMenusView.remove()
    }
    private fun showNoMenu()
    {
        binding.menusView.remove()
        binding.noMenusView.show()
    }
}
