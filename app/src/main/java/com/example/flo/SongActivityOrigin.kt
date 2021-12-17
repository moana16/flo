package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import kotlin.concurrent.timer

class SongActivityOrigin : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    private var song: Song = Song()
    private lateinit var player: Player
    private var repeat = 0

    //미디어 플레이어
    private var mediaPlayer: MediaPlayer? = null

    //Gson
    private var gson: Gson = Gson()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()


        player = Player(song.playTime, song.isPlaying)
        //player thread 시작함
        player.start()

        binding.songPlaySb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    //song.second=seekBar!!.progress*song.playTime/1000
                    binding.songPlaySb.progress = progress
                    Log.d("progress", "${binding.songPlaySb.progress}")

                    mediaPlayer?.seekTo(progress)
                    //song.second=mediaPlayer!!.currentPosition
                }//진행중일때
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { //사용자가
                song.second = mediaPlayer!!.currentPosition / 1000
                mediaPlayer?.seekTo(binding.songPlaySb.progress)

            }
        })

        binding.songNuguDownBtnIv.setOnClickListener {
            startMainActivity()
        }
        binding.songNuguPlayBtnIv.setOnClickListener {
            player.isPlaying = true
            song.isPlaying = true
            setPlayerStatus(song)
            mediaPlayer?.start()
        }
        binding.songNuguPauseBtnIv.setOnClickListener {
            player.isPlaying = false
            song.isPlaying = false
            setPlayerStatus(song)

            mediaPlayer?.pause()
        }
        binding.songRepeatInactiveBtnIv.setOnClickListener {
            setRepeatStatus(0)
            Toast.makeText(this, "전체 음악을 반복합니다.", Toast.LENGTH_SHORT).show()
            repeat = 0

        }
        binding.songRepeatActiveBtnIv.setOnClickListener {
            setRepeatStatus(1)

            Toast.makeText(this, "현재 음악을 반복합니다.", Toast.LENGTH_SHORT).show()
            repeat = 1
        }
        binding.songRepeatRecentActiveBtnIv.setOnClickListener {
            setRepeatStatus(2)
            song.isPlaying = true
            Toast.makeText(this, "반복을 사용하지 않습니다.", Toast.LENGTH_SHORT).show()
            repeat = 0
        }

    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer") && intent.hasExtra("second") && intent.hasExtra(
                "playTime"
            ) && intent.hasExtra("isPlaying") && intent.hasExtra("music")
        ) {
            song.title = intent.getStringExtra("title")!!
            song.singer = intent.getStringExtra("singer")!!
            song.second = intent.getIntExtra("second", 0)
            song.playTime = intent.getIntExtra("playTime", 0)
            song.isPlaying = intent.getBooleanExtra("isPlaying", false)
            song.music = intent.getStringExtra("music")!!
            //이름만 받은 리소스 파일을 실제 music 변수에 저자해줌
            val music = resources.getIdentifier(song.music, "raw", this.packageName)

            binding.songMusicPlayEndTv.text =
                String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
            binding.songAlbumSingerTv.text = song.singer
            binding.songAlbumTitleTv.text = song.title
            setPlayerStatus(song)
            mediaPlayer = MediaPlayer.create(this, music)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("title", song.title)
        intent.putExtra("singer", song.singer)
        intent.putExtra("second", song.second)
        intent.putExtra("playTime", song.playTime)
        intent.putExtra("isPlaying", song.isPlaying)
        intent.putExtra("music", song.music)
        startActivity(intent)
    }

    private fun setPlayerStatus(song: Song) {

        binding.songAlbumTitleTv.text = song.title
        binding.songAlbumSingerTv.text = song.singer
        //song.second=mediaPlayer!!.currentPosition/1000
        //binding.songPlaySb.progress=song.second*1000/song.playTime
        //mediaPlayer?.seekTo(binding.songPlaySb.progress)
        Log.d("second", "${song.second}")
        Log.d("progress", "${binding.songPlaySb.progress}")
        binding.songMusicPlayTimeTv.text =
            String.format("%02d:%02d", song.second / 60, song.second % 60)

        //mediaPlayer= MediaPlayer.create(this,music)
        if (song.isPlaying) {
            binding.songNuguPlayBtnIv.visibility = View.GONE
            binding.songNuguPauseBtnIv.visibility = View.VISIBLE

        } else {
            binding.songNuguPlayBtnIv.visibility = View.VISIBLE
            binding.songNuguPauseBtnIv.visibility = View.GONE
        }
    }

    private fun setRepeatStatus(isRepeat: Int) {
        if (isRepeat === 0) {
            binding.songRepeatInactiveBtnIv.visibility = View.GONE
            binding.songRepeatActiveBtnIv.visibility = View.VISIBLE
            binding.songRepeatRecentActiveBtnIv.visibility = View.GONE
        } else if (isRepeat === 1) {
            binding.songRepeatInactiveBtnIv.visibility = View.GONE
            binding.songRepeatActiveBtnIv.visibility = View.GONE
            binding.songRepeatRecentActiveBtnIv.visibility = View.VISIBLE
        } else {
            binding.songRepeatInactiveBtnIv.visibility = View.VISIBLE
            binding.songRepeatActiveBtnIv.visibility = View.GONE
            binding.songRepeatRecentActiveBtnIv.visibility = View.GONE
        }
    }

    //쓰레드
    inner class Player(private val playTime: Int, var isPlaying: Boolean) : Thread() {
        override fun run() {
            try {
                while (true) {
                    if (song.second >= playTime) {
                        break
                    }

                    if (isPlaying) {
                        binding.songPlaySb.max = mediaPlayer!!.duration
//                        Log.d("Player::SongAct", "seekbar max: ${binding.songPlaySb.max}, mp.dur: ${mediaPlayer!!.duration}, seekbar progress: ${binding.songPlaySb.progress}, song second: ${song.second}")
                        var timeFormat = SimpleDateFormat("mm:ss")
                        //sleep(1000) //thread에서 제공해주는 함수로 1초마다 second에게 1을 더해줌
                        //song.second++
                        runOnUiThread {
//                            binding.songPlaySb.setProgress(mediaPlayer!!.currentPosition)
//                            binding.songPlaySb.progress = song.second * 1000 / playTime //max가 1000값이니까
                            binding.songPlaySb.progress = mediaPlayer!!.currentPosition

                            //binding.songMusicPlayTimeTv.text =
                            //String.format("%02d:%02d", song.second / 60, song.second % 60)

                            binding.songMusicPlayTimeTv.text =
                                timeFormat.format(mediaPlayer!!.currentPosition)
                        }
                    }

                }

            } catch (e: InterruptedException) {
                Log.d("interrupt", "쓰레드가 종료되었습니다")
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val jsonSong = sharedPreferences.getString("song", null)

        song = if (jsonSong === null) {
            Song("라일락", "아이유(IU)", 0, 215, false, "music_lilac")
        } else {
            gson.fromJson(jsonSong, Song::class.java)
        }
        Log.d("hi::second", "${song.second}")

//        binding.songPlaySb.progress = song.second * 1000 / song.playTime
        //Log.d("progress","${binding.songPlaySb.progress}")

        binding.songPlaySb.max = mediaPlayer!!.duration
        mediaPlayer?.seekTo(song.second * 1000 )
        binding.songPlaySb.progress = mediaPlayer!!.currentPosition

        Log.d("hi::second", "${song.second}, cp: ${mediaPlayer!!.currentPosition}")


        setPlayerStatus(song)
    }

    override fun onResume() {
        super.onResume()
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val jsonSong = sharedPreferences.getString("song", null)
//
//        song = if (jsonSong === null) {
//            Song("라일락", "아이유(IU)", 0, 215, false, "music_lilac")
//        } else {
//            gson.fromJson(jsonSong, Song::class.java)
//        }
//        binding.songPlaySb.progress = song.second * 1000 / song.playTime
//        mediaPlayer?.seekTo(binding.songPlaySb.progress)
//        setPlayerStatus(song)
    }

    override fun onPause() { //사용자가 activity에서 focus를 잃을 때
        super.onPause()

        mediaPlayer?.pause() //미디어 플레이어 중지
        player.isPlaying = false //스레드 중지

        song.isPlaying = false
        song.second = mediaPlayer!!.currentPosition / 1000
//        binding.songPlaySb.progress = mediaPlayer!!.currentPosition


        setPlayerStatus(song) //재생버튼도 일시정지로 바꿔줌

        //데이터를 내부저장소 어딘가에 song이라는 이름으로 저장해줌
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //sharedPreference 조작할 때 사용한다
        //editor.putString("title",song.title ) 이렇게 보낼 수 있으나 코드가 많아질 수 있는 단점이 있음
        val json = gson.toJson(song) //song 데이터 객체를 json으로 변환해줌
        editor.putString("song", json) //자바 객체를 한번에 넣어줄 수 있음
        editor.apply()
    }


    override fun onDestroy() { //필요없는 리소스 해제해줘야함
        super.onDestroy()
        player.interrupt() //화면이 꺼지면 오류를 내서 thread를 종료시킴(스레드 해제)
        mediaPlayer?.release() //미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }


}