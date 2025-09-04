package com.example.expensetrackerapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.data.model.Category
import com.example.expensetrackerapp.data.model.Transaction
import com.example.expensetrackerapp.data.model.TransactionType
import com.example.expensetrackerapp.databinding.FragmentAddTransactionDialogBinding
import com.example.expensetrackerapp.ui.viewModel.FinanceViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AddTransactionDialog : DialogFragment() {
    private var _binding: FragmentAddTransactionDialogBinding?=null
    private val binding get()= _binding!!

    private lateinit var viewModel: FinanceViewModel
    private var currentType = TransactionType.INCOME
    private var categories = listOf<Category>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]

        setupTypeSelection()
        setupCategorySpinner()
        setupButton()
        observeCategories()
    }

    private fun setupTypeSelection() {
        // Setup tab selection for Income/Expense
        binding.typeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentType = when (tab?.position) {
                    0 -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                // Update categories based on selected type
                observeCategories()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun observeCategories() {
        // Observe categories based on selected type
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCategoriesByType(currentType).collect { newCategories ->
                categories = newCategories
                updateCategorySpinner()
            }
        }
    }
    private fun updateCategorySpinner() {
        // Update the category spinner with the available categories
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories.map { it.name }
        )
        binding.spinnerCategory.setAdapter(adapter)
    }
    private fun setupCategorySpinner() {
        // Setup the category spinner
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding.spinnerCategory.setAdapter(adapter)
    }
    private fun setupButton() {
        // Handle save button click
        binding.btnSave.setOnClickListener {
            // Validate and save transactions
            if(validateInput()) {
                saveTransaction()
                dismiss()
            }
        }

        // Handle cancel button click
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    private fun validateInput(): Boolean {

        // Validate them all
        val amount = binding.etAmount.text.toString()
        if (amount.isEmpty() || amount.toDoubleOrNull() == null) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            return false
        }

        if(binding.etDescription.text.toString().isEmpty()) {
            binding.etDescription.error = getString(R.string.error_empty_description)
            return false
        }

        if(binding.spinnerCategory.text.toString().isEmpty()) {
            binding.spinnerCategory.error = getString(R.string.error_category_required)
            return false
        }
        return true
    }
    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().toDouble()
        val description = binding.etDescription.text.toString()
        val category = binding.spinnerCategory.text.toString()

        val transaction = Transaction(
            amount=amount,
            description=description,
            category=category,
            type=currentType,
            date=System.currentTimeMillis()
        )
        viewModel.addTransaction(transaction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}