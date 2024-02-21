package com.reyaz.allegallery.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.reyaz.allegallery.ImagesViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GallerySliderScreen(
    modifier: Modifier = Modifier,
    imagesViewModel: ImagesViewModel
) {
    val uriList by imagesViewModel.allUri.collectAsStateWithLifecycle()
    val currentlySelectedPosition by imagesViewModel.currentSelectedPosition.collectAsStateWithLifecycle()
    Box(modifier = modifier) {
        val pagerState = rememberPagerState()

        LaunchedEffect(currentlySelectedPosition) {
            if (currentlySelectedPosition == pagerState.currentPage) return@LaunchedEffect
            pagerState.scrollToPage(currentlySelectedPosition)
        }

        LaunchedEffect(pagerState.currentPage) {
            imagesViewModel.onSelect(pagerState.currentPage)
        }

        AllePager(uriList, pagerState)
        AlleScroller(uriList, currentlySelectedPosition, imagesViewModel::onSelect)
    }
}

@Composable
private fun BoxScope.AlleScroller(
    uriList: List<Uri>,
    currentlySelectedPosition: Int,
    onSelect: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    val context = LocalContext.current
    val currentItem by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstOrNull {
                val center = it.offset + it.size.div(2)
                center > 0
            }?.index ?: currentlySelectedPosition
        }
    }

    LaunchedEffect(currentItem) {
        onSelect(currentItem)
    }

    val centerPixel by remember {
        derivedStateOf {
            context.resources.displayMetrics.widthPixels.div(2)
        }
    }

    val horizontalPaddingValue by remember {
        derivedStateOf {
            val density = context.resources.displayMetrics.density
            val centerDp = centerPixel.div(density).dp
            centerDp.minus(30.dp)
        }
    }
    LazyRow(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 16.dp)
            .height(80.dp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        contentPadding = PaddingValues(
            horizontal = horizontalPaddingValue
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
    ) {
        items(uriList.size) { index ->
            val currentUri = uriList[index]
            val height by animateDpAsState(targetValue = if (index == currentlySelectedPosition) 80.dp else 64.dp, label = "")
            val width by animateDpAsState(targetValue = if (index == currentlySelectedPosition) 60.dp else 48.dp, label = "")
            Image(
                modifier = Modifier
                    .height(height)
                    .width(width)
                    .border(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Gray,
                        width = 1.dp
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(currentlySelectedPosition != index) {
                        onSelect(index)
                    },
                painter = rememberAsyncImagePainter(model = currentUri),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun AllePager(uriList: List<Uri>, pagerState: PagerState) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        count = uriList.size,
        state = pagerState,
        key = { it },
    ) { index ->
        val currentUri = uriList[index]
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = rememberAsyncImagePainter(model = currentUri),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth,
        )
    }
}