package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.athompson.cafe.R
import com.athompson.cafe.adapters.MenuAdapter
import com.athompson.cafe.adapters.OrganisationAdapter
import com.athompson.cafe.adapters.VenueAdapter
import com.athompson.cafe.databinding.AddItemBinding
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.databinding.OrgListItemBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.models.Menu
import com.athompson.cafe.models.Organisation
import com.athompson.cafe.models.Venue
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val organisationAdapter = OrganisationAdapter()
    private val venueAdapter = VenueAdapter()
    private val menuAdapter = MenuAdapter()
    private val binding get() = _binding!!

    private var organisations: ArrayList<Organisation> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private var menus: ArrayList<Menu> = ArrayList()
    private var busyCount:Int = 0

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
        setupRecycler()

    }

    override fun onResume() {
        super.onResume()
        populate()
       // getOrganisationsList()
    }

    private fun getOrganisationsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        busyCount=0
        FireStoreClass().getOrganisationList(this@DashboardFragment)
        FireStoreClass().getVenueItemsList(this@DashboardFragment)
        FireStoreClass().getMenusList(this@DashboardFragment)
    }

    private fun setupRecycler()
    {
        binding.recyclerOrganisations.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerOrganisations.itemAnimator = DefaultItemAnimator()
        binding.recyclerOrganisations.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerOrganisations.adapter = organisationAdapter


        binding.recyclerVenues.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerVenues.itemAnimator = DefaultItemAnimator()
        binding.recyclerVenues.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerVenues.adapter = venueAdapter

    }

    private fun populate()
    {
        organisations.add(Organisation("","","MCDonalds","Fast Food","999 Letsby Avenue","Ratupadrainpipe","Walsall","a.t@t.com",1234567))
        organisations.add(Organisation("","","Burger King","Fast Food","999 Letsby Avenue","Ratupadrainpipe","Walsall","a.t@t.com",1234567))
        venues.add(Venue("","","MCDonalds","999 Letsby Avenue","Ratupadrainpipe","Walsall","a.t@t.com",1234567))
        venues.add(Venue("","","Burger King","998 Letsby Avenue","Ratupadrainpipe","Cannock","b.t@t.com",787987987))
        menus.add(Menu("","","Burger Sandwich","Big juicy sandwich"))
        organisationAdapter.dataChanged(organisations)
        venueAdapter.dataChanged(venues)
        menuAdapter.dataChanged(menus)
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successfulOrganisationsList(organisationsList: ArrayList<Organisation>) {
        hideProgressWhenComplete()

        if (organisationsList.size > 0) {
            organisations.clear()
            organisations.addAll(organisationsList)
            organisationAdapter.dataChanged(organisations)
            binding.recyclerOrganisations.show()
            binding.noOrganisationsView.remove()
        } else {
            binding.recyclerOrganisations.remove()
            binding.noOrganisationsView.show()
        }
    }

    fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {
        if (venuesList.size > 0) {
            venues.clear()
            venues.addAll(venuesList)
            venueAdapter.dataChanged(venues)
            binding.recyclerVenues.show()
            binding.noVenuesView.remove()
        } else {
            binding.recyclerVenues.remove()
            binding.noVenuesView.show()
        }
    }

    fun successfulMenuList(menuList: ArrayList<Menu>) {
        hideProgressWhenComplete()

        if (menuList.size > 0) {
            menus.clear()
            menus.addAll(menuList)
            menuAdapter.dataChanged(menus)
            binding.recyclerMenus.show()
            binding.noMenusView.remove()
        } else {
            binding.recyclerMenus.remove()
            binding.noMenusView.show()
        }
    }

    private fun hideProgressWhenComplete()
    {
        busyCount -= 1
        if(busyCount==0)
            hideProgressDialog()
    }


    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = OrgListItemBinding.bind(view)

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.org_list_item, parent, false)
                return ItemViewHolder(view)
            }
        }
    }

    class AddItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = AddItemBinding.bind(view)

        companion object {
            fun from(parent: ViewGroup): AddItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.add_item, parent, false)
                return AddItemViewHolder(view)
            }
        }
    }
}