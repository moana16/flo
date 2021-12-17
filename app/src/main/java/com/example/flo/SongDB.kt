package com.example.flo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Song::class, Album::class, User::class, Like::class], version = 3)
abstract class SongDB: RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
    abstract fun UserDao() :UserDao

    companion object {
        private var instance: SongDB? = null

        @Synchronized
        fun getInstance(context: Context): SongDB? {
            if (instance == null) {
                synchronized(SongDB::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDB::class.java,
                        "song-database"//다른 데이터 베이스랑 이름겹치면 꼬임
                    ).allowMainThreadQueries().build()
                }
            }

            return instance
        }
    }
}