package com.macapp.carmusicapp.activity

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.macapp.carmusicapp.R
import com.macapp.carmusicapp.adapter.MyAdapter
import com.macapp.carmusicapp.api.Response
import com.macapp.carmusicapp.databinding.ActivityMainBinding
import com.macapp.carmusicapp.model.MyMusic
import com.macapp.carmusicapp.viewmodel.MusicViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MyAdapter.ItemClickListener {

    private val mainActivity by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val musicViewModel: MusicViewModel by viewModels()
    private var myAdapter: MyAdapter? = null
    private lateinit var imageView: ImageView
    private lateinit var songTitle: TextView
    private lateinit var singerName: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var mMediaPlayer: MediaPlayer
    private val musicList: ArrayList<MyMusic.Data> = ArrayList()
    private var playPosition: Int = 0
    private lateinit var timerTextView: TextView
    private var songId: Int = 0
    private lateinit var handler: Handler
    private var nextSongPosition: Int = 0
    private var previousSongPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivity.root)
        musicViewModel.mutableLiveDataResponse.observe(this, musicListObserver)
        handler = Handler()
        mMediaPlayer = MediaPlayer()
        initializeViews()
        initializeListeners()
        apiCalMusicList()
        Log.d("TAG", "onCreate: $songId")

        mainActivity.backBtn.setOnClickListener { onBackPressed() }

    }

    private fun initializeViews() {
        songTitle = findViewById(R.id.tv_song_title)
        singerName = findViewById(R.id.tv_singer_name)
        imageView = findViewById(R.id.imageViewControl)
        seekBar = findViewById(R.id.seekBar)
        timerTextView = findViewById(R.id.tvTimePost)

    }

    private fun initializeListeners() {
        mainActivity.ivNext.setOnClickListener {
            playNextSong()

        }
        mainActivity.ivPre.setOnClickListener {
            playPreviousSong()

        }
        mMediaPlayer.setOnCompletionListener {

            playNextSong()
            Toast.makeText(this,"Next Song Played",Toast.LENGTH_LONG).show()

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun playNextSong() {
        if (musicList.isNotEmpty()) {
            // Increment the playPosition to move to the next song
            playPosition = (playPosition + 1) % musicList.size

            if (playPosition >= 0 && playPosition < musicList.size) {
                val nextSong = musicList[playPosition]
                Log.d("TAG", "Next button Clicked : Current Song Position: $playPosition")
                nextSongPosition = playPosition
                myAdapter = MyAdapter(this@MainActivity, musicList, this, playPosition)
                mainActivity.recyclerview.adapter = myAdapter
                myAdapter?.notifyDataSetChanged()

                val nextSongUrl = nextSong.preview
                val nextSongDuration = nextSong.duration
                playSong(nextSongUrl, nextSongDuration)
                songNames(nextSong)

            } else {
                Log.e("TAG", "Invalid playPosition: $playPosition")

            }
        } else {
            Log.d("TAG", "No songs available to play")

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun playPreviousSong() {
        if (musicList.isNotEmpty()) {

            val previousPosition = if (playPosition > 0) playPosition - 1 else musicList.size - 1

            // Ensure the calculated index is within bounds
            if (previousPosition < musicList.size) {
                val previousSong = musicList[previousPosition]
                Log.d("TAG", "Previous button Clicked : Current Song Position: $previousPosition")
                previousSongPosition = previousPosition
                myAdapter = MyAdapter(this@MainActivity, musicList, this, previousPosition)
                mainActivity.recyclerview.adapter = myAdapter
                myAdapter?.notifyDataSetChanged()
                val previousSongUrl = previousSong.preview
                val songDuration = previousSong.duration
                playSong(previousSongUrl, songDuration)
                songNames(previousSong)
                // Update the playPosition to the previous index
                playPosition = previousPosition

            } else {
                Log.e("TAG", "Invalid previousPosition: $previousPosition")
            }
        } else {
            Log.d("TAG", "No songs available to play")
        }

    }

    private fun apiCalMusicList() {
        lifecycleScope.launch {
//            musicViewModel.getMusicList("Padayappa")
            musicViewModel.getMusicList("eminem")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private val musicListObserver = Observer<Response<MyMusic>> { response ->
        when (response) {
            is Response.Success -> {
                musicList.addAll(response.data!!.data)
                myAdapter = MyAdapter(this@MainActivity, response.data?.data, this, 0)
                mainActivity.recyclerview.adapter = myAdapter
                myAdapter?.notifyDataSetChanged()

                // Automatically select the first song and update UI
//                response.data?.data?.firstOrNull()?.let { firstSong ->
//                    onItemClick(firstSong, position = 0)
//                }
            }

            is Response.Error -> {
                Log.d("TAG", "Error: ${response.errorMessage}")
            }

            is Response.Loading -> {
                if (response.showLoader == true) {
                    mainActivity.progress.visibility = View.VISIBLE
                } else {
                    mainActivity.progress.visibility = View.GONE
                }
                Log.d("TAG", "Loading: ${response.showLoader}")
            }
        }
    }


    override fun onItemClick(item: MyMusic.Data, position: Int) {

        item.isSelected = true
        playPosition = position

        mainActivity.tvSongTitle.text = item.title
        mainActivity.tvSingerName.text = item.artist.name
        Picasso.get().load(item.album.cover).into(mainActivity.imageViewControl)

        playSong(item.preview, item.duration)
        songId = item.id.toInt()

        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(item.preview)


        mMediaPlayer?.prepare()
        seekBar.max = mMediaPlayer!!.duration ?: 0
        mMediaPlayer!!.setOnPreparedListener {
            mMediaPlayer.start()
            setupSeekBar()
        }
        mainActivity.ivPlay.setImageResource(R.drawable.btn_pause)


    }

    private fun setupSeekBar() {

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mMediaPlayer.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mMediaPlayer.start()
            }
        })
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val currentPosition = mMediaPlayer.currentPosition
                seekBar.progress = currentPosition

                val minutes = currentPosition / 1000 / 60
                val seconds = (currentPosition / 1000) % 60
                mainActivity.tvTimePost.text = String.format("%02d:%02d", minutes, seconds)


                val totalDuration = mMediaPlayer.duration
                val totalMinutes = totalDuration / 1000 / 60
                val totalSeconds = (totalDuration / 1000) % 60
                mainActivity.tvTimePre.text = String.format("%02d:%02d", totalMinutes, totalSeconds)
                handler.postDelayed(this, 1000)
            }

        }, 0)

    }

    private fun playSong(songUrl: String, playDuration: Int) {
//        startSongTimer(playDuration)
//        startSeekBarUpdate(playDuration)

//      if song is already playig time to click next song it will continue wo
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
        }
        try {
            mMediaPlayer.setDataSource(songUrl)
            mMediaPlayer.prepareAsync()
            // Set a listener to start playing the song
            mMediaPlayer.setOnPreparedListener {
                seekBar?.max = mMediaPlayer?.duration ?: 0
                // Start updating seekbar progress
                mMediaPlayer.start()
                // Update the play/pause button to pause state when a new song starts playing
                mainActivity.ivPlay.setImageResource(R.drawable.btn_pause)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mainActivity.ivPlay.setOnClickListener {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
                mainActivity.ivPlay.setImageResource(R.drawable.btn_play)
            } else {
                try {
                    mMediaPlayer.reset()
                    mMediaPlayer.setDataSource(songUrl) // Set data source here

                    mMediaPlayer.prepareAsync()

                    mMediaPlayer.setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.start()
                        mainActivity.ivPlay.setImageResource(R.drawable.btn_pause)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        //        mainActivity.ivPlay.setOnClickListener {
//            if (mMediaPlayer.isPlaying) {
//                mMediaPlayer.pause()
//                mainActivity.ivPlay.setImageResource(R.drawable.btn_play)
//            } else {
//                try {
//                    mMediaPlayer.reset()
//                    mMediaPlayer?.setDataSource(item.preview)
//                    mMediaPlayer.prepare()
//                    mMediaPlayer.start()
//                    mainActivity.ivPlay.setImageResource(R.drawable.btn_pause)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }
//        }

    }

    private fun songNames(data: MyMusic.Data? = null) {
        data?.let {
            songTitle.text = it.title
            singerName.text = it.artist.name
            Picasso.get().load(it.album.cover).into(imageView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressed()
    }


}

