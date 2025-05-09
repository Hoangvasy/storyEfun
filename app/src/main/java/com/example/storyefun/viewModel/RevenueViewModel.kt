package com.example.storyefun.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.repository.RevenueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RevenueViewModel(
    private val repository: RevenueRepository
) : ViewModel() {
    private val _monthlyRevenue = MutableStateFlow<Double?>(null)
    val monthlyRevenue: StateFlow<Double?> = _monthlyRevenue.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    fun setYear(year: Int) {
        _selectedYear.value = year
    }

    fun setMonth(month: Int) {
        _selectedMonth.value = month
    }

    fun loadRevenue() {
        viewModelScope.launch {
            Log.d("RevenueViewModel", "Loading revenue for ${_selectedYear.value}-${_selectedMonth.value}")
            repository.getMonthlyRevenue(_selectedYear.value, _selectedMonth.value).onSuccess { revenue ->
                Log.d("RevenueViewModel", "Revenue loaded: $revenue")
                _monthlyRevenue.value = revenue
                _status.value = null
            }.onFailure { e ->
                Log.e("RevenueViewModel", "Error loading revenue: ${e.message}")
                _status.value = "Lỗi khi tải dữ liệu: ${e.message}"
            }
        }
    }
}