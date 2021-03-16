package com.athompson.cafe.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.athompson.cafe.R
import com.athompson.cafe.adapters.OrganisationAdapter
import com.athompson.cafe.adapters.VenueAdapter
import com.athompson.cafe.databinding.AddItemBinding
import com.athompson.cafe.databinding.FragmentDashboardBinding
import com.athompson.cafe.databinding.OrgListItemBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.ui.activities.AddOrganisationActivity
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerHorizontal
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.models.Organisation
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication.Companion.selectedOrganisation
import com.athompson.cafelib.shared.CafeQRApplication.Companion.selectedVenue
import kotlinx.android.synthetic.main.add_item.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var organisations: ArrayList<Organisation> = ArrayList()
    private var venues: ArrayList<Venue> = ArrayList()
    private lateinit var organisationAdapter:OrganisationAdapter
    private lateinit var venueAdapter:VenueAdapter

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
        binding.addOrg.setOnClickListener{
            startActivity(Intent(activity, AddOrganisationActivity::class.java))
        }
        binding.addVenue.setOnClickListener{
            startActivity(Intent(activity, AddVenuesActivity::class.java))
        }
        binding.printCodes.setOnClickListener{
            showShortToast("clicked")
        }
        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
        getOrganisationsList()
    }



    private fun setupRecycler()
    {
        venueAdapter = VenueAdapter(requireContext(),venues,this)
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

    fun failureVenueList(e: Exception) {
        showShortToast("Failed To get venus",e)
        binding.recyclerVenues.hide()
        binding.noVenuesView.show()
        hideProgressDialog()
    }

    fun failureOrganisationList(e: Exception) {
        showShortToast("Failed To get organisations",e)
        binding.recyclerOrganisations.hide()
        binding.noOrganisationsView.show()
        hideProgressDialog()
    }

    //handle success
    fun successfulOrganisationsList(organisationsList: ArrayList<Organisation>) {
        if (organisationsList.size > 0) {
            organisations.clear()
            organisations.addAll(organisationsList)
            setSelectedOrganisation(organisations[0])
            binding.recyclerOrganisations.show()
            binding.noOrganisationsView.hide()
        } else {
            binding.recyclerOrganisations.hide()
            binding.noOrganisationsView.show()
            hideProgressDialog()
        }
        organisationAdapter.dataChanged(organisations)
    }

    fun successVenuesList(venuesList: java.util.ArrayList<Venue>) {
        if (venuesList.size > 0) {
            venues.clear()
            venues.addAll(venuesList)
            setSelectedVenue(venues[0])
            binding.recyclerVenues.show()
            binding.noVenuesView.hide()
        } else {
            binding.recyclerVenues.hide()
            binding.noVenuesView.show()
        }
        venueAdapter.dataChanged(venues)
        hideProgressDialog()
    }


    fun setSelectedOrganisation(org: Organisation) {
        selectedOrganisation = org
        val s = selectedOrganisation
        if(s!=null) {
            GlideLoader(requireContext()).loadImagePicture(org.imageUrl, binding.image)
            binding.orgName.text = s.name
            binding.orgType.text = s.type
        }
        getVenuesList()
    }

    fun setSelectedVenue(venue: Venue) {
        selectedVenue = venue
        val s = selectedVenue
        if(s!=null) {
            GlideLoader(requireContext()).loadImagePicture(venue.imageUrl, binding.venueImage)
            binding.venueTown.text = s.name
            binding.venueShortAddress.text = s.city
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