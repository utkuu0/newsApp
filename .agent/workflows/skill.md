---
description: Android projesi için hızlı build, clean ve run komutları (otomatik çalışır)
---

// turbo-all

## Build
Projeyi derlemek için:
```
./gradlew assembleDebug
```

## Clean
Build cache ve çıktıları temizlemek için:
```
./gradlew clean
```

## Run
Bağlı cihazda/emülatörde çalıştırmak için:

1. Cihazları listele:
```
adb devices
```

2. Debug APK'yı yükle:
```
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

3. Uygulamayı başlat:
```
adb shell am start -n com.example.technews/.MainActivity
```

## Full Build & Run
Temizle, derle ve çalıştır:
```
./gradlew clean assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.example.technews/.MainActivity
```
