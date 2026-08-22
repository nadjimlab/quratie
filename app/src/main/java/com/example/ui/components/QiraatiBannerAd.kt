package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

private const val GOOGLE_TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun QiraatiBannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = GOOGLE_TEST_BANNER_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
