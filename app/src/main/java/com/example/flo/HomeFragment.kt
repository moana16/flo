package com.example.flo

import android.icu.util.DateInterval
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var albums = ArrayList<Album>()
    private lateinit var songDB: SongDB

    var bannerPosition=0
    private var homeBannerHandler = HomeBannerHandler()
    //1.5초 간격으로 배너 페이지 넘어감
    private var intervalTime=1500.toLong()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


//        binding.homeTodayAlbumEx1Iv.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm,AlbumFragment())
//                .commitAllowingStateLoss()
//        }
        //ROOM_DB
        songDB = SongDB.getInstance(requireContext())!!
        albums.addAll(songDB.albumDao().getAlbums()) // songDB에서 album list를 가져옵니다.





        //아이템 어댑터 설정
        val albumRVAdapter = AlbumRVAdapter(albums) //더미데이터랑 어댑터 연결
        binding.homeTodayMusicAlbumRv.adapter=albumRVAdapter //리싸이클러뷰에 어댑터 연결
        binding.homeTodayMusicAlbumRv.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
                changeSongFragment(album)
            }
        })

        val panelAdapter=PanelViewpagerAdapter(this)

        panelAdapter.addFragment(PanelFragment())
        panelAdapter.addFragment(PanelTwoFragment())
        panelAdapter.addFragment(PanelThreeFragment())
        binding.homePanelVp.adapter=panelAdapter
        binding.homePanelVp.orientation=ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePanelIndicator.setViewPager2(binding.homePanelVp)


        val bannerAdapter = BannerViewpagerAdapter(this)

        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))

        binding.homeBannerVp.adapter=bannerAdapter
        binding.homeBannerVp.orientation=ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeBannerVp.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                //state 값으로 뷰페이저의 상태를 알 수 있음
                override fun onPageScrollStateChanged(state : Int) {
                    super.onPageScrollStateChanged(state)
                    when(state) {
                        //뷰페이저가 움직이는 중일 때 자동 스크롤 시작 함수 호툴
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                        //뷰페이저에서 손 뗐을 때 뷰페이저가 멈춰있을 때 자동 스크롤 멈춤
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                    }
                }
            })
        }

        return binding.root
    }
    //앨범 정보들을 앨범프레그먼트에 전달
    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album",albumJson)

                }
            })
            .commitAllowingStateLoss()
    }
    private fun changeSongFragment(album: Album) {
        SongFragment().apply {
            arguments = Bundle().apply {
                val gson = Gson()
                val albumJson = gson.toJson(album)
                putString("album",albumJson)

            }
        }

    }

    //배너 자동 스크롤 시작하게 하는 함수
    private fun autoScrollStart(intervalTime: Long) {
        homeBannerHandler.removeMessages(0)
        homeBannerHandler.sendEmptyMessageDelayed(0,intervalTime) //intervalTime만큼 반복해서 핸들러 실행

    }
    //배너 자동 스크롤 멈추게 하는 함수
    private fun autoScrollStop() {
        homeBannerHandler.removeMessages(0) // 핸들러 중지
    }
    private inner class HomeBannerHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what===0) {
                binding.homeBannerVp.setCurrentItem(++bannerPosition%3,true) //다음 페이지로 이동
                Log.d("handleMessage","$bannerPosition")
                autoScrollStart(intervalTime) //스크롤 유지
            }
        }
    }
    //다른 화면으로 갔다가 돌아오면 배너 스크롤 다시 시작
    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }
    //다른 화면을 보고 있는 동안에는 배너 스크롤 안함
    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }



}