package com.ludovic.vimont.deezeropenapi.player

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.helper.AndroidVersions

/**
 * Used to display a media friendly notification to allow the user to interact with the current
 * MediaSession.
 * @see: https://stackoverflow.com/questions/21872022/notification-for-android-music-player
 */
class MediaNotificationBuilder {
    companion object {
        const val NOTIFICATION_ID = 19_920_509

        private const val CHANNEL_ID = "com.ludovic.vimont.deezeralbums.AUDIO_PLAYER"
        private const val CHANNEL_NAME = "Music Player"
        private const val CHANNEL_DESCRIPTION = "Play album tracks preview, in background"
    }

    fun buildNotification(context: Context, mediaSessionCompat: MediaSessionCompat, currentTrack: Int, isPlaying: Boolean = false): Notification? {
        val controller: MediaControllerCompat = mediaSessionCompat.controller
        val mediaMetadata: MediaMetadataCompat = mediaSessionCompat.controller.metadata
        val description: MediaDescriptionCompat = mediaMetadata.description

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (AndroidVersions.atLeastOreo) {
            createNotificationChannel(notificationManager)
        }

        val previousString: String = context.getString(R.string.player_previous)
        val previousIntent: NotificationCompat.Action? = if (currentTrack == 0) {
            NotificationCompat.Action(0, previousString, null)
        } else {
            getAction(context, R.drawable.ic_previous, previousString, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        }

        val playState: PlayState = getPlayState(context, isPlaying)

        val nextString: String = context.getString(R.string.player_next)
        val nextIntent: NotificationCompat.Action? = if (currentTrack == controller.queue.size-1) {
            NotificationCompat.Action(0, nextString, null)
        } else {
            getAction(context, R.drawable.ic_next, nextString, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        }

        val notificationCompat: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setSmallIcon(R.drawable.ic_music)
                .setLargeIcon(description.iconBitmap)
                .setOngoing(false)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(createMediaStyle(context, mediaSessionCompat))
                .setContentIntent(getContentIntent(context, controller))
                .setDeleteIntent(getDeleteIntent(context))
                .addAction(previousIntent)
                .addAction(getAction(context, playState.drawable, playState.displayedText, playState.playbackStateAction))
                .addAction(nextIntent)

        return notificationCompat.build()
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        channel.description = CHANNEL_DESCRIPTION
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.setSound(null, null)
        channel.enableVibration(false)
        channel.setShowBadge(false)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getPlayState(context: Context, isPlaying: Boolean): PlayState {
        return if (isPlaying) {
            val pauseString: String = context.getString(R.string.player_pause)
            PlayState(R.drawable.ic_pause, pauseString, PlaybackStateCompat.ACTION_PAUSE)
        } else {
            val playString: String = context.getString(R.string.player_play)
            PlayState(R.drawable.ic_play, playString, PlaybackStateCompat.ACTION_PLAY)
        }
    }

    private fun createMediaStyle(context: Context, mediaSession: MediaSessionCompat): androidx.media.app.NotificationCompat.MediaStyle? {
        return androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setCancelButtonIntent(getCancelIntent(context))
    }

    private fun getContentIntent(context: Context, controller: MediaControllerCompat): PendingIntent {
        context.packageManager.getLaunchIntentForPackage(context.packageName)?.let { intent ->
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return controller.sessionActivity
    }

    private fun getDeleteIntent(context: Context): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context, PlaybackStateCompat.ACTION_STOP
        )
    }

    private fun getCancelIntent(context: Context): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context, PlaybackStateCompat.ACTION_STOP
        )
    }

    private fun getAction(context: Context, drawable: Int, title: String, action: Long): NotificationCompat.Action {
        return NotificationCompat.Action(drawable, title,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context, action
            )
        )
    }
}