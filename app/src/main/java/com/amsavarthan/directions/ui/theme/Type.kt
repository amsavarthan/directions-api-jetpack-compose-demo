package com.amsavarthan.directions.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.amsavarthan.directions.R

val fonts = FontFamily(
    Font(R.font.black, weight = FontWeight.Black),
    Font(R.font.blackitalic, weight = FontWeight.Black, style = FontStyle.Italic),
    Font(R.font.bold, weight = FontWeight.Bold),
    Font(R.font.bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.extrabolditalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
    Font(R.font.extralight, weight = FontWeight.ExtraLight),
    Font(R.font.extralightitalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(R.font.italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.light, weight = FontWeight.Light),
    Font(R.font.lightitalic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.medium, weight = FontWeight.Medium),
    Font(R.font.mediumitalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.regular, weight = FontWeight.Normal),
    Font(R.font.semibold, weight = FontWeight.SemiBold),
    Font(R.font.semibolditalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.thin, weight = FontWeight.Thin),
    Font(R.font.thinitalic, weight = FontWeight.Thin, style = FontStyle.Italic),
)

val Typography = Typography(
    defaultFontFamily = fonts
)