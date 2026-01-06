package com.example.technews.presentation.newslist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory
import com.example.technews.domain.model.toComposeColor
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
        onArticleClick: (String) -> Unit,
        onSettingsClick: () -> Unit,
        onSavedClick: () -> Unit,
        viewModel: NewsListViewModel = hiltViewModel()
) {
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // Animate category color
        val categoryColor by
                animateColorAsState(
                        targetValue = state.selectedCategory.primaryColor.toComposeColor(),
                        animationSpec = tween(500),
                        label = "categoryColor"
                )
        val gradientStart by
                animateColorAsState(
                        targetValue = state.selectedCategory.gradientStart.toComposeColor(),
                        animationSpec = tween(500),
                        label = "gradientStart"
                )
        val gradientEnd by
                animateColorAsState(
                        targetValue = state.selectedCategory.gradientEnd.toComposeColor(),
                        animationSpec = tween(500),
                        label = "gradientEnd"
                )

        LaunchedEffect(state.error) {
                state.error?.let { error ->
                        snackbarHostState.showSnackbar(error)
                        viewModel.clearError()
                }
        }

        Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
                PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { viewModel.refreshNews() },
                        modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                        LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                                // Premium Header with dynamic color
                                item {
                                        PremiumHeader(
                                                category = state.selectedCategory,
                                                gradientStart = gradientStart,
                                                gradientEnd = gradientEnd,
                                                onRefreshClick = { viewModel.refreshNews() },
                                                onSettingsClick = onSettingsClick,
                                                onSavedClick = onSavedClick
                                        )
                                }

                                // Category Chips
                                item {
                                        CategoryChips(
                                                categories = state.categories,
                                                selectedCategory = state.selectedCategory,
                                                onCategorySelected = {
                                                        viewModel.selectCategory(it)
                                                }
                                        )
                                }

                                // Loading or Content
                                if (state.isLoading && state.articles.isEmpty()) {
                                        item { LoadingState(categoryColor) }
                                } else if (state.articles.isEmpty()) {
                                        item { EmptyState(state.selectedCategory) }
                                } else {
                                        // Featured Article (First Article)
                                        if (state.articles.isNotEmpty()) {
                                                item {
                                                        FeaturedArticleCard(
                                                                article = state.articles.first(),
                                                                gradientStart = gradientStart,
                                                                onClick = {
                                                                        onArticleClick(
                                                                                state.articles
                                                                                        .first()
                                                                                        .url
                                                                        )
                                                                }
                                                        )
                                                }
                                        }

                                        // Horizontal Trending Cards
                                        if (state.articles.size > 1) {
                                                item {
                                                        SectionHeader(
                                                                title =
                                                                        "${state.selectedCategory.displayName} Haberleri",
                                                                color = categoryColor
                                                        )
                                                }

                                                item {
                                                        LazyRow(
                                                                contentPadding =
                                                                        PaddingValues(
                                                                                horizontal = 20.dp
                                                                        ),
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(16.dp)
                                                        ) {
                                                                items(
                                                                        items =
                                                                                state.articles
                                                                                        .drop(1)
                                                                                        .take(5),
                                                                        key = { it.url }
                                                                ) { article ->
                                                                        TrendingArticleCard(
                                                                                article = article,
                                                                                categoryColor =
                                                                                        categoryColor,
                                                                                onClick = {
                                                                                        onArticleClick(
                                                                                                article.url
                                                                                        )
                                                                                }
                                                                        )
                                                                }
                                                        }
                                                }
                                        }

                                        // Regular Article List
                                        if (state.articles.size > 6) {
                                                item {
                                                        SectionHeader(
                                                                title = "Daha Fazla",
                                                                color = categoryColor
                                                        )
                                                }

                                                itemsIndexed(
                                                        items = state.articles.drop(6),
                                                        key = { _, article -> article.url }
                                                ) { index, article ->
                                                        AnimatedVisibility(
                                                                visible = true,
                                                                enter =
                                                                        fadeIn(
                                                                                tween(
                                                                                        300,
                                                                                        delayMillis =
                                                                                                index *
                                                                                                        50
                                                                                )
                                                                        ) +
                                                                                slideInVertically(
                                                                                        tween(
                                                                                                300,
                                                                                                delayMillis =
                                                                                                        index *
                                                                                                                50
                                                                                        ),
                                                                                        initialOffsetY = {
                                                                                                it /
                                                                                                        2
                                                                                        }
                                                                                )
                                                        ) {
                                                                CompactArticleCard(
                                                                        article = article,
                                                                        categoryColor =
                                                                                categoryColor,
                                                                        onClick = {
                                                                                onArticleClick(
                                                                                        article.url
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun PremiumHeader(
        category: NewsCategory,
        gradientStart: Color,
        gradientEnd: Color,
        onRefreshClick: () -> Unit,
        onSettingsClick: () -> Unit,
        onSavedClick: () -> Unit
) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        Brush.verticalGradient(
                                                colors = listOf(gradientStart, gradientEnd)
                                        )
                                )
                                .statusBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
                Column {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Column {
                                        Text(
                                                text = category.displayName,
                                                style =
                                                        MaterialTheme.typography.headlineLarge.copy(
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                color = Color.White
                                        )
                                        Text(
                                                text = "Haberleri",
                                                style =
                                                        MaterialTheme.typography.headlineMedium
                                                                .copy(
                                                                        fontWeight =
                                                                                FontWeight.Light
                                                                ),
                                                color = Color.White.copy(alpha = 0.9f)
                                        )
                                }

                                Row {
                                        IconButton(
                                                onClick = onRefreshClick,
                                                modifier =
                                                        Modifier.size(44.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                        CircleShape
                                                                )
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = "Yenile",
                                                        tint = Color.White
                                                )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                                onClick = onSavedClick,
                                                modifier =
                                                        Modifier.size(44.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                        CircleShape
                                                                )
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Bookmark,
                                                        contentDescription = "Kaydedilenler",
                                                        tint = Color.White
                                                )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                                onClick = onSettingsClick,
                                                modifier =
                                                        Modifier.size(44.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                        CircleShape
                                                                )
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Settings,
                                                        contentDescription = "Ayarlar",
                                                        tint = Color.White
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "En güncel ${category.displayName.lowercase()} haberleri",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                        )
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChips(
        categories: List<NewsCategory>,
        selectedCategory: NewsCategory,
        onCategorySelected: (NewsCategory) -> Unit
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        val categoryColor = category.primaryColor.toComposeColor()

                        FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelected(category) },
                                label = {
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                                Icon(
                                                        imageVector = getCategoryIcon(category),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                        text = category.displayName,
                                                        fontWeight =
                                                                if (isSelected) FontWeight.SemiBold
                                                                else FontWeight.Normal
                                                )
                                        }
                                },
                                colors =
                                        FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = categoryColor,
                                                selectedLabelColor = Color.White,
                                                selectedLeadingIconColor = Color.White
                                        ),
                                border =
                                        FilterChipDefaults.filterChipBorder(
                                                borderColor = categoryColor.copy(alpha = 0.5f),
                                                selectedBorderColor = categoryColor,
                                                enabled = true,
                                                selected = isSelected
                                        )
                        )
                }
        }
}

@Composable
private fun getCategoryIcon(category: NewsCategory): ImageVector {
        return when (category) {
                NewsCategory.TECHNOLOGY -> Icons.Default.PhoneAndroid
                NewsCategory.SPORTS -> Icons.Default.SportsSoccer
                NewsCategory.BUSINESS -> Icons.Default.TrendingUp
                NewsCategory.ENTERTAINMENT -> Icons.Default.Movie
                NewsCategory.HEALTH -> Icons.Default.Favorite
                NewsCategory.SCIENCE -> Icons.Default.Science
                NewsCategory.GENERAL -> Icons.Default.Newspaper
        }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(4.dp, 20.dp)
                                        .background(color, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        text = title,
                        style =
                                MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                ),
                        color = MaterialTheme.colorScheme.onBackground
                )
        }
}

@Composable
private fun FeaturedArticleCard(article: Article, gradientStart: Color, onClick: () -> Unit) {
        val interactionSource = remember { MutableInteractionSource() }
        val scale by
                animateFloatAsState(targetValue = 1f, animationSpec = tween(300), label = "scale")

        Card(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .scale(scale)
                                .shadow(
                                        elevation = 16.dp,
                                        shape = RoundedCornerShape(24.dp),
                                        spotColor = gradientStart.copy(alpha = 0.3f)
                                )
                                .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        onClick = onClick
                                ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
                Box {
                        // Article Image
                        article.urlToImage?.let { imageUrl ->
                                AsyncImage(
                                        model = imageUrl,
                                        contentDescription = article.title,
                                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f),
                                        contentScale = ContentScale.Crop
                                )

                                // Gradient Overlay
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .aspectRatio(16f / 10f)
                                                        .background(
                                                                Brush.verticalGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        Color.Transparent,
                                                                                        Color.Black
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.7f
                                                                                                )
                                                                                ),
                                                                        startY = 100f
                                                                )
                                                        )
                                )
                        }

                        // Content Overlay
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                                // Source Badge
                                Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = gradientStart.copy(alpha = 0.9f)
                                ) {
                                        Text(
                                                text = article.sourceName,
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                fontWeight = FontWeight.SemiBold
                                                        ),
                                                color = Color.White,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 12.dp,
                                                                vertical = 6.dp
                                                        )
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                        text = article.title,
                                        style =
                                                MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = Color.White,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = formatDate(article.publishedAt),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f)
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun TrendingArticleCard(article: Article, categoryColor: Color, onClick: () -> Unit) {
        Card(
                modifier = Modifier.width(280.dp).clickable(onClick = onClick),
                shape = RoundedCornerShape(20.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
                Column {
                        // Article Image
                        Box {
                                AsyncImage(
                                        model = article.urlToImage,
                                        contentDescription = article.title,
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(140.dp)
                                                        .clip(
                                                                RoundedCornerShape(
                                                                        topStart = 20.dp,
                                                                        topEnd = 20.dp
                                                                )
                                                        ),
                                        contentScale = ContentScale.Crop
                                )

                                // Source Badge
                                Surface(
                                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = categoryColor.copy(alpha = 0.9f)
                                ) {
                                        Text(
                                                text = article.sourceName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 8.dp,
                                                                vertical = 4.dp
                                                        )
                                        )
                                }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                        text = article.title,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = formatDate(article.publishedAt),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun CompactArticleCard(article: Article, categoryColor: Color, onClick: () -> Unit) {
        Card(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Thumbnail
                        AsyncImage(
                                model = article.urlToImage,
                                contentDescription = article.title,
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = article.sourceName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = categoryColor,
                                        fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = article.title,
                                        style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Medium
                                                ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = formatDate(article.publishedAt),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun LoadingState(categoryColor: Color) {
        Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                                color = categoryColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = "Haberler yükleniyor...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                }
        }
}

@Composable
private fun EmptyState(category: NewsCategory) {
        Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                                imageVector = getCategoryIcon(category),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = "${category.displayName} haberi bulunamadı",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Yenilemek için aşağı çekin",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                }
        }
}

private fun formatDate(dateString: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("tr"))
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
                dateString
        }
}
