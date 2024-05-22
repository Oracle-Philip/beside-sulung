package com.rummy.sulung.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rummy.sulung.presentation.R

// Set of Material typography styles to start with

val nanum_bareunhipi = FontFamily(Font(R.font.nanum_bareunhipi))
val pretendard_bold = FontFamily(Font(R.font.pretendard_bold))
val pretendard_medium = FontFamily(Font(R.font.pretendard_medium))
val pretendard_regular = FontFamily(Font(R.font.pretendard_regular))
val pretendard_semibold = FontFamily(Font(R.font.pretendard_semibold))

private val defaultTypography = Typography()

val Typography = Typography(
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = pretendard_regular),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = pretendard_regular),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = pretendard_regular),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = pretendard_regular),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = pretendard_regular),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = pretendard_regular),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = pretendard_regular),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = pretendard_regular),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = pretendard_regular),
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = pretendard_regular),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = pretendard_regular),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = pretendard_regular),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = pretendard_regular),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = pretendard_regular),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = pretendard_regular),
)

//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)