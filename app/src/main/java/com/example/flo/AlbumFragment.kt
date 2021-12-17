package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class AlbumFragment : Fragment() {
    lateinit var binding: FragmentAlbumBinding
    private val gson : Gson = Gson()

    val info = arrayListOf("수록곡","상세정보","영상")

    private var isLike : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        binding.albumArrowBackBtnIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm,HomeFragment())
                .commitAllowingStateLoss()
        }

        //Home 프레그먼트에서 넘어온 데이터  받아오기
        val albumData = arguments?.getString("album")
        val album = gson.fromJson(albumData, Album::class.java)
        //데이터 반영

        isLike = isLikeAlbum(album.id)
        setInit(album)
        setOnClick(album)

        val albumAdapter=AlbumViewpagerAdapter(this)
        binding.albumContentVp.adapter=albumAdapter
        TabLayoutMediator(binding.albumContentTl,binding.albumContentVp) {
            tab, position ->
            tab.text=info[position]
        }.attach()

        return binding.root
    }


    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumAlbumTitleTv.text = album.title.toString()
        binding.albumSingerTv.text = album.singer.toString()

        if(isLike) {
            binding.albumMyLikeOffBtnIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumMyLikeOffBtnIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun setOnClick(album: Album) {
        val userId : Int = getUserIdx(requireContext())
        binding.albumMyLikeOffBtnIv.setOnClickListener {
            if(isLike) {
                binding.albumMyLikeOffBtnIv.setImageResource(R.drawable.ic_my_like_off)
                disLikeAlbum(userId,album.id)
            }
            else {
                binding.albumMyLikeOffBtnIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId,album.id)
            }
        }
    }

    private fun likeAlbum(userId : Int, albumId : Int) {
        val songDB = SongDB.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum((like))

    }

    private fun isLikeAlbum(albumId: Int) :Boolean {
        val songDB = SongDB.getInstance(requireContext())!!
        val userId = getUserIdx(requireContext())

        val likeId = songDB.albumDao().isLikeAlbum(userId, albumId) //좋아요가 안눌러져있으면 likeId값이 null이 되기 때문에 처리 해줌

        return likeId != null
    }

    private fun disLikeAlbum(userId: Int,albumId: Int)  {
        val songDB = SongDB.getInstance(requireContext())!!
        songDB.albumDao().dislikeAlbum(userId,albumId)
    }


}