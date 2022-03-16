package co.finema.etdassi.common.helper

import android.content.Context
import androidx.room.AutoMigration

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.finema.etdassi.common.data.VCDocumentTable
import co.finema.etdassi.common.data.VCDocumentDao
import co.finema.etdassi.common.data.VPDocumentDao
import co.finema.etdassi.common.data.VPDocumentTable
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedDao
import co.finema.etdassi.feature.mysign.data.MyRejectDao
import co.finema.etdassi.feature.mysign.myreject.MyRejectModel
import co.finema.etdassi.feature.mysign.data.SignRequestDao
import co.finema.etdassi.feature.mysign.data.SignRequestModel
import co.finema.etdassi.feature.notification.data.NotificationDao
import co.finema.etdassi.feature.notification.data.NotificationModel


@Database(entities = [SignRequestModel::class, MyRejectModel::class, VCSigned::class,
    NotificationModel::class, VCDocumentTable::class, VPDocumentTable::class],
    version = 20, autoMigrations = [AutoMigration(from = 19, to = 20)])//autoMigrations = [AutoMigration(from = 7, to = 8)]
abstract class AppDatabase: RoomDatabase() {
    abstract fun signRequestDao(): SignRequestDao
    abstract fun myRejectDao(): MyRejectDao
    abstract fun vcSignedDao(): VCSignedDao
    abstract fun notificationDao(): NotificationDao
    abstract fun vcDocumentDao(): VCDocumentDao
    abstract fun vpDocumentDao(): VPDocumentDao

    companion object {

        const val DATABASE_NAME = "ssi-ewallet-db"

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()
        }

    }
}