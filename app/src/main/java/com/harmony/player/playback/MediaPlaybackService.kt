package com.harmony.player.playback

import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlaybackService : MediaSessionService() {
    
    private var mediaSession: androidx.media3.session.MediaSession? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: androidx.media3.session.MediaSession.ControllerInfo): androidx.media3.session.MediaSession? {
        return mediaSession
    }
}
