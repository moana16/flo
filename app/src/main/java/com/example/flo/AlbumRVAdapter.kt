package com.example.flo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemAlbumBinding

class AlbumRVAdapter (private val albumList : ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    //클릭 인터페이스 정의
    interface MyItemClickListener {
        fun onItemClick(album: Album)
    }
    //리스너 객체를 저장할 변수
    private lateinit var myItemClickListener: MyItemClickListener

    //리스너 객체를 전달받는 함수
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    //뷰홀더를 생성해 줘야 할 때 호출되는 함수 아이템 뷰 객체를 만들어서 뷰홀더에 던져줌
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }
    //뷰홀더에 데이터를 바인딩 해줘야 할 때마다 호출됨 엄청 많이 호출될 것임
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener { myItemClickListener.onItemClick(albumList[position]) }

    }

    fun addItems(albums: ArrayList<Album>) {
        albumList.clear()
        albumList.addAll(albums)
        notifyDataSetChanged()
    }

    fun addItem(album: Album) {
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItems() {
        albumList.clear()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        notifyDataSetChanged()
    }
    //데이터 세트 크기를 알려주는 함수 리싸이클러뷰가 마지막이 언제인지를 알게 됨
    override fun getItemCount(): Int = albumList.size

    //뷰홀더 아이템 뷰 객체들을 담는 곳
    inner class ViewHolder(val binding: ItemAlbumBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(album : Album) {
            binding.itemAlbumTitleTv.text=album.title
            binding.itemAlbumSingerTv.text=album.singer
            binding.itemAlbumIv.setImageResource(album.coverImg!!)
        }

    }
}