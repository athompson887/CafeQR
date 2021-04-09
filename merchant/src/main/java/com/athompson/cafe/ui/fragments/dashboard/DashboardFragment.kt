package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.viewpager2.widget.ViewPager2
import com.athompson.cafe.R
import com.athompson.cafe.adapters.SimpleMenuItemAdapter
import com.athompson.cafe.adapters.VenuesViewPagerAdapter
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.firestore.FireStoreCafeQrMenu
import com.athompson.cafe.firestore.FireStoreFoodMenuItem
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.activities.AddMenuActivity
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.models.Venue


class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding
    private var cafeQrMenus: ArrayList<CafeQrMenu?> = ArrayList()
    private var menuItems: ArrayList<FoodMenuItem?> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private var selectedMenu:CafeQrMenu? = null
    private lateinit var simpleMenuItemAdapter:SimpleMenuItemAdapter
    private lateinit var venuesViewPagerAdapter: VenuesViewPagerAdapter
    private var firstLoad = false
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

        FireStoreImage().listAllImages()
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
        hideMenus()
        setupRecycler()
        if(!firstLoad) {
            firstLoad = true
            getMenusList()
        }
    }


    private fun setupRecycler()
    {
        venuesViewPagerAdapter = VenuesViewPagerAdapter(requireContext(),venues,cafeQrMenus)
        simpleMenuItemAdapter = SimpleMenuItemAdapter(requireContext(),menuItems)

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
                selectedMenu = getSelectedMenu(position)
                renderSelectedMenu()
                getMenusItemsList(venues[position].selectedMenuId)
            }
        })


        binding.recyclerMenus.setLayoutManagerVertical()
        binding.recyclerMenus.itemAnimator = DefaultItemAnimator()
        binding.recyclerMenus.showVerticalDividers()
        binding.recyclerMenus.adapter = simpleMenuItemAdapter

    }

    private fun renderSelectedMenu() {
        binding.menuTitle.show()
        binding.menuTitle.text = selectedMenu?.name
        binding.menusWrapper.show()
    }


    private fun getVenuesList() {
        FireStoreVenue().getAll(::successVenuesList, ::failureVenueList)
    }

    private fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {

        if (venuesList.isNullOrEmpty()) {
            noVenue()
        }
        else
        {
            venues.clear()
            venues.addAll(venuesList)
            venuesViewPagerAdapter.dataChanged()
        }
        binding.menusWrapper.show()
    }

    private fun noVenue()
    {
        venues.clear()
        venues.add(Venue("fake_venue","","","",""))
        venuesViewPagerAdapter.dataChanged()
        binding.menusWrapper.show()
    }

    private fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus",e)
        noVenue()
    }

    private fun getMenusList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreCafeQrMenu().getAll(::successfulCafeQrMenuList,::failureCafeQrMenuList)
    }

    private fun successfulCafeQrMenuList(menuList: ArrayList<CafeQrMenu?>) {
        if (menuList.size > 0) {
            cafeQrMenus.clear()
            cafeQrMenus.addAll(menuList)
        }
            getVenuesList()
    }

    private fun failureCafeQrMenuList(e: Exception) {
        getVenuesList()
        showNoMenu()
        logError(e.message.toString())
    }

    fun getSelectedMenu(position: Int):CafeQrMenu?
    {
         val currentVenue = venuesViewPagerAdapter.itemAt(position)
         cafeQrMenus.forEach {
            if(it?.id.safe().isNotBlank()&&it?.id==currentVenue?.selectedMenuId)
            {
                return  it
            }
         }
        return null
    }

    private fun getMenusItemsList(selectedMenuId:String) {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreFoodMenuItem().getAll(selectedMenuId,::successfulMenuItemsList,::failureMenuItemsList)
    }

    private fun successfulMenuItemsList(menuList: ArrayList<FoodMenuItem?>) {
        if (menuList.size > 0) {
            menuItems.clear()
            menuItems.addAll(menuList)
            showMenu()
        } else {
            showNoMenu()
        }
        hideProgressDialog()
        simpleMenuItemAdapter.dataChanged(menuItems)
    }

    private fun failureMenuItemsList(e: Exception) {
        hideProgressDialog()
        showNoMenu()
        logError(e.message.toString())
    }
    private fun showMenu()
    {
        binding.menusView.show()
        binding.noMenusView.remove()
    }
    private fun showNoMenu()
    {
        binding.menuTitle.hide()
        binding.menusView.remove()
        binding.noMenusView.show()
    }
    private fun hideMenus()
    {
        binding.menusWrapper.hide()
        binding.menuTitle.hide()
    }
}
