package com.example.flo

import androidx.room.*

@Dao
interface AlbumDao {

    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable") // 테이블의 모든 값을 가져와라
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    @Insert
    fun likeAlbum(like: Like)

    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikeAlbum(userId:Int, albumId:Int) : Int?

    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId") //일치하는 정보의 데이터를 삭제해주기
    fun dislikeAlbum(userId:Int,albumId:Int) : Int

    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT ON LT.albumId=AT.id WHERE LT.userId = :userId")
    fun getLikeAlbums(userId:Int) : List<Album>


}
