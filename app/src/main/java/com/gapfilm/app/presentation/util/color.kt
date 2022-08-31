package com.gapfilm.app.presentation.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

fun AppCompatActivity.getRColor(color: Int) = ContextCompat.getColor(applicationContext, color)
