package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.databinding.FragmentSongBinding
import com.google.gson.Gson

class SongFragment : Fragment() {
    lateinit var binding: FragmentSongBinding
    private val gson : Gson = Gson()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        //Home 프레그먼트에서 넘어온 데이터  받아오기
        val albumData = arguments?.getString("album")
        val album = gson.fromJson(albumData, Album::class.java)
        //데이터 반영
        //setInit(album)
        return binding.root
    }

}