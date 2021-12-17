package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSavealbumBinding

class AlbumLockerRVadapter() : RecyclerView.Adapter<AlbumLockerRVadapter.ViewHolder> (){

    private val albums = ArrayList<Album>()

    interface MyItemClickListener {
        fun onRemoveSong(albumId : Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener=itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumLockerRVadapter.ViewHolder {
        val binding : ItemSavealbumBinding= ItemSavealbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumLockerRVadapter.ViewHolder, position: Int) {
        holder.bind(albums[position])
        holder.binding.itemSavealbumPlayerMoreBtn.setOnClickListener {
            removeAlbum(position)
            mItemClickListener.onRemoveSong(albums[position].id)
        }

    }

    override fun getItemCount(): Int = albums.size

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums : ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    fun removeAlbum(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSavealbumBinding ) :RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.itemSavealbumTitleTv.text=album.title
            binding.itemSavealbumSingerTv.text=album.singer
            binding.itemSavealbumIv.setImageResource(album.coverImg!!)
        }
    }
}