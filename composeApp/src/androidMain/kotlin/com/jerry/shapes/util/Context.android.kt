package com.jerry.shapes.util

import android.content.Context

// actual typealias PlatformContext = android.content.Context

// TODO Clean this up
 actual abstract class PlatformContext
 class AndroidPlatformContext(val context: Context) : PlatformContext()