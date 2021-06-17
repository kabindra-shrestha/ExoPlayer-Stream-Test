package com.kabindra.exoplayerstreamtestapp

class ErrorType {

    companion object {
        const val ERROR_EXO_PLAYER_COMMON_EXCEPTION =
            "Error: Please refresh and try again, if problem persists contact support."
        const val ERROR_EXO_PLAYER_FILE_NOT_FOUND_EXCEPTION = "Error: Invalid stream."
        const val ERROR_EXO_PLAYER_SOCKET_TIMEOUT_EXCEPTION = "Error: Connection timed out."
        const val ERROR_EXO_PLAYER_UNKNOWN_HOST_EXCEPTION =
            "Error: Couldn't connect to server. Please check your network connection."
        const val ERROR_EXO_PLAYER_CONNECT_EXCEPTION = "Error: Failed to connect to server."
    }

}