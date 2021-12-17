package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.flo.databinding.FragmentSaveBinding

class SaveFragment: Fragment() {
    lateinit var binding: FragmentSaveBinding
    lateinit var songDB: SongDB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        songDB = SongDB.getInstance(requireContext())!!

        initRecyclerview()

//        val saveRVAdapter = SaveRVAdapter()
//        binding.saveRv.adapter=saveRVAdapter
//        binding.saveRv.layoutManager=
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
//
//        saveRVAdapter.setMyItemClickListener(object :SaveRVAdapter.MyItemClickListener{
//            override fun onItemClick(album: Album) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onRemoveSong(position: Int) {
//                saveRVAdapter.removeItems(position)
//            }
//        })
        return binding.root

    }
    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }
    private fun initRecyclerview(){

        val saveRVAdapter = SaveRVAdapter()
        saveRVAdapter.setMyItemClickListener(object :SaveRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                TODO("Not yet implemented")
            }

            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId)
            }
        })

        binding.saveRv.adapter=saveRVAdapter
        saveRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList)
        binding.saveRv.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }
}