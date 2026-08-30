package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TavanaDatabase
import com.example.data.model.BusinessEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.TavanaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TavanaUiState(
    val businesses: List<BusinessEntity> = emptyList(),
    val filteredBusinesses: List<BusinessEntity> = emptyList(),
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val searchQuery: String = "",
    val selectedCategory: String = "all",
    val showOnlyBookmarked: Boolean = false,
    val isAddModalOpen: Boolean = false,
    val isDisclaimerOpen: Boolean = false,
    val isLeagueInfoOpen: Boolean = false,
    val selectedBusiness: BusinessEntity? = null,
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false
)

class TavanaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TavanaDatabase.getDatabase(application)
    private val repository = TavanaRepository(database.businessDao(), database.userProfileDao())

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("all")
    private val _showOnlyBookmarked = MutableStateFlow(false)
    private val _isAddModalOpen = MutableStateFlow(false)
    private val _isLeagueInfoOpen = MutableStateFlow(false)
    private val _selectedBusiness = MutableStateFlow<BusinessEntity?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<TavanaUiState> = combine(
        repository.allBusinesses,
        repository.userProfile,
        _searchQuery,
        _selectedCategory,
        _showOnlyBookmarked,
        _isAddModalOpen,
        _isLeagueInfoOpen,
        _selectedBusiness,
        _snackbarMessage
    ) { args: Array<Any?> ->
        val businesses = (args[0] as? List<BusinessEntity>) ?: emptyList()
        val profile = args[1] as? UserProfileEntity
        val query = (args[2] as? String) ?: ""
        val category = (args[3] as? String) ?: "all"
        val bookmarkedOnly = (args[4] as? Boolean) ?: false
        val addOpen = (args[5] as? Boolean) ?: false
        val leagueOpen = (args[6] as? Boolean) ?: false
        val selectedBiz = args[7] as? BusinessEntity
        val snackbar = args[8] as? String

        val userProfile = profile ?: UserProfileEntity()
        val filtered = businesses.filter { item ->
            val matchesCategory = (category == "all" || item.category == category)
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.desc.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.contact.contains(query, ignoreCase = true)
            val matchesBookmark = !bookmarkedOnly || item.isBookmarked

            matchesCategory && matchesQuery && matchesBookmark
        }

        TavanaUiState(
            businesses = businesses,
            filteredBusinesses = filtered,
            userProfile = userProfile,
            searchQuery = query,
            selectedCategory = category,
            showOnlyBookmarked = bookmarkedOnly,
            isAddModalOpen = addOpen,
            isDisclaimerOpen = !userProfile.hasAcceptedDisclaimer,
            isLeagueInfoOpen = leagueOpen,
            selectedBusiness = selectedBiz,
            snackbarMessage = snackbar,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TavanaUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun toggleBookmarkFilter() {
        _showOnlyBookmarked.value = !_showOnlyBookmarked.value
    }

    fun setAddModalOpen(isOpen: Boolean) {
        _isAddModalOpen.value = isOpen
    }

    fun setLeagueInfoOpen(isOpen: Boolean) {
        _isLeagueInfoOpen.value = isOpen
    }

    fun selectBusiness(business: BusinessEntity?) {
        _selectedBusiness.value = business
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun acceptDisclaimer() {
        viewModelScope.launch {
            repository.acceptDisclaimer()
            _snackbarMessage.value = "به شهر مجازی توانا خوش آمدید!"
        }
    }

    fun addNewBusiness(
        name: String,
        category: String,
        desc: String,
        location: String,
        contact: String
    ) {
        viewModelScope.launch {
            val newEntity = BusinessEntity(
                id = "biz_${System.currentTimeMillis()}",
                name = name.trim(),
                category = category,
                desc = desc.trim(),
                location = location.trim().ifEmpty { "سراسر کشور" },
                contact = contact.trim(),
                rating = 5.0f,
                isVerified = true,
                createdAt = System.currentTimeMillis()
            )
            repository.addBusiness(newEntity)
            _isAddModalOpen.value = false
            _snackbarMessage.value = "کسب‌وکار با موفقیت ثبت شد (+۱۰۰ امتیاز مشارکت کسب کردید) 🎉"
        }
    }

    fun toggleBookmark(business: BusinessEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(business.id, business.isBookmarked)
            _snackbarMessage.value = if (!business.isBookmarked) "به نشان‌شده‌ها افزوده شد" else "از نشان‌شده‌ها حذف شد"
        }
    }

    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            val result = repository.applyReferralCode(code)
            result.onSuccess { msg ->
                _snackbarMessage.value = msg
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "خطا در ثبت کد معرف"
            }
        }
    }
}
