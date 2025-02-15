package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.LocationTag
import com.example.advancedandroidapp.data.models.LocationTagMap
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationTagDao {
    @Query("SELECT * FROM location_tags")
    fun getAllTags(): Flow<List<LocationTag>>

    @Query("SELECT * FROM location_tags WHERE id = :id")
    suspend fun getTagById(id: String): LocationTag?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: LocationTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<LocationTag>)

    @Update
    suspend fun updateTag(tag: LocationTag)

    @Delete
    suspend fun deleteTag(tag: LocationTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagMap(tagMap: LocationTagMap)

    @Delete
    suspend fun deleteTagMap(tagMap: LocationTagMap)

    @Query("SELECT t.* FROM location_tags t INNER JOIN location_tag_map m ON t.id = m.tag_id WHERE m.location_id = :locationId")
    fun getTagsForLocation(locationId: String): Flow<List<LocationTag>>
}
