package com.sk.carmusicapp.model


import com.google.gson.annotations.SerializedName

data class MyMusic(
    @SerializedName("data")
    val `data`: List<Data> = listOf(),
    @SerializedName("next")
    val next: String = "",
    @SerializedName("total")
    val total: Int = 0
) {
    data class Data(
        var isSelected: Boolean = false,
        @SerializedName("album")
        val album: Album = Album(),
        @SerializedName("artist")
        val artist: Artist = Artist(),
        @SerializedName("duration")
        val duration: Int = 0,
        @SerializedName("explicit_content_cover")
        val explicitContentCover: Int = 0,
        @SerializedName("explicit_content_lyrics")
        val explicitContentLyrics: Int = 0,
        @SerializedName("explicit_lyrics")
        val explicitLyrics: Boolean = false,
        @SerializedName("id")
        val id: Long = 0,
        @SerializedName("link")
        val link: String = "",
        @SerializedName("md5_image")
        val md5Image: String = "",
        @SerializedName("preview")
        val preview: String = "",
        @SerializedName("rank")
        val rank: Int = 0,
        @SerializedName("readable")
        val readable: Boolean = false,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("title_short")
        val titleShort: String = "",
        @SerializedName("title_version")
        val titleVersion: String = "",
        @SerializedName("type")
        val type: String = ""
    ) {

        data class Album(
            @SerializedName("cover")
            val cover: String = "",
            @SerializedName("cover_big")
            val coverBig: String = "",
            @SerializedName("cover_medium")
            val coverMedium: String = "",
            @SerializedName("cover_small")
            val coverSmall: String = "",
            @SerializedName("cover_xl")
            val coverXl: String = "",
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("md5_image")
            val md5Image: String = "",
            @SerializedName("title")
            val title: String = "",
            @SerializedName("tracklist")
            val tracklist: String = "",
            @SerializedName("type")
            val type: String = ""
        )

        data class Artist(
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("link")
            val link: String = "",
            @SerializedName("name")
            val name: String = "",
            @SerializedName("picture")
            val picture: String = "",
            @SerializedName("picture_big")
            val pictureBig: String = "",
            @SerializedName("picture_medium")
            val pictureMedium: String = "",
            @SerializedName("picture_small")
            val pictureSmall: String = "",
            @SerializedName("picture_xl")
            val pictureXl: String = "",
            @SerializedName("tracklist")
            val tracklist: String = "",
            @SerializedName("type")
            val type: String = ""
        )
    }
}