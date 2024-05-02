package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.*
import com.pierfrancescosoffritti.androidyoutubeplayer.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.toFloat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.Utils
import java.util.*
import kotlin.jvm.internal.Intrinsics

/**
 * WebView implementation of [YouTubePlayer]. The player runs inside the WebView, using the IFrame Player API.
 */
internal class WebViewYouTubePlayer constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr), YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {
    private val classes: ArrayList<String?> = ArrayList<String?>()
    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    private val youTubePlayerListeners = HashSet<YouTubePlayerListener>()
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    internal var isBackgroundPlaybackEnabled = false

    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default)
    }

    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(this)

    override fun getInstance(): YouTubePlayer = this

    override fun loadVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:loadVideo('$videoId', $startSeconds)") }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:cueVideo('$videoId', $startSeconds)") }
    }

    override fun play() {
        mainThreadHandler.post { loadUrl("javascript:playVideo()") }
    }

    override fun pause() {
        mainThreadHandler.post { loadUrl("javascript:pauseVideo()") }

    }

    override fun mute() {
        mainThreadHandler.post { loadUrl("javascript:mute()") }
    }

    override fun unMute() {
        mainThreadHandler.post { loadUrl("javascript:unMute()") }
    }

    override fun setVolume(volumePercent: Int) {
        require(!(volumePercent < 0 || volumePercent > 100)) { "Volume must be between 0 and 100" }

        mainThreadHandler.post { loadUrl("javascript:setVolume($volumePercent)") }
    }

    override fun seekTo(time: Float) {
        mainThreadHandler.post { loadUrl("javascript:seekTo($time)") }
    }

    override fun setPlaybackRate(playbackRate: PlayerConstants.PlaybackRate) {
        mainThreadHandler.post { loadUrl("javascript:setPlaybackRate(${playbackRate.toFloat()})") }
    }

    override fun setPlaybackQuality(quality: String) {
        mainThreadHandler.post { loadUrl("javascript:setPlaybackQuality('$quality')") }
    }
    override fun setPlaybackQuality(playbackQuality: PlayerConstants.PlaybackQuality) {
        mainThreadHandler.post { loadUrl("javascript:setPlaybackQuality(${playbackQuality.name})") }
    }


    private fun initialList() {
        this.classes.add("ytp-chrome-top-buttons")
        this.classes.add("ytp-title")
        this.classes.add("showinfo=0")
        this.classes.add("ytp-youtube-button ytp-button yt-uix-sessionlink")
        this.classes.add("ytp-button ytp-endscreen-next")
        this.classes.add("ytp-button ytp-endscreen-previous")
        this.classes.add("ytp-show-cards-title")
        this.classes.add("ytp-endscreen-content")
        this.classes.add("ytp-chrome-top")
        this.classes.add("ytp-pause-overlay")
        this.classes.add("ytp-title-text")
    }

    private fun hideSomeSectionOfBlog(webView: WebView?) {
        if (webView != null) {
            try {
                val it: Iterator<*> = this.classes.iterator()
                Intrinsics.checkNotNullExpressionValue(it, "this.classes.iterator()")
                while (it.hasNext()) {
                    val next = it.next()!!
                    Intrinsics.checkNotNull(
                        next,
                        "null cannot be cast to non-null type kotlin.String"
                    )
                    val str = next as String
                    hideElementByClassName(webView, str)
                    removeElementByClassName(webView, str)
                }
                hideContextMenu(webView)
                /*Handler(Looper.getMainLooper()).postDelayed(
                    `WebPlayer$$ExternalSyntheticLambda6`(
                        webView
                    ), 2
                )*/
            } catch (unused: Exception) {
            }
        }
    }


    private fun hideElementByClassName(webView: WebView, str: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("javascript:(function() { var elements = document.getElementsByClassName('")
        stringBuilder.append(str)
        stringBuilder.append("')[0].style.display='none'; })()")
        webView.loadUrl(stringBuilder.toString())
    }

    private fun removeElementByClassName(webView: WebView, str: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("javascript:(function() { var elements = document.getElementsByClassName('")
        stringBuilder.append(str)
        stringBuilder.append("'); while(elements.length > 0)elements[0].parentNode.removeChild(elements[0]);  })()")
        webView.loadUrl(stringBuilder.toString())
    }

    private fun hideContextMenu(webView: WebView) {
        webView.loadUrl("javascript:(function() { var css = document.createElement('style');  css.type = 'text/css'; var styles = '.ytp-contextmenu { width: 0px !important}';if (css.styleSheet) css.styleSheet.cssText = styles; else css.appendChild(document.createTextNode(styles));document.getElementsByTagName('head')[0].appendChild(css); })()")
    }

    override fun destroy() {
        youTubePlayerListeners.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
        super.destroy()
    }

    override fun getListeners(): Collection<YouTubePlayerListener> {
        return Collections.unmodifiableCollection(HashSet(youTubePlayerListeners))
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.add(listener)
    }

    override fun removeListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.remove(listener)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions) {
        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.domStorageEnabled = true
        initialList()

        addJavascriptInterface(YouTubePlayerBridge(this), "YouTubePlayerBridge")

        val htmlPage = Utils
            .readHTMLFromUTF8File(resources.openRawResource(R.raw.ayp_youtube_player))
            .replace("<<injectedPlayerVars>>", playerOptions.toString())

        Log.d("TAG", "initWebView: " + htmlPage)
        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)

        // if the video's thumbnail is not in memory, show a black screen
        webChromeClient = object : WebChromeClient() {
            override fun getDefaultVideoPoster(): Bitmap? {
                val result = super.getDefaultVideoPoster()

                return result ?: Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
            }
        }
    }
    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE))
            return

        super.onWindowVisibilityChanged(visibility)
    }
    object JSBridge {
        @JavascriptInterface
        fun callFromJS() {
            Log.d("TAG", "callFromJS: ")
        }
    }
}
