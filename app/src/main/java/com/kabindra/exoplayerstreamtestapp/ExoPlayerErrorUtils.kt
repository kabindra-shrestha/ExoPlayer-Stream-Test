package com.kabindra.exoplayerstreamtestapp

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ExoPlayerErrorUtils {

    fun errorHandler(e: ExoPlaybackException): String {
        var errorString = ""

        when (e.type) {
            ExoPlaybackException.TYPE_SOURCE -> {
                loggerDebug(
                    TAG,
                    ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + e.sourceException.message
                )
                errorString =
                    ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + e.sourceException.message

                val cause = e.sourceException
                cause.printStackTrace()
                when (cause) {
                    is FileDataSource.FileDataSourceException -> {
                        val fileError = cause
                        errorString = if (fileError.cause!!.cause is FileNotFoundException) {
                            ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + ErrorType.ERROR_EXO_PLAYER_FILE_NOT_FOUND_EXCEPTION
                        } else {
                            ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + ErrorType.ERROR_EXO_PLAYER_COMMON_EXCEPTION
                        }
                    }
                    is HttpDataSource.HttpDataSourceException -> {
                        val httpError = cause
                        val requestDataSpec = httpError.dataSpec

                        errorString =
                            if (httpError is HttpDataSource.InvalidResponseCodeException) {
                                if (httpError.responseCode == 401) {
                                    // fetch video token and play again
                                }
                                ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + httpError.responseCode.toString() + " - " + httpError.responseMessage
                            } else if (httpError.cause is ConnectException) {
                                ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + ErrorType.ERROR_EXO_PLAYER_CONNECT_EXCEPTION
                            } else if (httpError.cause is SocketTimeoutException) {
                                ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + ErrorType.ERROR_EXO_PLAYER_SOCKET_TIMEOUT_EXCEPTION
                            } else if (httpError.cause is UnknownHostException) {
                                ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + ErrorType.ERROR_EXO_PLAYER_UNKNOWN_HOST_EXCEPTION
                            } else {
                                ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + httpError.cause.toString() /*+ " - " + httpError.cause?.message.toString()*/
                            }
                    }
                    else -> {
                        errorString =
                            ExoPlaybackException.TYPE_SOURCE.toString() + "\n" + cause.toString()
                    }
                }
            }

            ExoPlaybackException.TYPE_RENDERER -> {
                loggerDebug(
                    TAG,
                    ExoPlaybackException.TYPE_RENDERER.toString() + "\n" + e.rendererException.message
                )
                errorString =
                    ExoPlaybackException.TYPE_RENDERER.toString() + "\n" + e.rendererException.message
            }

            ExoPlaybackException.TYPE_UNEXPECTED -> {
                loggerDebug(
                    TAG,
                    ExoPlaybackException.TYPE_UNEXPECTED.toString() + "\n" + e.unexpectedException.message
                )
                errorString =
                    ExoPlaybackException.TYPE_UNEXPECTED.toString() + "\n" + e.unexpectedException.message
            }
            ExoPlaybackException.TYPE_REMOTE -> {
                loggerDebug(
                    TAG,
                    ExoPlaybackException.TYPE_REMOTE.toString() + "\n" + e.message
                )
                errorString = ExoPlaybackException.TYPE_REMOTE.toString() + "\n" + e.message
            }
        }

        return errorString
    }

}