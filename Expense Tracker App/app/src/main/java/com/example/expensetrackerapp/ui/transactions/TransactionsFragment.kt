package com.example.expensetrackerapp.ui.transactions

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerapp.data.model.Transaction
import com.example.expensetrackerapp.data.model.TransactionFilter
import com.example.expensetrackerapp.databinding.FragmentTransactionsBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionsViewModel by viewModels()
    private lateinit var categoryAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        observeTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                // Handle transaction click (e.g., Show details or edit. )
                showTransactionDetails(transaction)
            },
            onDeleteClick = { transaction ->
                viewModel.deleteTransaction(transaction)
            }
        )

        binding.recyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupTabLayout() {
        // Handle tap selection to filter transactions.
        binding.transactionTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> viewModel.setTransactionFilter(TransactionFilter.ALL)
                        1 -> viewModel.setTransactionFilter(TransactionFilter.INCOME)
                        2 -> viewModel.setTransactionFilter(TransactionFilter.EXPENSE)
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }
    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tranactions.collect { transactions ->
                transactionAdapter.submitList(transactions)
            }
        }
    }
    private fun showTransactionDetails(transaction: Transaction) {
        // Navigate to Transaction Details Or show dialogue.
        // Implementation depends on app's navigation requirements.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }
}