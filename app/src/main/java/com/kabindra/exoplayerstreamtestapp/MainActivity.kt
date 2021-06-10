package com.kabindra.exoplayerstreamtestapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = player_view_main

        onClickListener()
    }

    override fun onPause() {
        super.onPause()

        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        releasePlayer()
    }

    private fun onClickListener() {
        button_main_stream.setOnClickListener {
            showSoftKeyboard(root_main, this, false)

            val validated = edit_text_main_stream.isValidText()

            if (validated) {
                initializePlayer(edit_text_main_stream.text.toString())

                edit_text_main_stream.text = null
            }
        }
    }

    private fun initializePlayer(stream: String) {
        if (player == null) {
            // Build a HttpDataSource.Factory with cross-protocol redirects enabled.
            val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSourceFactory(
                ExoPlayerLibraryInfo.DEFAULT_USER_AGENT,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
            )
            // Wrap the HttpDataSource.Factory in a DefaultDataSourceFactory, which adds in
            // support for requesting data from other sources (e.g., files, resources, etc).
            val dataSourceFactory = DefaultDataSourceFactory(this, httpDataSourceFactory)

            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(this)
                .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                // .setTrackSelector(trackSelector)
                .build()

            /*val mediaSourceFactory: MediaSourceFactory =
                DefaultMediaSourceFactory(RtmpDataSourceFactory())
            player = SimpleExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()*/
        }

        playerView!!.player = player

        var mediaItem: MediaItem

        mediaItem = MediaItem.Builder()
            .setUri(stream)
            // .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        player!!.addMediaItem(mediaItem)

        playWhenReady = true
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)

        Log.d("MainActivity", "Current  Playing Home $stream")

        player!!.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                var stateString: String = ""

                when (playbackState) {
                    ExoPlayer.STATE_IDLE -> {
                        stateString = "ExoPlayer.STATE_IDLE      -"
                    }
                    ExoPlayer.STATE_BUFFERING -> {
                        stateString = "ExoPlayer.STATE_BUFFERING -"
                    }
                    ExoPlayer.STATE_READY -> {
                        stateString = "ExoPlayer.STATE_READY     -"
                    }
                    ExoPlayer.STATE_ENDED -> {
                        stateString = "ExoPlayer.STATE_ENDED     -"
                    }
                    else -> {
                        stateString = "UNKNOWN_STATE             -"
                    }
                }

                Log.d("MainActivity", "onPlaybackStateChanged to $stateString")
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
            }
        })

        player!!.prepare()
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    fun showSoftKeyboard(view: View, activity: Activity, show: Boolean) {
        if (show) {
            if (view.requestFocus()) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        } else {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}