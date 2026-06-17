# تطبيق تشغيل القرآن الكريم تلقائياً عبر البلوثوث - المواصفات الكاملة

## 1. الرؤية والمفهوم

تطبيق يُشغّل سوراً من القرآن الكريم بشكل عشوائي تلقائياً عند ربط الموبايل مع نظام السيارة عبر البلوثوث. الهدف هو توفير تجربة روحانية خفيفة دون تدخل يدوي - بمجرد دخولك سيارتك والاقتران بالبلوثوث، يبدأ تشغيل القرآن بصوت الشيخ مصطفى الفرجاني أو أي شيخ آخر تختاره.

التجربة: **"صمتٍ هادئ في الطريق"**

---

## 2. لغة التصميم

### الاتجاه الجمالي
تصميم إسلامي عصري - خطوط هادئة، ألوان مريحة للعين، زخارف بسيطة مستوحاة من الخط العربي. ليس تصميم مذهل أو فخم، بل هادئ ومريح مثل المسجد.

### لوحة الألوان

#### الوضع الفاتح (Light)
```
Primary:    #1B4D3E (أخضر إسلامي داكن)
Secondary:  #D4AF37 (ذهبي دافئ)
Background: #F7F5F0 (كريمي دافئ)
Surface:    #FFFFFF (أبيض)
Text:       #2C3E50 (رمادي داكن)
TextMuted:  #7F8C8D (رمادي متوسط)
Accent:     #1ABC9C (تركواز)
```

#### الوضع الداكن (Dark)
```
Primary:    #2A7A5F (أخضر فاتح)
Secondary:  #E6C555 (ذهبي فاتح)
Background: #1A1A1A (رمادي داكن)
Surface:    #2D2D2D (رمادي متوسط)
Text:       #FFFFFF (أبيض)
TextMuted:  #B0B0B0 (رمادي فاتح)
Accent:     #48D6AD (تركواز فاتح)
```

#### الوضع الأخضر (Islamic Green)
```
Primary:    #00A86B (أخضر إسلامي نقي)
Secondary:  #FFD700 (ذهبي)
Background: #004D40 (أخضر داكن جداً)
Surface:    #00695C (أخضر متوسط)
Text:       #E8F5E9 (أخضر فاتح جداً)
TextMuted:  #80CBC4 (تركواز)
Accent:     #69F0AE (أخضر لامع)
```

#### الوضع الذهبي (Royal Gold)
```
Primary:    #B8860B (ذهبي داكن)
Secondary:  #FFD700 (ذهبي لامع)
Background: #2C2416 (بني داكن)
Surface:    #3D3522 (بني متوسط)
Text:       #FFF8E1 (كريمي فاتح)
TextMuted:  #D4AF37 (ذهبي)
Accent:     #FFC107 (ذهبي لامع)
```

### الخطوط
- **العناوين:** Tajawal Bold (Google Fonts)
- **النصوص:** Tajawal Regular
- **الأرقام:** Roboto Mono
- Fallback: sans-serif

### نظام المسافات
- وحدة أساسية: 8dp
- Padding خارجي: 16dp
- Padding داخلي: 12dp
- Border radius: 12dp للكروت، 24dp للأزرار الكبيرة

### فلسفة الحركة
- انتقالات سلسة وبطيئة (300ms ease-out)
- تأثير تلاشي عند بدء/إيقاف السورة
- Animation للنقل بين السور (crossfade 400ms)
- تأثير نبض (pulse) عند تشغيل السورة
- Slide animation لقائمة السور

---

## 3. الهيكل والتخطيط

### الشاشات الرئيسية

```
┌─────────────────────────────────────┐
│          شاشة التشغيل الرئيسية       │
│  ┌───────────────────────────────┐  │
│  │      لوجو التطبيق + اسم       │  │
│  │      حالة البلوثوث (on/off)   │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │   أيقونة الشيخ المحدد         │  │
│  │   اسم الشيخ + اسم السورة     │  │
│  │   [شريط التقدم]              │  │
│  │   [◀◀] [▶/⏸] [▶▶]           │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │  🎵 قائمة السور المحملة        │  │
│  │  🔀 وضع عشوائي (on/off)       │  │
│  │  ⏰ Sleep Timer               │  │
│  │  🔖 العلامات المحفوظة         │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │  👤 اختيار الشيخ              │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │  📁 إدارة التنزيلات           │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

### التنقل
- **Bottom Navigation** مع 5 أقسام:
  1. الرئيسية (الآن)
  2. المكتبة (السور)
  3. المشايخ
  4. العلامات (Bookmarks)
  5. الإعدادات

---

## 4. الميزات والتفاعلات

### 4.1 اكتشاف البلوثوث التلقائي (Core Feature)

**السلوك:**
- عند اكتشاف اقتران بلوثوث مع جهاز سيارة → يبدأ التشغيل تلقائياً
- عند فصل الاتصال → إيقاف مؤقت (resume عند إعادة الاتصال)
- يمكن تعطيل هذا السلوك من الإعدادات

**قائمة الكلمات المفتاحية للسيارات (50+):**
```kotlin
val CAR_KEYWORDS = setOf(
    // Generic
    "car", "vehicle", "automobile", "auto", "cars",
    // Brands
    "toyota", "honda", "bmw", "mercedes", "audi", "ford",
    "chevrolet", "gmc", "nissan", "hyundai", "kia", "lexus",
    "porsche", "volvo", "tesla", "jaguar", "land rover",
    "vw", "volkswagen", "mazda", "subaru", "suzuki",
    "mitsubishi", "jeep", "dodge", "chrysler", "buick",
    "cadillac", "lincoln", "infiniti", "acura", "genesis",
    // Car Systems
    "carduo", "car multimedia", "car audio", "car kit",
    "carplay", "android auto", "car host", "car mode",
    "handsfree", "hc-", "parrot", "carkit", "m otorola",
    // Additional
    "carl", "carla", "vehicle headunit", "car stereo",
    "car nav", "car gps", "car entertainment"
)
```

**التحسينات:**
- حفظ آخر جهاز سيارة تم الاتصال به
- Auto-Resume من نفس المكان
- فلترة ذكية حسب نوع الجهاز (A2DP, HSP)

### 4.2 مشغل الصوت العشوائي

**السلوك:**
- عند بدء التشغيل: اختيار سورة عشوائية
- بعد انتهاء السورة: اختيار سورة جديدة عشوائية (بدون تكرار متتالي)
- يمكن تفعيل/إلغاء الوضع العشوائي

### 4.3 Sleep Timer

**الخيارات:**
- 15 دقيقة
- 30 دقيقة
- 45 دقيقة
- 60 دقيقة
- 90 دقيقة
- بدون نهاية

**السلوك:**
- عرض العد التنازلي في المشغل
- إشعار قبل 5 دقائق من الإيقاف
- Fade out للصوت تدريجياً

### 4.4 Bookmarks / العلامات

**المميزات:**
- حفظ المواضع المفضلة (سورة + ثانية)
- إضافة ملاحظات
- تنظيم حسب الشيخ
- تصدير/استيراد

### 4.5 Repeat Ayah (تكرار آية)

**السلوك:**
- تمكين التكرار عند نقطة معينة
- عدد التكرارات (1، 3، 5، ∞)
- Fade بين التكرارات

### 4.6 مكتبة القرآن الكريم

**السور:** 114 سورة كاملة بصوت الشيخ مصطفى الفرجاني

**الفئات:**
- جميع السور
- السور المكية (86 سورة)
- السور المدنية (28 سورة)
- أجزاء محددة (الأجزاء 30)
- حسب التصنيف (عمري، قصار، متوسطات، طويلة)

**التحسينات:**
- Download Manager مع progress
- Offline Mode
- البحث السريع
- Sort (رقم، اسم، مدة)

### 4.7 Download Manager

**المميزات:**
- تحميل جميع السور أو انتقائي
- Progress لكل سورة
- Resume بعد الانقطاع
- WiFi only option
- حذف الملفات غير المستخدمة

### 4.8 Backup / Restore

**المميزات:**
- نسخ الإعدادات والعلامات
- تصدير لقائمة Google
- استيراد من ملف

### 4.9 Widget للشاشة الرئيسية

**الحجم:** 4x2

**المحتوى:**
- اسم السورة الحالية
- زر Play/Pause
- زر التالي
- اسم الشيخ
- Progress bar

### 4.10 إشعارات ذكية

**المحتوى:**
- اسم السورة + اسم الشيخ
- أزرار (السابق، تشغيل، التالي)
- Sleep Timer countdown
- Progress

---

## 5. مخزون المكونات

### مشغل الصوت (AudioPlayerView)
```
┌─────────────────────────────────────┐
│  [cover_art]  اسم الشيخ              │
│               سورة الفاتحة           │
│               00:00 ━━━━━━●━━━━ 03:00│
│                                     │
│          [⏮️] [⏯️] [⏭️]            │
│                                     │
│  🔀 عشوائي  🔁 تكرار  ⏰ تايمر  🔖   │
└─────────────────────────────────────┘
```

### قائمة السور (SurahListView)
```
┌─────────────────────────────────────┐
│ 🔍 بحث...                          │
│ [الكل] [مكية] [مدنية] [محملة]      │
├─────────────────────────────────────┤
│ 001  الفاتحة    00:00  📥  [▶️]   │
│ 002  البقرة     02:45  ✅  [▶️]   │
│ 003  آل عمران  02:30  ⏳  [▶️]   │
│ ...                                 │
└─────────────────────────────────────┘
```

### بطاقة الشيخ (ReaderCardView)
```
┌─────────────────────────────────────┐
│  [img]  الشيخ مصطفى الفرجاني        │
│         🇸🇦 المملكة العربية السعودية │
│                                      │
│  [🎵 114 سورة]    [📁 1.2 GB]      │
│  [📥 محمل: 45/114]                  │
│                                      │
│         [تحديد كقارئ افتراضي]       │
└─────────────────────────────────────┘
```

### Widget (WidgetView)
```
┌─────────────────────────────┐
│ 📖 مصحف البلوثوث           │
│                             │
│ الفاتحة - الشيخ مصطفى      │
│ [━━━━━●━━━━━] 01:23/03:00  │
│                             │
│   [⏮️]  [▶️]  [⏭️]         │
└─────────────────────────────┘
```

### Sleep Timer Dialog
```
┌─────────────────────────────────────┐
│           ⏰ مؤقت النوم            │
├─────────────────────────────────────┤
│  ○ 15 دقيقة                        │
│  ○ 30 دقيقة                        │
│  ○ 45 دقيقة                        │
│  ○ 60 دقيقة                        │
│  ○ 90 دقيقة                        │
│  ○ بدون نهاية                      │
├─────────────────────────────────────┤
│  [إلغاء]              [تطبيق]       │
└─────────────────────────────────────┘
```

### Bookmark Item
```
┌─────────────────────────────────────┐
│ 📖 سورة الفاتحة                    │
│  ⏱️ 01:23 - الجزء 1              │
│  📝 ملاحظة:最喜欢的开头           │
│                                     │
│  الشيخ: مصطفى الفرجاني             │
│  📅 2024-01-15                     │
│                                     │
│  [▶️ تشغيل] [✏️ تعديل] [🗑️ حذف]   │
└─────────────────────────────────────┘
```

---

## 6. Database Schema (محدث)

### Entities

```kotlin
// Surah (موجود)
@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameArabic: String,
    val readerId: Int,
    val audioPath: String,
    val durationMs: Long,
    val type: String, // "meccan" or "medinan"
    val versesCount: Int
)

// Reader (موجود)
@Entity(tableName = "readers")
data class ReaderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val country: String,
    val description: String,
    val audioPathPrefix: String,
    val isDefault: Boolean
)

// NEW: Bookmark
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val surahId: Int,
    val positionMs: Long,
    val note: String,
    val createdAt: Long,
    val readerId: Int
)

// NEW: Download Status
@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val surahId: Int,
    val readerId: Int,
    val status: String, // "pending", "downloading", "completed", "failed"
    val progress: Int, // 0-100
    val localPath: String?,
    val fileSize: Long,
    val lastAttempt: Long
)

// NEW: Play History
@Entity(tableName = "play_history")
data class PlayHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val surahId: Int,
    val readerId: Int,
    val playedAt: Long,
    val durationListened: Long,
    val completed: Boolean
)

// NEW: App Settings
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
```

---

## 7. الخدمات (Services)

### BluetoothCarReceiver
```kotlin
class BluetoothCarReceiver : BroadcastReceiver() {
    // Enhanced with 50+ car keywords
    // Auto-save last car device
    // Auto-resume support
}
```

### QuranPlayerService
```kotlin
class QuranPlayerService : Service() {
    // Sleep Timer support
    // Bookmark on position
    // Repeat Ayah mode
    // Media Session integration
}
```

### DownloadService
```kotlin
class DownloadService : Service() {
    // Background download
    // Progress notification
    // Resume support
    // WiFi only option
}
```

### WidgetProvider
```kotlin
class QuranWidgetProvider : AppWidgetProvider() {
    // 4x2 widget
    // Play/Pause/Next controls
    // Progress display
    // Quick actions
}
```

---

## 8. الحالات والحدود

### حالات التطبيق
- **Idle:** لا يوجد اتصال بلوثوث، مشغل متوقف
- **Connecting:** تم اكتشاف جهاز سيارة، جاري الاتصال
- **Playing:** تشغيل السور بشكل عشوائي
- **Paused:** فصل البلوثوث، إيقاف مؤقت
- **Downloading:** جاري تحميل السور
- **Error:** خطأ في تحميل السورة أو البلوثوث

### حالات التنزيل
- **Pending:** في انتظار التنزيل
- **Downloading:** جاري التنزيل (مع Progress)
- **Completed:** تم التنزيل
- **Failed:** فشل التنزيل
- **Paused:** إيقاف مؤقت

---

## 9. المستقبل (Phase 2)

- Android Auto integration
- Equalizer للتحكم بالصوت
- Voice commands ("شغل سورة البقرة")
- Cloud sync
- Widget themes
- Apple CarPlay