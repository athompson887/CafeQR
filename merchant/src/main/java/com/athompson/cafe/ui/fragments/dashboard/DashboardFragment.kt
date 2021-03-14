package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.athompson.cafe.R
import com.athompson.cafe.adapters.MenuAdapter
import com.athompson.cafe.adapters.OrganisationAdapter
import com.athompson.cafe.adapters.VenueAdapter
import com.athompson.cafe.databinding.AddItemBinding
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.databinding.OrgListItemBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.models.Menu
import com.athompson.cafelib.models.Organisation
import com.athompson.cafelib.models.Venue
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerHorizontal
import com.athompson.cafelib.extensions.ViewExtensions.show
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.lang.Exception


class DashboardFragment : BaseFragment() {

    private var selectedOrganisation: Organisation? = null
    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val venueAdapter = VenueAdapter()
    private val menuAdapter = MenuAdapter()
    private val binding get() = _binding!!

    private var organisations: ArrayList<Organisation> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private var menus: ArrayList<Menu> = ArrayList()
    private lateinit var organisationAdapter:OrganisationAdapter


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


    override fun onCreateOptionsMenu(menu: android.view.Menu, menuInflater: MenuInflater) {
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
        binding.noOrganisationsView.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_dashboard_to_organisationsFragment, null, null, null)
        }
        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
       // populate()
        getOrganisationsList()
    }



    private fun setupRecycler()
    {
        organisationAdapter = OrganisationAdapter(requireContext(),organisations,this)
        binding.recyclerOrganisations.setLayoutManagerHorizontal()
        binding.recyclerOrganisations.itemAnimator = DefaultItemAnimator()
        binding.recyclerOrganisations.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerOrganisations.adapter = organisationAdapter


        binding.recyclerVenues.setLayoutManagerHorizontal()
        binding.recyclerVenues.itemAnimator = DefaultItemAnimator()
        binding.recyclerVenues.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerVenues.adapter = venueAdapter

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOrganisationsList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreClass().getOrganisationList(this@DashboardFragment)
    }

    private fun getVenuesList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreClass().getVenueItemsList(this@DashboardFragment)
    }

    private fun getMenusList() {
        showProgressDialog(R.string.please_wait.toString())
        FireStoreClass().getMenusList(this@DashboardFragment)
    }
    //handle failures
    fun failureMenuList(e: Exception) {
        showShortToast("Failed To get menus",e)
        hideProgressDialog()
        binding.recyclerMenus.hide()
        binding.noMenusView.show()
    }

    fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus",e)
        getMenusList()
        binding.recyclerVenues.hide()
        binding.noVenuesView.show()
    }

    fun failureOrganisationList(e: Exception) {
        showShortToast("Failed To get organisations",e)
        getVenuesList()
        binding.recyclerOrganisations.hide()
        binding.noOrganisationsView.show()
    }

    //handle success
    fun successfulOrganisationsList(organisationsList: ArrayList<Organisation>) {
        getVenuesList()
        if (organisationsList.size > 0) {
            organisations.clear()
            organisations.addAll(organisationsList)
            binding.recyclerOrganisations.show()
            binding.noOrganisationsView.hide()
        } else {
            binding.recyclerOrganisations.hide()
            binding.noOrganisationsView.show()
        }
        organisationAdapter.dataChanged(organisations)
    }

    fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {
        getMenusList()
        if (venuesList.size > 0) {
            venues.clear()
            venues.addAll(venuesList)
            binding.recyclerVenues.show()
            binding.noVenuesView.hide()
        } else {
            binding.recyclerVenues.hide()
            binding.noVenuesView.show()
        }
        venueAdapter.dataChanged(venues)
    }

    fun successfulMenuList(menuList: ArrayList<Menu>) {
        hideProgressDialog()
        if (menuList.size > 0) {
            menus.clear()
            menus.addAll(menuList)
            binding.recyclerMenus.show()
            binding.noMenusView.hide()
        } else {
            binding.recyclerMenus.hide()
            binding.noMenusView.show()
        }
        menuAdapter.dataChanged(menus)
    }

    fun setSelectedOrganisation(org: Organisation) {
        selectedOrganisation = org
        val s = selectedOrganisation
        if(s!=null) {
            GlideLoader(requireContext()).loadImagePicture(org.imageUrl, binding.image)
            binding.name.text = s.name
            binding.type.text = s.type
        }
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