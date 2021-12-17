package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemAlbumBinding
import com.example.flo.databinding.ItemSaveBinding

class SaveRVAdapter() : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {

    private val songs = ArrayList<Song>() //안전하게 데이터 관리

    //클릭 인터페이스 정의
    interface MyItemClickListener {
        fun onItemClick(album: Album)
        fun onRemoveSong(position: Int)
    }
    //리스너 객체를 저장할 변수
    private lateinit var myItemClickListener: MyItemClickListener

    //리스너 객체를 전달받는 함수
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SaveRVAdapter.ViewHolder {
        val binding : ItemSaveBinding = ItemSaveBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.binding.itemSavePlayerMoreBtnIv.setOnClickListener {
            myItemClickListener.onRemoveSong(position)
        }

    }

    override fun getItemCount(): Int = songs.size

    fun removeItems(position: Int) {
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSaveBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(song : Song) {
            binding.itemSaveTitleTv.text=song.title
            binding.itemSaveSingerTv.text=song.singer
            binding.itemSaveIv.setImageResource(song.coverImg!!)
        }

    }
}
