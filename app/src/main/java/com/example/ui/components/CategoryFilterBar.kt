package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryList

@Composable
fun CategoryFilterBar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    showOnlyBookmarked: Boolean,
    onToggleBookmarkFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bookmarked quick filter button
        FilterChip(
            selected = showOnlyBookmarked,
            onClick = onToggleBookmarkFilter,
            label = {
                Text(
                    text = "نشان‌شده‌ها",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (showOnlyBookmarked) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (showOnlyBookmarked) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFFEF3C7),
                selectedLabelColor = Color(0xFF92400E)
            ),
            modifier = Modifier.testTag("bookmark_filter_chip")
        )

        // Categories
        CategoryList.ALL_CATEGORIES.forEach { category ->
            val isSelected = category.id == selectedCategory && !showOnlyBookmarked
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (showOnlyBookmarked) {
                        onToggleBookmarkFilter()
                    }
                    onCategorySelected(category.id)
                },
                label = {
                    Text(
                        text = "${category.emoji} ${category.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("category_chip_${category.id}")
            )
        }
    }
}
