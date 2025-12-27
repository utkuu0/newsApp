package com.example.technews.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.technews.domain.model.Article
import com.example.technews.domain.repository.NewsRepository
import com.example.technews.ui.theme.GradientEnd
import com.example.technews.ui.theme.GradientMiddle
import com.example.technews.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ArticleDetailScreen(articleUrl: String, repository: NewsRepository, onBackClick: () -> Unit) {
    val context = LocalContext.current
    var article by remember { mutableStateOf<Article?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(articleUrl) {
        val decodedUrl = java.net.URLDecoder.decode(articleUrl, "UTF-8")
        article = repository.getArticleByUrl(decodedUrl)
        isLoading = false
        isContentVisible = true
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (isLoading) {
            LoadingState()
        } else if (article == null) {
            ErrorState()
        } else {
            article?.let { art ->
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    // Hero Image Section
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 12f)) {
                        // Article Image
                        art.urlToImage?.let { imageUrl ->
                            AsyncImage(
                                    model = imageUrl,
                                    contentDescription = art.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                            )
                        }
                                ?: Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                Brush.verticalGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        GradientStart,
                                                                                        GradientMiddle,
                                                                                        GradientEnd
                                                                                )
                                                                )
                                                        )
                                )

                        // Gradient Overlay
                        Box(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color.Black.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                                Color.Transparent,
                                                                                Color.Black.copy(
                                                                                        alpha = 0.7f
                                                                                )
                                                                        )
                                                        )
                                                )
                        )

                        // Top Bar
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .statusBarsPadding()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Back Button
                            IconButton(
                                    onClick = onBackClick,
                                    modifier =
                                            Modifier.size(44.dp)
                                                    .background(
                                                            Color.Black.copy(alpha = 0.3f),
                                                            CircleShape
                                                    )
                            ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = Color.White
                                )
                            }

                            Row {
                                IconButton(
                                        onClick = {
                                            val sendIntent =
                                                    Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(
                                                                Intent.EXTRA_TEXT,
                                                                "${art.title}\n\n${art.url}"
                                                        )
                                                        type = "text/plain"
                                                    }
                                            context.startActivity(
                                                    Intent.createChooser(sendIntent, "Paylaş")
                                            )
                                        },
                                        modifier =
                                                Modifier.size(44.dp)
                                                        .background(
                                                                Color.Black.copy(alpha = 0.3f),
                                                                CircleShape
                                                        )
                                ) {
                                    Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Paylaş",
                                            tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                        onClick = { /* TODO: Bookmark */},
                                        modifier =
                                                Modifier.size(44.dp)
                                                        .background(
                                                                Color.Black.copy(alpha = 0.3f),
                                                                CircleShape
                                                        )
                                ) {
                                    Icon(
                                            imageVector = Icons.Outlined.Bookmark,
                                            contentDescription = "Kaydet",
                                            tint = Color.White
                                    )
                                }
                            }
                        }

                        // Source Badge at Bottom
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                            Surface(shape = RoundedCornerShape(12.dp), color = GradientStart) {
                                Text(
                                        text = art.sourceName,
                                        style =
                                                MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                ),
                                        color = Color.White,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                )
                                )
                            }
                        }
                    }

                    // Content Card (overlapping the image)
                    Card(
                            modifier = Modifier.fillMaxWidth().offset(y = (-24).dp),
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                    ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        AnimatedVisibility(
                                visible = isContentVisible,
                                enter =
                                        fadeIn(tween(500)) +
                                                slideInVertically(
                                                        tween(500),
                                                        initialOffsetY = { it / 4 }
                                                )
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                // Title
                                Text(
                                        text = art.title,
                                        style =
                                                MaterialTheme.typography.headlineSmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Meta Info Row
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Author
                                    if (art.author.isNotBlank() && art.author != "Unknown") {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                    text = art.author,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color =
                                                            MaterialTheme.colorScheme
                                                                    .onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Date
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                                text = formatDate(art.publishedAt),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Divider
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(1.dp)
                                                        .background(
                                                                MaterialTheme.colorScheme.outline
                                                                        .copy(alpha = 0.2f)
                                                        )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Description
                                if (art.description.isNotBlank()) {
                                    Text(
                                            text = art.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color =
                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.9f
                                                    ),
                                            lineHeight =
                                                    MaterialTheme.typography.bodyLarge.lineHeight *
                                                            1.5f
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                }

                                // Content
                                if (art.content.isNotBlank()) {
                                    Text(
                                            text =
                                                    art.content.replace(
                                                            Regex("\\[\\+\\d+ chars\\]"),
                                                            ""
                                                    ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color =
                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.8f
                                                    ),
                                            lineHeight =
                                                    MaterialTheme.typography.bodyMedium.lineHeight *
                                                            1.5f
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Read More Button
                                Button(
                                        onClick = {
                                            val intent =
                                                    Intent(Intent.ACTION_VIEW, Uri.parse(art.url))
                                            context.startActivity(intent)
                                        },
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(56.dp)
                                                        .shadow(
                                                                elevation = 8.dp,
                                                                shape = RoundedCornerShape(16.dp),
                                                                spotColor =
                                                                        GradientStart.copy(
                                                                                alpha = 0.4f
                                                                        )
                                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color.Transparent
                                                ),
                                        contentPadding =
                                                androidx.compose.foundation.layout.PaddingValues(
                                                        0.dp
                                                )
                                ) {
                                    Box(
                                            modifier =
                                                    Modifier.fillMaxSize()
                                                            .background(
                                                                    Brush.horizontalGradient(
                                                                            colors =
                                                                                    listOf(
                                                                                            GradientStart,
                                                                                            GradientMiddle,
                                                                                            GradientEnd
                                                                                    )
                                                                    )
                                                            ),
                                            contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                    imageVector = Icons.Default.OpenInBrowser,
                                                    contentDescription = null,
                                                    tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                    text = "Haberin Tamamını Oku",
                                                    style =
                                                            MaterialTheme.typography.titleMedium
                                                                    .copy(
                                                                            fontWeight =
                                                                                    FontWeight
                                                                                            .SemiBold
                                                                    ),
                                                    color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                    color = GradientStart,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    text = "Haber yükleniyor...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "😕", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    text = "Haber bulunamadı",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Bu haber artık mevcut değil veya bir sorun oluştu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
