package com.app.quran.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.quran.data.local.db.dao.BookmarkDao
import com.app.quran.data.local.db.dao.DownloadDao
import com.app.quran.data.local.db.dao.PlayHistoryDao
import com.app.quran.data.local.db.dao.ReaderDao
import com.app.quran.data.local.db.dao.SurahDao
import com.app.quran.data.local.db.entity.BookmarkEntity
import com.app.quran.data.local.db.entity.DownloadEntity
import com.app.quran.data.local.db.entity.DownloadStatus
import com.app.quran.data.local.db.entity.PlayHistoryEntity
import com.app.quran.data.local.db.entity.ReaderEntity
import com.app.quran.data.local.db.entity.SurahEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SurahEntity::class,
        ReaderEntity::class,
        BookmarkEntity::class,
        DownloadEntity::class,
        PlayHistoryEntity::class
    ],
    version = 2, // Incremented for new entities
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun surahDao(): SurahDao
    abstract fun readerDao(): ReaderDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun downloadDao(): DownloadDao
    abstract fun playHistoryDao(): PlayHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quran_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val readerDao = database.readerDao()
            val surahDao = database.surahDao()

            // Insert default reader - Mustafa Al-Farjani
            val defaultReader = ReaderEntity(
                id = 1,
                name = "الشيخ مصطفى الفرجاني",
                nameArabic = "الشيخ مصطفى الفرجاني",
                country = "مصر",
                description = "قرآن كريم بصوت الشيخ مصطفى الفرجاني",
                audioPathPrefix = "mustafa_alfarjani/",
                isDefault = true
            )
            readerDao.insertReader(defaultReader)

            // Insert all 114 surahs
            val surahs = createSurahList()
            surahDao.insertSurahs(surahs)

            // Initialize download entries for all surahs
            val downloadDao = database.downloadDao()
            val downloads = surahs.map { surah ->
                DownloadEntity(
                    surahId = surah.id,
                    readerId = 1,
                    status = DownloadStatus.PENDING.name,
                    progress = 0,
                    localPath = null,
                    fileSize = 0L,
                    lastAttempt = 0L
                )
            }
            downloadDao.insertDownloads(downloads)
        }

        private fun createSurahList(): List<SurahEntity> {
            return listOf(
                SurahEntity(1, "Al-Fatiha", "الفاتحة", 1, "quran/surah_001.mp3", 180000, "meccan", 7),
                SurahEntity(2, "Al-Baqarah", "البقرة", 1, "quran/surah_002.mp3", 6300000, "medinan", 286),
                SurahEntity(3, "Aal-Imran", "آل عمران", 1, "quran/surah_003.mp3", 5400000, "medinan", 200),
                SurahEntity(4, "An-Nisa", "النساء", 1, "quran/surah_004.mp3", 4800000, "medinan", 176),
                SurahEntity(5, "Al-Ma'idah", "المائدة", 1, "quran/surah_005.mp3", 3600000, "medinan", 120),
                SurahEntity(6, "Al-An'am", "الأنعام", 1, "quran/surah_006.mp3", 4500000, "meccan", 165),
                SurahEntity(7, "Al-A'raf", "الأعراف", 1, "quran/surah_007.mp3", 5700000, "meccan", 206),
                SurahEntity(8, "Al-Anfal", "الأنفال", 1, "quran/surah_008.mp3", 2100000, "medinan", 75),
                SurahEntity(9, "At-Tawbah", "التوبة", 1, "quran/surah_009.mp3", 3600000, "medinan", 129),
                SurahEntity(10, "Yunus", "يونس", 1, "quran/surah_010.mp3", 3000000, "meccan", 109),
                SurahEntity(11, "Hud", "هود", 1, "quran/surah_011.mp3", 3300000, "meccan", 123),
                SurahEntity(12, "Yusuf", "يوسف", 1, "quran/surah_012.mp3", 3000000, "meccan", 111),
                SurahEntity(13, "Ar-Ra'd", "الرعد", 1, "quran/surah_013.mp3", 1500000, "medinan", 43),
                SurahEntity(14, "Ibrahim", "إبراهيم", 1, "quran/surah_014.mp3", 1500000, "meccan", 52),
                SurahEntity(15, "Al-Hijr", "الحجر", 1, "quran/surah_015.mp3", 1200000, "meccan", 99),
                SurahEntity(16, "An-Nahl", "النحل", 1, "quran/surah_016.mp3", 3600000, "meccan", 128),
                SurahEntity(17, "Al-Isra", "الإسراء", 1, "quran/surah_017.mp3", 3000000, "meccan", 111),
                SurahEntity(18, "Al-Kahf", "الكهف", 1, "quran/surah_018.mp3", 3000000, "meccan", 110),
                SurahEntity(19, "Maryam", "مريم", 1, "quran/surah_019.mp3", 1500000, "meccan", 98),
                SurahEntity(20, "Ta-Ha", "طه", 1, "quran/surah_020.mp3", 2100000, "meccan", 135),
                SurahEntity(21, "Al-Anbiya", "الأنبياء", 1, "quran/surah_021.mp3", 2100000, "meccan", 112),
                SurahEntity(22, "Al-Hajj", "الحج", 1, "quran/surah_022.mp3", 1800000, "medinan", 78),
                SurahEntity(23, "Al-Mu'minun", "المؤمنون", 1, "quran/surah_023.mp3", 2100000, "meccan", 118),
                SurahEntity(24, "An-Nur", "النور", 1, "quran/surah_024.mp3", 1800000, "medinan", 64),
                SurahEntity(25, "Al-Furqan", "الفرقان", 1, "quran/surah_025.mp3", 1500000, "meccan", 77),
                SurahEntity(26, "Ash-Shu'ara", "الشعراء", 1, "quran/surah_026.mp3", 4200000, "meccan", 227),
                SurahEntity(27, "An-Naml", "النمل", 1, "quran/surah_027.mp3", 2400000, "meccan", 93),
                SurahEntity(28, "Al-Qasas", "القصص", 1, "quran/surah_028.mp3", 3300000, "meccan", 88),
                SurahEntity(29, "Al-Ankabut", "العنكبوت", 1, "quran/surah_029.mp3", 2100000, "meccan", 69),
                SurahEntity(30, "Ar-Rum", "الروم", 1, "quran/surah_030.mp3", 1800000, "meccan", 60),
                SurahEntity(31, "Luqman", "لقمان", 1, "quran/surah_031.mp3", 900000, "meccan", 34),
                SurahEntity(32, "As-Sajdah", "السجدة", 1, "quran/surah_032.mp3", 600000, "meccan", 30),
                SurahEntity(33, "Al-Ahzab", "الأحزاب", 1, "quran/surah_033.mp3", 2700000, "medinan", 73),
                SurahEntity(34, "Saba", "سبأ", 1, "quran/surah_034.mp3", 1800000, "meccan", 54),
                SurahEntity(35, "Fatir", "فاطر", 1, "quran/surah_035.mp3", 1500000, "meccan", 45),
                SurahEntity(36, "Ya-Sin", "يس", 1, "quran/surah_036.mp3", 1800000, "meccan", 83),
                SurahEntity(37, "As-Saffat", "الصافات", 1, "quran/surah_037.mp3", 2700000, "meccan", 182),
                SurahEntity(38, "Sad", "ص", 1, "quran/surah_038.mp3", 2100000, "meccan", 88),
                SurahEntity(39, "Az-Zumar", "الزمر", 1, "quran/surah_039.mp3", 2700000, "meccan", 75),
                SurahEntity(40, "Ghafir", "غافر", 1, "quran/surah_040.mp3", 2700000, "meccan", 85),
                SurahEntity(41, "Fussilat", "فصلت", 1, "quran/surah_041.mp3", 1800000, "meccan", 54),
                SurahEntity(42, "Ash-Shura", "الشورى", 1, "quran/surah_042.mp3", 2100000, "meccan", 53),
                SurahEntity(43, "Az-Zukhruf", "الزخرف", 1, "quran/surah_043.mp3", 2100000, "meccan", 89),
                SurahEntity(44, "Ad-Dukhan", "الدخان", 1, "quran/surah_044.mp3", 900000, "meccan", 59),
                SurahEntity(45, "Al-Jathiyah", "الجاثية", 1, "quran/surah_045.mp3", 1200000, "meccan", 37),
                SurahEntity(46, "Al-Ahqaf", "الأحقاف", 1, "quran/surah_046.mp3", 1200000, "meccan", 35),
                SurahEntity(47, "Muhammad", "محمد", 1, "quran/surah_047.mp3", 1200000, "medinan", 38),
                SurahEntity(48, "Al-Fath", "الفتح", 1, "quran/surah_048.mp3", 1200000, "medinan", 29),
                SurahEntity(49, "Al-Hujurat", "الحجرات", 1, "quran/surah_049.mp3", 600000, "medinan", 18),
                SurahEntity(50, "Qaf", "ق", 1, "quran/surah_050.mp3", 900000, "meccan", 45),
                SurahEntity(51, "Adh-Dhariyat", "الذاريات", 1, "quran/surah_051.mp3", 900000, "meccan", 60),
                SurahEntity(52, "At-Tur", "الطور", 1, "quran/surah_052.mp3", 600000, "meccan", 49),
                SurahEntity(53, "An-Najm", "النجم", 1, "quran/surah_053.mp3", 900000, "meccan", 62),
                SurahEntity(54, "Al-Qamar", "القمر", 1, "quran/surah_054.mp3", 900000, "meccan", 55),
                SurahEntity(55, "Ar-Rahman", "الرحمن", 1, "quran/surah_055.mp3", 1800000, "medinan", 78),
                SurahEntity(56, "Al-Waqi'ah", "الواقعة", 1, "quran/surah_056.mp3", 1500000, "meccan", 96),
                SurahEntity(57, "Al-Hadid", "الحديد", 1, "quran/surah_057.mp3", 1800000, "medinan", 29),
                SurahEntity(58, "Al-Mujadila", "المجادلة", 1, "quran/surah_058.mp3", 1200000, "medinan", 22),
                SurahEntity(59, "Al-Hashr", "الحشر", 1, "quran/surah_059.mp3", 900000, "medinan", 24),
                SurahEntity(60, "Al-Mumtahanah", "الممتحنة", 1, "quran/surah_060.mp3", 600000, "medinan", 13),
                SurahEntity(61, "As-Saf", "الصف", 1, "quran/surah_061.mp3", 450000, "medinan", 14),
                SurahEntity(62, "Al-Jumu'ah", "الجمعة", 1, "quran/surah_062.mp3", 450000, "medinan", 11),
                SurahEntity(63, "Al-Munafiqun", "المنافقون", 1, "quran/surah_063.mp3", 450000, "medinan", 11),
                SurahEntity(64, "At-Taghabun", "التغابن", 1, "quran/surah_064.mp3", 600000, "medinan", 18),
                SurahEntity(65, "At-Talaq", "الطلاق", 1, "quran/surah_065.mp3", 600000, "medinan", 12),
                SurahEntity(66, "At-Tahrim", "التحريم", 1, "quran/surah_066.mp3", 600000, "medinan", 12),
                SurahEntity(67, "Al-Mulk", "الملك", 1, "quran/surah_067.mp3", 900000, "meccan", 30),
                SurahEntity(68, "Al-Qalam", "القلم", 1, "quran/surah_068.mp3", 900000, "meccan", 52),
                SurahEntity(69, "Al-Haqqah", "الحاقة", 1, "quran/surah_069.mp3", 900000, "meccan", 52),
                SurahEntity(70, "Al-Ma'arij", "المعارج", 1, "quran/surah_070.mp3", 600000, "meccan", 44),
                SurahEntity(71, "Nuh", "نوح", 1, "quran/surah_071.mp3", 600000, "meccan", 28),
                SurahEntity(72, "Al-Jinn", "الجن", 1, "quran/surah_072.mp3", 600000, "meccan", 28),
                SurahEntity(73, "Al-Muzzammil", "المزمل", 1, "quran/surah_073.mp3", 450000, "meccan", 20),
                SurahEntity(74, "Al-Muddaththir", "المدثر", 1, "quran/surah_074.mp3", 900000, "meccan", 56),
                SurahEntity(75, "Al-Qiyamah", "القيامة", 1, "quran/surah_075.mp3", 600000, "meccan", 40),
                SurahEntity(76, "Al-Insan", "الإنسان", 1, "quran/surah_076.mp3", 600000, "medinan", 31),
                SurahEntity(77, "Al-Mursalat", "المرسلات", 1, "quran/surah_077.mp3", 600000, "meccan", 50),
                SurahEntity(78, "An-Naba", "النبأ", 1, "quran/surah_078.mp3", 600000, "meccan", 40),
                SurahEntity(79, "An-Nazi'at", "النازعات", 1, "quran/surah_079.mp3", 600000, "meccan", 46),
                SurahEntity(80, "Abasa", "عبس", 1, "quran/surah_080.mp3", 450000, "meccan", 42),
                SurahEntity(81, "At-Takwir", "التكوير", 1, "quran/surah_081.mp3", 300000, "meccan", 29),
                SurahEntity(82, "Al-Infitar", "الانفطار", 1, "quran/surah_082.mp3", 300000, "meccan", 19),
                SurahEntity(83, "Al-Mutaffifin", "المطففين", 1, "quran/surah_083.mp3", 600000, "meccan", 36),
                SurahEntity(84, "Al-Inshiqaq", "الانشقاق", 1, "quran/surah_084.mp3", 300000, "meccan", 25),
                SurahEntity(85, "Al-Buruj", "البروج", 1, "quran/surah_085.mp3", 300000, "meccan", 22),
                SurahEntity(86, "At-Tariq", "الطارق", 1, "quran/surah_086.mp3", 300000, "meccan", 17),
                SurahEntity(87, "Al-A'la", "الأعلى", 1, "quran/surah_087.mp3", 300000, "meccan", 19),
                SurahEntity(88, "Al-Ghashiyah", "الغاشية", 1, "quran/surah_088.mp3", 300000, "meccan", 26),
                SurahEntity(89, "Al-Fajr", "الفجر", 1, "quran/surah_089.mp3", 600000, "meccan", 30),
                SurahEntity(90, "Al-Balad", "البلد", 1, "quran/surah_090.mp3", 300000, "meccan", 20),
                SurahEntity(91, "Ash-Shams", "الشمس", 1, "quran/surah_091.mp3", 300000, "meccan", 15),
                SurahEntity(92, "Al-Layl", "الليل", 1, "quran/surah_092.mp3", 300000, "meccan", 21),
                SurahEntity(93, "Ad-Duha", "الضحى", 1, "quran/surah_093.mp3", 300000, "meccan", 11),
                SurahEntity(94, "Ash-Sharh", "الشرح", 1, "quran/surah_094.mp3", 150000, "meccan", 8),
                SurahEntity(95, "At-Tin", "التين", 1, "quran/surah_095.mp3", 150000, "meccan", 8),
                SurahEntity(96, "Al-Alaq", "العلق", 1, "quran/surah_096.mp3", 300000, "meccan", 19),
                SurahEntity(97, "Al-Qadr", "القدر", 1, "quran/surah_097.mp3", 150000, "meccan", 5),
                SurahEntity(98, "Al-Bayyinah", "البينة", 1, "quran/surah_098.mp3", 300000, "medinan", 8),
                SurahEntity(99, "Az-Zalzalah", "الزلزلة", 1, "quran/surah_099.mp3", 150000, "medinan", 8),
                SurahEntity(100, "Al-Adiyat", "العاديات", 1, "quran/surah_100.mp3", 150000, "meccan", 11),
                SurahEntity(101, "Al-Qari'ah", "القارعة", 1, "quran/surah_101.mp3", 150000, "meccan", 11),
                SurahEntity(102, "At-Takathur", "التكاثر", 1, "quran/surah_102.mp3", 150000, "meccan", 8),
                SurahEntity(103, "Al-Asr", "العصر", 1, "quran/surah_103.mp3", 150000, "meccan", 3),
                SurahEntity(104, "Al-Humazah", "الهمزة", 1, "quran/surah_104.mp3", 150000, "meccan", 9),
                SurahEntity(105, "Al-Fil", "الفيل", 1, "quran/surah_105.mp3", 150000, "meccan", 5),
                SurahEntity(106, "Quraysh", "قريش", 1, "quran/surah_106.mp3", 150000, "meccan", 4),
                SurahEntity(107, "Al-Ma'un", "الماعون", 1, "quran/surah_107.mp3", 150000, "meccan", 7),
                SurahEntity(108, "Al-Kawthar", "الكوثر", 1, "quran/surah_108.mp3", 150000, "meccan", 3),
                SurahEntity(109, "Al-Kafirun", "الكافرون", 1, "quran/surah_109.mp3", 150000, "meccan", 6),
                SurahEntity(110, "An-Nasr", "النصر", 1, "quran/surah_110.mp3", 150000, "medinan", 3),
                SurahEntity(111, "Al-Masad", "المسد", 1, "quran/surah_111.mp3", 150000, "meccan", 5),
                SurahEntity(112, "Al-Ikhlas", "الإخلاص", 1, "quran/surah_112.mp3", 150000, "meccan", 4),
                SurahEntity(113, "Al-Falaq", "الفلق", 1, "quran/surah_113.mp3", 150000, "meccan", 5),
                SurahEntity(114, "An-Nas", "الناس", 1, "quran/surah_114.mp3", 150000, "meccan", 6)
            )
        }
    }
}

class Converters {
    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus {
        return DownloadStatus.valueOf(value)
    }
}