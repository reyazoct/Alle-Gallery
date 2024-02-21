package com.reyaz.allegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reyaz.allegallery.ui.screens.GallerySliderScreen
import com.reyaz.allegallery.ui.screens.PermissionScreen
import com.reyaz.allegallery.ui.theme.AlleGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlleGalleryTheme {
                val imagesViewModel by viewModels<ImagesViewModel>()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uriList by imagesViewModel.allUri.collectAsStateWithLifecycle()
                    if (uriList.isEmpty()) {
                        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia()) { list ->
                            imagesViewModel.updateUriList(list)
                        }
                        PermissionScreen(
                            modifier = Modifier.fillMaxSize(),
                            onAllowClick = {
                                val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                launcher.launch(request)
                            }
                        )
                    } else {
                        GallerySliderScreen(
                            modifier = Modifier.fillMaxSize(),
                            imagesViewModel = imagesViewModel
                        )
                    }
                }
            }
        }
    }
}