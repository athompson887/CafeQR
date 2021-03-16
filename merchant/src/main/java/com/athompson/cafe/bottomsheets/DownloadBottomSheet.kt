package com.athompson.cafe.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.athompson.cafe.databinding.FragmentItemListDialogListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val ARG_ITEM_COUNT = "item_count"

class DownloadBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    activity?.findViewById<RecyclerView>(R.id.list)?.layoutManager =
     //       LinearLayoutManager(context)
       // activity?.findViewById<RecyclerView>(R.id.list)?.adapter =
        //    arguments?.getInt(ARG_ITEM_COUNT)?.let { ItemAdapter(it) }
    }



    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): DownloadBottomSheet =
            DownloadBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}