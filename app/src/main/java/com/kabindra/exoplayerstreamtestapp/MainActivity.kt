package com.kabindra.exoplayerstreamtestapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.DebugTextViewHelper
import com.kabindra.exoplayerstreamtestapp.ExoPlayerErrorUtils.errorHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var playerViewMain: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var progressBarMain: ProgressBar? = null
    private var containerErrorMain: ConstraintLayout? = null
    private var textViewMainErrorMessage: TextView? = null
    private var textViewMainErrorCode: TextView? = null
    private var containerDescription: ConstraintLayout? = null
    private var textViewInfo: TextView? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerViewMain = player_view_main
        progressBarMain = progress_bar_main
        containerErrorMain = container_error_main
        textViewMainErrorMessage = text_view_main_error_message
        textViewMainErrorCode = text_view_main_error_code
        containerDescription = container_description
        textViewInfo = text_view_info

        showInfo(false)

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
        showError(false)
        showInfo(true)

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

        playerViewMain!!.player = player

        var mediaItem: MediaItem

        mediaItem = MediaItem.Builder()
            .setUri(stream)
            // .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        player!!.addMediaItem(mediaItem)

        playWhenReady = true
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)

        val debugTextViewHelper= DebugTextViewHelper(player!!, textViewInfo!!)
        debugTextViewHelper.start()

        Log.d("MainActivity", "Current  Playing Home $stream")

        player!!.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                var stateString: String = ""

                when (playbackState) {
                    ExoPlayer.STATE_IDLE -> {
                        stateString = "ExoPlayer.STATE_IDLE      -"
                        showPlayerProgressBar(progressBarMain!!, false)
                    }
                    ExoPlayer.STATE_BUFFERING -> {
                        stateString = "ExoPlayer.STATE_BUFFERING -"
                        showPlayerProgressBar(progressBarMain!!, true)
                    }
                    ExoPlayer.STATE_READY -> {
                        stateString = "ExoPlayer.STATE_READY     -"
                        showPlayerProgressBar(progressBarMain!!, false)
                    }
                    ExoPlayer.STATE_ENDED -> {
                        stateString = "ExoPlayer.STATE_ENDED     -"
                        showPlayerProgressBar(progressBarMain!!, false)
                    }
                    else -> {
                        stateString = "UNKNOWN_STATE             -"
                        showPlayerProgressBar(progressBarMain!!, false)
                    }
                }

                Log.d("MainActivity", "onPlaybackStateChanged to $stateString")
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)

                showPlayerProgressBar(progressBarMain!!, false)

                releasePlayer()

                showInfo(false)

                try {
                    var errorResponseCode = ""
                    if (error.sourceException is HttpDataSource.InvalidResponseCodeException) {
                        errorResponseCode =
                            "-" + (error.sourceException as HttpDataSource.InvalidResponseCodeException).responseCode
                    }

                    textViewMainErrorMessage!!.text =
                        getString(R.string.exo_player_error_message)
                    /*textViewMainPlayerErrorSupport!!.text =
                        getString(R.string.exo_player_error_support)*/
                    textViewMainErrorCode!!.text =
                        getString(R.string.exo_player_error_prefix_code) + errorHandler(error)

                    showError(true)
                } catch (e: Exception) {
                    textViewMainErrorMessage!!.text =
                        getString(R.string.exo_player_error_message)
                    /*textViewMainPlayerErrorSupport!!.text =
                        getString(R.string.exo_player_error_support)*/
                    textViewMainErrorCode!!.text =
                        getString(R.string.exo_player_unknown_exception_code) + errorHandler(error)

                    showError(true)
                }
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

    private fun showSoftKeyboard(view: View, activity: Activity, show: Boolean) {
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

    fun showPlayerProgressBar(progressBar: ProgressBar, show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    fun showError(show: Boolean) {
        if (show) {
            containerErrorMain!!.visibility = View.VISIBLE
        } else {
            containerErrorMain!!.visibility = View.GONE
        }
    }

    fun showInfo(show: Boolean) {
        if (show) {
            containerDescription!!.visibility = View.VISIBLE
        } else {
            containerDescription!!.visibility = View.GONE
        }
    }
}