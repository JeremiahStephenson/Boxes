package com.jerry.shapes.util

import android.content.Context

actual abstract class PlatformContext
class AndroidPlatformContext(val context: Context) : PlatformContext()