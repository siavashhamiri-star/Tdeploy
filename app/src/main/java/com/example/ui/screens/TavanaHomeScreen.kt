package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TavanaUiState
import com.example.ui.TavanaViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TavanaHomeScreen(
    viewModel: TavanaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Force RTL for Persian typography and natural Persian UX
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("tavana_home_screen"),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.setAddModalOpen(true) },
                    containerColor = Color(0xFF059669),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("fab_add_business")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "معرفی کسب‌وکار یا تخصص جدید",
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // 1. Hero Header & League Card
                item(key = "header_league_card") {
                    LeagueHeaderCard(
                        userProfile = uiState.userProfile,
                        onApplyReferral = { code -> viewModel.applyReferralCode(code) },
                        onOpenAddModal = { viewModel.setAddModalOpen(true) },
                        onOpenLeagueInfo = { viewModel.setLeagueInfoOpen(true) },
                        onShowMessage = { msg -> viewModel.showSnackbar(msg) }
                    )
                }

                // 2. Search Box
                item(key = "search_box") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = {
                                Text(
                                    text = "جستجوی شغل، تخصص، خدمات یا نام صنف...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "جستجو",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاک کردن",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_field"),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                // 3. Category Filter Chips
                item(key = "category_filters") {
                    CategoryFilterBar(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { viewModel.onCategorySelect(it) },
                        showOnlyBookmarked = uiState.showOnlyBookmarked,
                        onToggleBookmarkFilter = { viewModel.toggleBookmarkFilter() }
                    )
                }

                // 4. Section Header & Results count
                item(key = "results_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.showOnlyBookmarked) "نشان‌شده‌های شما" else "کسب‌وکارها و تخصص‌های برتر",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${uiState.filteredBusinesses.size} مورد",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 5. Business List or Empty State
                if (uiState.filteredBusinesses.isEmpty()) {
                    item(key = "empty_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (uiState.showOnlyBookmarked) "هنوز کسب‌وکاری را نشان نکرده‌اید" else "موردی با این مشخصات یافت نشد",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (uiState.showOnlyBookmarked) "با لمس علامت نشان در هر کارت، آن را در این بخش ذخیره کنید." else "می‌توانید اولین نفری باشید که این تخصص یا کسب‌وکار را معرفی می‌کند!",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                if (!uiState.showOnlyBookmarked) {
                                    Button(
                                        onClick = { viewModel.setAddModalOpen(true) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                    ) {
                                        Text("معرفی در این حوزه", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredBusinesses,
                        key = { it.id }
                    ) { business ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            BusinessCard(
                                business = business,
                                onClick = { viewModel.selectBusiness(business) },
                                onToggleBookmark = { viewModel.toggleBookmark(business) }
                            )
                        }
                    }
                }
            }

            // Modals and Sheets
            // 1. Mandatory Legal & Privacy Disclaimer Modal on First Launch
            DisclaimerModal(
                isOpen = uiState.isDisclaimerOpen,
                onAccept = { viewModel.acceptDisclaimer() }
            )

            // 2. Add Business Modal
            AddBusinessDialog(
                isOpen = uiState.isAddModalOpen,
                onClose = { viewModel.setAddModalOpen(false) },
                onSubmit = { name, category, desc, location, contact ->
                    viewModel.addNewBusiness(name, category, desc, location, contact)
                }
            )

            // 3. League Information Dialog
            LeagueInfoDialog(
                isOpen = uiState.isLeagueInfoOpen,
                onClose = { viewModel.setLeagueInfoOpen(false) }
            )

            // 4. Business Detail Sheet
            BusinessDetailSheet(
                business = uiState.selectedBusiness,
                onDismiss = { viewModel.selectBusiness(null) },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                onShowMessage = { viewModel.showSnackbar(it) }
            )
        }
    }
}
