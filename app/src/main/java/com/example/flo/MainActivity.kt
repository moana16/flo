package com.example.flo

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var player: Player? = null

    //Gson
    private var gson: Gson = Gson()

    //Song
    private var songs = ArrayList<Song>()
    private var nowPos = 0

    //미디어 플레이어
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        inputDummyAlbums()
        inputDummySongs()

        setMiniPlayerChange()


        binding.mainPlayerLayout.setOnClickListener {
            Log.d("nowSongId", songs[nowPos].id.toString())
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.putInt("songSecond", songs[nowPos].second)
            editor.apply()

            val intent = Intent(this@MainActivity, SongActivity::class.java)

            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
//            player!!.isPlaying = true
//            songs[nowPos].isPlaying = true
//            setMiniplayer(songs[nowPos])
//            mediaPlayer?.start()
            setPlayerStatus(true)
        }

        binding.mainPauseBtn.setOnClickListener {
//            player!!.isPlaying = false
//            songs[nowPos].isPlaying = false
//            setMiniplayer(songs[nowPos])
//            mediaPlayer?.pause()
            setPlayerStatus(false)
        }
        binding.mainPreviousBtn.setOnClickListener {
            moveSong(-1)
        }
        binding.mainNextBtn.setOnClickListener {
            moveSong(+1)
        }

        initNavigation()


    }


    override fun onStart() {
        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0) //일단 받아온 값이 없어서 기본값이 0임
        val songSecond = spf.getInt("songSecond", 0) //일단 받아온 값이 없어서 기본값이 0임



        val songDB = SongDB.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
//        songs[nowPos] = if (songId == 0) {
//            songDB.songDao().getSong(1) //근데 인덱스는 1부터 시작하니까 1로 넣어줌
//        } else {
//            songDB.songDao().getSong(songId)
//
//        }
        nowPos = getPlayingSongPosition(songId)


        Log.d("song ID", songs[nowPos].id.toString())

        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        songs[nowPos].second=songSecond
        setMiniplayer(songs[nowPos])

        if (mediaPlayer === null) { //아예 처음 실행이면
            mediaPlayer = MediaPlayer.create(this, music) //만들어줌
        }

        mediaPlayer?.let {
            binding.mainMiniPlaySb.max = it.duration //곡의 총 길이만큼 max를 설정해줌
            it.seekTo(songs[nowPos].second * 1000) //현재 진행 위치를 찾아줌
            binding.mainMiniPlaySb.progress = it.currentPosition //progress 바를 현재 음악 실행 위치로 맞춰줌
        }

        startPlayer()
    }
    private fun getPlayingSongPosition(songId: Int): Int{ //song의 id 값과  DB에서 받아온 songs를 비교해서 nowpos찾는 함수
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    override fun onPause() { //사용자가 activity에서 focus를 잃을 때
        super.onPause()

        mediaPlayer?.pause() //미디어 플레이어 중지

        player!!.isPlaying = false //스레드 중지
        songs[nowPos].isPlaying = false

        songs[nowPos].second = mediaPlayer!!.currentPosition / 1000

        setMiniplayer(songs[nowPos])

        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()

    }

    override fun onDestroy() { //화면이 꺼지면 오류를 내서 thread를 종료시킴
        super.onDestroy()
        player!!.interrupt()
        mediaPlayer?.release() //미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }

    private fun moveSong(direct: Int){

        if (nowPos + direct < 0){ //첫번째 곡이면
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){ //두번째 곡이면
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct //이전곡이면 -1이 더해지고 다음곡이면 +1이 더해짐

        player!!.interrupt() //새로운 노래를 재생해줘야하니까..
        startPlayer() //다음 노래로 새롭게 스레드 재시작해주깅

        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스를 해방
        mediaPlayer = null // 미디어플레이어 해제

        setMiniplayer(songs[nowPos])
    }
    private fun startPlayer() {
        if (player === null) {
            player = Player(songs[nowPos].playTime, songs[nowPos].isPlaying, songs[nowPos].second)
            player!!.start()
        }

        player?.let {
            if (it.isAlive) {
                it.second = songs[nowPos].second
                it.isPlaying = songs[nowPos].isPlaying
            }
        }
    }

    private fun setMiniPlayerChange() {
        binding.mainMiniPlaySb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) { //진행중일때
                if (fromUser) {
                    binding.mainMiniPlaySb.progress = progress
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { //사용자가
                songs[nowPos].second = mediaPlayer!!.currentPosition / 1000
                mediaPlayer?.seekTo(binding.mainMiniPlaySb.progress)
            }
        })
    }
    private fun setPlayerStatus(isPlaying: Boolean) {
//        binding.songAlbumTitleTv.text = song.title
//        binding.songAlbumSingerTv.text = song.singer
//        binding.songMusicPlayTimeTv.text =
//            String.format("%02d:%02d", song.second / 60, song.second % 60)
        player?.isPlaying = isPlaying
        songs[nowPos].isPlaying = isPlaying


        if (isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()

        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            mediaPlayer?.pause()
        }
    }

    private fun setMiniplayer(song: Song) {

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniPlaySb.progress = song.second * 1000 / song.playTime


        setPlayerStatus(song.isPlaying)


        mediaPlayer = MediaPlayer.create(this, music)
    }


    private fun initNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, AlbumFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    inner class Player(private val playTime: Int, var isPlaying: Boolean, var second: Int = 0) : Thread() {

        @SuppressLint("SetTextI18n")
        override fun run() {
            try {
                while (true) {
                    if (songs[nowPos].second >= playTime) {
                        break
                    }

                    if (isPlaying) {
                        binding.mainMiniPlaySb.max = mediaPlayer!!.duration
                        var timeFormat = SimpleDateFormat("mm:ss")
                        runOnUiThread {
                            binding.mainMiniPlaySb.progress = mediaPlayer!!.currentPosition

                        }
                    }
                }


            } catch (e: InterruptedException) {
                Log.d("interrupt", "쓰레드가 종료되었습니다")
            }
        }
    }


    //ROOM_DB
    private fun inputDummyAlbums() {
        val songDB = SongDB.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "iScreaM Vol.10 : Next Level Remixes", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "MAP OF THE SOUL : PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                5,
                "GREAT!", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )

    }

    //ROOM_DB
    private fun inputDummySongs() {
        val songDB = SongDB.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter (Hotter Remix)",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter (Sweeter Remix)",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_nextlevel",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level (IMLAY Remix)",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_nextlevel",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "소우주 (Mikrokosmos)",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "Make It Right",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_lilac",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )

        songDB.songDao().insert(
            Song(
                "궁금해",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_lilac",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )


        val _songs = songDB.songDao().getSongs()
        Log.d("DB DATA", _songs.toString())

    }
}


