# تطبيق مصحف البلوثوث - Quran Bluetooth Player

تطبيق أندرويد يقوم بتشغيل سور من القرآن الكريم بشكل عشوائي تلقائياً عند ربط الموبايل مع نظام السيارة عبر البلوثوث.

## المميزات

- 🎵 **تشغيل عشوائي**: تشغيل سور القرآن الكريم بشكل عشوائي
- 📱 **بلوثوث تلقائي**: اكتشاف اقتران البلوثوث مع جهاز سيارة وبدء التشغيل تلقائياً
- 👤 **دعم مشايخ متعدد**: إمكانية إضافة أصوات مشايخ آخرين
- 📚 **مكتبة كاملة**: جميع سور القرآن الكريم (114 سورة)
- 🎨 **تصميم إسلامي**: واجهة مريحة بتصميم إسلامي عصري

## التقنيات المستخدمة

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room
- **Audio**: Media3 ExoPlayer
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## هيكل المشروع

```
QuranBluetoothPlayer/
├── app/src/main/
│   ├── java/com/app/quran/
│   │   ├── data/           # Repository, Database, Models
│   │   ├── domain/         # Use Cases, Interfaces
│   │   ├── presentation/   # ViewModels, Screens
│   │   ├── service/        # Bluetooth, Audio
│   │   └── di/             # Hilt Modules
│   ├── assets/
│   │   └── quran/          # القرآن MP3 (تحتاج تحميله)
│   └── res/                # Resources
└── build.gradle.kts
```

## التثبيت والبناء

### 1. المتطلبات
- Android Studio Hedgehog أو أحدث
- JDK 17+
- Android SDK 34

### 2. خطوات البناء

```bash
# نسخ المشروع
cd QuranBluetoothPlayer

# بناء المشروع
./gradlew assembleDebug

# أو عبر Android Studio
# File > Open > اختر مجلد المشروع
# Build > Make Project
```

### 3. تحميل ملفات القرآن

قم بتنزيل ملفات القرآن بصيغة MP3 وضعها في:
```
app/src/main/assets/quran/
```

بالتسمية:
- `surah_001.mp3` - الفاتحة
- `surah_002.mp3` - البقرة
- ...
- `surah_114.mp3` - الناس

يمكنك تحميلها من مواقع مثل:
- [Quran.com](https://quran.com/)
- [MP3Quran.net](https://mp3quran.net/)

## إضافة أصوات مشايخ آخرين

1. اذهب إلى قسم "المشايخ" في التطبيق
2. اضغط على "إضافة قارئ جديد"
3. أدخل اسم الشيخ والبلد ومسار الملفات

## إعدادات البلوثوث

التطبيق يكتشف تلقائياً أجهزة السيارة بناءً على الكلمات المفتاحية:
- car, vehicle, automobile
- toyota, honda, bmw, mercedes, audi, ford
- وغيرها...

يمكنك تعديل هذه الكلمات من قسم "الإعدادات".

## Permissions

```xml
<!-- Bluetooth -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

<!-- Location (required for Bluetooth scanning) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Audio Service -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

## الخطط المستقبلية

- [ ] Widget للشاشة الرئيسية
- [ ] إشعارات عند بدء/إيقاف السورة
- [ ] History للتشغيلات الأخيرة
- [ ] Sleep Timer
- [ ] دعم Android Auto

## المساهمة

المساهمات مرحب بها! يرجى فتح Issue أو Pull Request.

## الترخيص

MIT License