# 📰 TechNews - Teknoloji Haberleri

Modern ve şık bir Android teknoloji haberleri uygulaması. Jetpack Compose ile geliştirilmiş, Clean Architecture prensiplerine uygun.

![Android](https://img.shields.io/badge/Android-26%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue?logo=jetpackcompose)

## ✨ Özellikler

- 🌐 **Güncel Haberler** - NewsAPI'den teknoloji haberlerini çeker
- 🔄 **Pull-to-Refresh** - Aşağı çekerek yenileme
- 🌙 **Karanlık Mod** - Göz yormayan karanlık tema desteği
- 📱 **Offline Desteği** - Room veritabanı ile haberler yerel olarak saklanır
- 🔔 **Günlük Bildirimler** - Uygulamayı açmadığınızda hatırlatma bildirimi
- 🎨 **Premium UI** - Gradient tasarım, animasyonlar, modern arayüz

## 📸 Ekran Görüntüleri

| Ana Sayfa | Haber Detayı | Ayarlar |
|:---------:|:------------:|:-------:|
| Featured + Trend Haberler | Hero Image + Gradient Buton | Toggle Animasyonları |

## 🏗️ Mimari

Proje **Clean Architecture** ve **MVVM** desenini takip eder:

```
app/
├── data/
│   ├── local/          # Room Database, DAO, Entity
│   ├── remote/         # Retrofit API Service, DTO
│   ├── preferences/    # DataStore Preferences
│   └── repository/     # Repository Implementation
├── di/                 # Hilt Dependency Injection
├── domain/
│   ├── model/          # Domain Models
│   └── repository/     # Repository Interface
├── presentation/
│   ├── detail/         # Article Detail Screen
│   ├── navigation/     # Navigation Graph
│   ├── newslist/       # News List Screen + ViewModel
│   └── settings/       # Settings Screen + ViewModel
├── ui/theme/           # Colors, Typography, Theme
└── worker/             # WorkManager + Notifications
```

## 🛠️ Teknoloji Stack

| Kategori | Teknoloji |
|----------|-----------|
| **UI** | Jetpack Compose, Material 3 |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit, OkHttp |
| **Local Database** | Room |
| **Async** | Kotlin Coroutines, Flow |
| **Background Work** | WorkManager |
| **Preferences** | DataStore |
| **Image Loading** | Coil |
| **Navigation** | Navigation Compose |

## 🚀 Kurulum

### Gereksinimler

- Android Studio Hedgehog (2023.1.1) veya üzeri
- JDK 11
- Android SDK 26+ (min) / 36 (target)

### Adımlar

1. **Repository'yi klonlayın:**
   ```bash
   git clone https://github.com/kullanici/technews.git
   cd technews
   ```

2. **API Key'i ayarlayın:**
   
   `app/build.gradle.kts` dosyasında NewsAPI key'i zaten tanımlı:
   ```kotlin
   buildConfigField("String", "NEWS_API_KEY", "\"YOUR_API_KEY\"")
   ```
   
   > 💡 Kendi API key'inizi [NewsAPI.org](https://newsapi.org/) adresinden alabilirsiniz.

3. **Projeyi çalıştırın:**
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.example.technews/.MainActivity
   ```

## 📁 Proje Yapısı

### Data Layer

```kotlin
// Entity - Room Database
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val description: String,
    // ...
)

// DTO - API Response
data class ArticleDto(
    val source: SourceDto,
    val title: String,
    // ...
)
```

### Domain Layer

```kotlin
// Domain Model - UI'da kullanılır
data class Article(
    val url: String,
    val sourceName: String,
    val title: String,
    // ...
)

// Repository Interface
interface NewsRepository {
    fun getNews(): Flow<List<Article>>
    suspend fun refreshNews()
}
```

### Presentation Layer

```kotlin
// ViewModel
@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    val state = repository.getNews()
        .map { NewsListState(articles = it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, NewsListState())
}

// Composable Screen
@Composable
fun NewsListScreen(
    onArticleClick: (String) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    // UI...
}
```

## 🎨 UI Tasarım

### Renk Paleti

| Renk | Hex | Kullanım |
|------|-----|----------|
| Primary | `#6366F1` | Ana renk (Indigo) |
| Gradient Middle | `#8B5CF6` | Gradient orta (Violet) |
| Gradient End | `#EC4899` | Gradient son (Pink) |
| Background Light | `#F8FAFC` | Açık tema arka plan |
| Background Dark | `#0F172A` | Koyu tema arka plan |

### Gradient Header

Premium görünüm için gradient header:
```kotlin
Box(
    modifier = Modifier
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    GradientStart,    // #6366F1
                    GradientMiddle,   // #8B5CF6
                    GradientEnd       // #EC4899
                )
            )
        )
)
```

## 🔔 Bildirimler

WorkManager ile günlük bildirim kontrolü:

```kotlin
class NewsCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val lastOpened = preferencesManager.getLastOpenedTime()
        val hoursSinceOpened = (System.currentTimeMillis() - lastOpened) / (1000 * 60 * 60)
        
        if (hoursSinceOpened >= 24) {
            NotificationHelper.showDailyReminder(applicationContext)
        }
        return Result.success()
    }
}
```

## 📝 Lisans

Bu proje MIT Lisansı altında lisanslanmıştır.

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'e push yapın (`git push origin feature/amazing-feature`)
5. Pull Request açın

---

<p align="center">
  Made with ❤️ using Kotlin & Jetpack Compose
</p>
