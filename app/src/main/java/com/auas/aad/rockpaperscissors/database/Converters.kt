package com.auas.aad.rockpaperscissors.database

import androidx.room.TypeConverter
import com.auas.aad.rockpaperscissors.model.Game
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun resultFromString(result: Game.Result?): String? {
        return result?.value
    }

    @TypeConverter
    fun resultToString(value: String?): Game.Result? {
        return Game.Result.values().associateBy(Game.Result::value)[value]
    }
}