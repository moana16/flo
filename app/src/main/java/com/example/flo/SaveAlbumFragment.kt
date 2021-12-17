package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.flo.databinding.FragmentSavealbumBinding

class SaveAlbumFragment : Fragment() {
    lateinit var binding: FragmentSavealbumBinding

    private var albums = ArrayList<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavealbumBinding.inflate(inflater, container, false)


        val songDB = SongDB.getInstance(requireContext())!!
        val userId=getUserIdx(requireContext())


        val albumRVAdapter = AlbumLockerRVadapter()
        binding.savealbumRv.adapter=albumRVAdapter
        albumRVAdapter.addAlbums(songDB.albumDao().getLikeAlbums(userId) as ArrayList)

        binding.savealbumRv.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        albumRVAdapter.setMyItemClickListener(object : AlbumLockerRVadapter.MyItemClickListener {
            override fun onRemoveSong(albumId : Int) {
                albumRVAdapter.removeAlbum(albumId)
            }

        })
        return binding.root
    }


}