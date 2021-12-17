package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat

class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding

    private var player: Player? = null
    private var repeat = 0

    //미디어 플레이어
    private var mediaPlayer: MediaPlayer? = null

    //Gson
    private var gson: Gson = Gson()

    private var songs = ArrayList<Song>()
    private var nowPos = 0
    private lateinit var songDB: SongDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSeekbarchange()

        initPlayList()
        initSong()

        initClickListener()

    }




    override fun onStart() {
        super.onStart()

        initSong()
    }

    override fun onPause() { //사용자가 activity에서 focus를 잃을 때
        super.onPause()


        songs[nowPos].isPlaying = false
        songs[nowPos].second = mediaPlayer!!.currentPosition / 1000

        setPlayerStatus(false) //재생버튼도 일시정지로 바꿔줌

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //sharedPreference 조작할 때 사용한다

        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSecond", songs[nowPos].second)
        editor.apply()
    }


    override fun onDestroy() { //필요없는 리소스 해제해줘야함
        super.onDestroy()

        player!!.interrupt() //화면이 꺼지면 오류를 내서 thread를 종료시킴(스레드 해제)
        mediaPlayer?.release() //미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }

    private fun setSeekbarchange() {
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
                songs[nowPos].second = mediaPlayer!!.currentPosition / 1000
                mediaPlayer?.seekTo(binding.songPlaySb.progress)

            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
    }
    private fun initClickListener() {
        binding.songNuguDownBtnIv.setOnClickListener {
            startMainActivity()
        }

        binding.songNuguPlayBtnIv.setOnClickListener {
            setPlayerStatus(true)

        }

        binding.songNuguPauseBtnIv.setOnClickListener {
            setPlayerStatus(false)

        }
        binding.songMyLikeBtnIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
            //Log.d("like",songs[nowPos].isLike.toString())
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
            songs[nowPos].isPlaying = true
            Toast.makeText(this, "반복을 사용하지 않습니다.", Toast.LENGTH_SHORT).show()
            repeat = 0
        }
        binding.songPreviousBtnIv.setOnClickListener {
            moveSong(-1)
        }
        binding.songNextBtnIv.setOnClickListener {
            moveSong(+1)
        }
    }

    private fun initPlayList(){ //DB더미데이터 받아오기 --> 앨범 DB사용해서 앨범의 음악 리스트만 저장 가능??
        songDB = SongDB.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }



    private fun initSong() { //현재 보여지고 있는 song id 값 받아와서 nowPos를 찾아줌
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songSecond=spf.getInt("songSecond",0)
        Log.d("songSecond",songSecond.toString())
        nowPos = getPlayingSongPosition(songId)
        songs[nowPos].second=songSecond

        Log.d("now Song ID",songs[nowPos].id.toString())
        Log.d("now Song SECOND",songs[nowPos].second.toString())


        setPlayer(songs[nowPos])

        if (mediaPlayer === null) {
            val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
            mediaPlayer = MediaPlayer.create(this, music)
        }

        mediaPlayer?.let {
            binding.songPlaySb.max = it.duration
            it.seekTo(songs[nowPos].second * 1000)
            binding.songPlaySb.progress = it.currentPosition
        }

        startPlayer()
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

    private fun getPlayingSongPosition(songId: Int): Int{ //song의 id 값과  DB에서 받아온 songs를 비교해서 nowpos찾는 함수
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) { //데이터 랜더링
        val music = resources.getIdentifier(song.music, "raw", this.packageName)

        binding.songAlbumTitleTv.text = song.title
        binding.songAlbumSingerTv.text = song.singer
        binding.songMusicPlayTimeTv.text =
            String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songMusicPlayEndTv.text =
            String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songPlaySb.progress = (song.second * 1000 / song.playTime)

        setPlayerStatus(song.isPlaying)

        if (song.isLike) {
            binding.songMyLikeBtnIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songMyLikeBtnIv.setImageResource(R.drawable.ic_my_like_off)
        }

        mediaPlayer = MediaPlayer.create(this, music)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
//        binding.songAlbumTitleTv.text = song.title
//        binding.songAlbumSingerTv.text = song.singer
//        binding.songMusicPlayTimeTv.text =
//            String.format("%02d:%02d", song.second / 60, song.second % 60)
        player?.isPlaying = isPlaying
        songs[nowPos].isPlaying = isPlaying


        if (isPlaying) {
            binding.songNuguPlayBtnIv.visibility = View.GONE
            binding.songNuguPauseBtnIv.visibility = View.VISIBLE
            mediaPlayer?.start()

        } else {
            binding.songNuguPlayBtnIv.visibility = View.VISIBLE
            binding.songNuguPauseBtnIv.visibility = View.GONE
            mediaPlayer?.pause()
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
    private fun moveSong(direct: Int){

        if (nowPos + direct < 0){ //첫번째 곡이면
            Toast.makeText(this,"first song",Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){ //두번째 곡이면
            Toast.makeText(this,"last song",Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct //이전곡이면 -1이 더해지고 다음곡이면 +1이 더해짐

        player!!.interrupt() //새로운 노래를 재생해줘야하니까..
        startPlayer() //다음 노래로 새롭게 스레드 재시작해주깅

        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스를 해방
        mediaPlayer = null // 미디어플레이어 해제

        setPlayer(songs[nowPos])
    }
    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike //반대값을 넣어주깅깅
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        if (isLike){
            binding.songMyLikeBtnIv.setImageResource(R.drawable.ic_my_like_off)
            Toast.makeText(this, "해당 곡을 좋아하지않습니다.", Toast.LENGTH_SHORT).show()

        }else{
            binding.songMyLikeBtnIv.setImageResource(R.drawable.ic_my_like_on)
            Toast.makeText(this, "해당 곡을 좋아합니다.", Toast.LENGTH_SHORT).show()

        }
    }

    //쓰레드
    inner class Player(private val playTime: Int, var isPlaying: Boolean, var second: Int) : Thread() {
        override fun run() {
            try {
                while (true) {
                    if (songs[nowPos].second >= playTime) {
                        break
                    }

                    if (isPlaying) {
                        binding.songPlaySb.max = mediaPlayer!!.duration
                        var timeFormat = SimpleDateFormat("mm:ss")

                        runOnUiThread {
                            binding.songPlaySb.progress = mediaPlayer!!.currentPosition
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
}