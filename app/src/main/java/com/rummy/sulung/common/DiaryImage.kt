package com.rummy.sulung.common

import com.rummy.sulung.R

object DiaryImage {
    fun setDrinkImg(emotion: Int, drinkType: Int) : Int {
        return when(emotion * 10 + drinkType){
            0 -> R.drawable.pleasure_soju
            1 -> R.drawable.pleasure_beer
            2 -> R.drawable.pleasure_makgeolli
            3 -> R.drawable.pleasure_liquor
            4 -> R.drawable.pleasure_cocktail
            5 -> R.drawable.pleasure_wine
            6 -> R.drawable.pleasure_sake
            7 -> R.drawable.pleasure_champagne
            8 -> R.drawable.pleasure_highball

            10 -> R.drawable.sadness_soju
            11 -> R.drawable.sadness_beer
            12 -> R.drawable.sadness_makgeolli
            13 -> R.drawable.sadness_liquor
            14 -> R.drawable.sadness_cocktail
            15 -> R.drawable.sadness_wine
            16 -> R.drawable.sadness_sake
            17 -> R.drawable.sadness_champagne
            18 -> R.drawable.sadness_highball

            20 -> R.drawable.depressed_soju
            21 -> R.drawable.depressed_beer
            22 -> R.drawable.depressed_makgeolli
            23 -> R.drawable.depressed_liquor
            24 -> R.drawable.depressed_cocktail
            25 -> R.drawable.depressed_wine
            26 -> R.drawable.depressed_sake
            27 -> R.drawable.depressed_champagne
            28 -> R.drawable.depressed_highball

            30 -> R.drawable.aggro_soju
            31 -> R.drawable.aggro_beer
            32 -> R.drawable.aggro_makgeolli
            33 -> R.drawable.aggro_liquor
            34 -> R.drawable.aggro_cocktail
            35 -> R.drawable.aggro_wine
            36 -> R.drawable.aggro_sake
            37 -> R.drawable.aggro_champagne
            38 -> R.drawable.aggro_highball

            40 -> R.drawable.tranquility_soju
            41 -> R.drawable.tranquility_beer
            42 -> R.drawable.tranquility_makgeolli
            43 -> R.drawable.tranquility_liquor
            44 -> R.drawable.tranquility_cocktail
            45 -> R.drawable.tranquility_wine
            46 -> R.drawable.tranquility_sake
            47 -> R.drawable.tranquility_champagne
            48 -> R.drawable.tranquility_highball

            50 -> R.drawable.contrats_soju
            51 -> R.drawable.contrats_beer
            52 -> R.drawable.contrats_makgeolli
            53 -> R.drawable.contrats_liquor
            54 -> R.drawable.contrats_cocktail
            55 -> R.drawable.contrats_wine
            56 -> R.drawable.contrats_sake
            57 -> R.drawable.contrats_champagne
            58 -> R.drawable.contrats_highball

            60 -> R.drawable.intoxication_soju
            61 -> R.drawable.intoxication_beer
            62 -> R.drawable.intoxication_makgeolli
            63 -> R.drawable.intoxication_liquor
            64 -> R.drawable.intoxication_cocktail
            65 -> R.drawable.intoxication_wine
            66 -> R.drawable.intoxication_sake
            67 -> R.drawable.intoxication_champagne
            68 -> R.drawable.intoxication_highball

            else -> return R.drawable.pleasure_soju
        }
        //return R.drawable.aggro_beer
    }

    fun setDrinkLottieImg(emotion: Int, drinkType: Int) : Int {
        return when(emotion * 10 + drinkType){
            0 -> R.raw.soju_happy
            1 -> R.raw.beer_happy
            2 -> R.raw.makgeolli_happy
            3 -> R.raw.liquor_happy
            4 -> R.raw.cocktail_happy
            5 -> R.raw.wine_happy
            6 -> R.raw.sake_happy
            7 -> R.raw.champagne_happy
            8 -> R.raw.highball_happy

            10 -> R.raw.soju_sad
            11 -> R.raw.beer_sad
            12 -> R.raw.makgeolli_sad
            13 -> R.raw.liquor_sad
            14 -> R.raw.cocktail_sad
            15 -> R.raw.wine_sad
            16 -> R.raw.sake_sad
            17 -> R.raw.champagne_sad
            18 -> R.raw.highball_sad

            20 -> R.raw.soju_gloom
            21 -> R.raw.beer_gloom
            22 -> R.raw.makgeolli_gloom
            23 -> R.raw.liquor_gloom
            24 -> R.raw.cocktail_gloom
            25 -> R.raw.wine_gloom
            26 -> R.raw.sake_gloom
            27 -> R.raw.champagne_gloom
            28 -> R.raw.highball_gloom

            30 -> R.raw.soju_angry
            31 -> R.raw.beer_angry
            32 -> R.raw.makgeolli_angry
            33 -> R.raw.liquor_angry
            34 -> R.raw.cocktail_angry
            35 -> R.raw.wine_angry
            36 -> R.raw.sake_angry
            37 -> R.raw.champagne_angry
            38 -> R.raw.highball_angry

            40 -> R.raw.soju_calm
            41 -> R.raw.beer_calm
            42 -> R.raw.makgeolli_calm
            43 -> R.raw.liquor_calm
            44 -> R.raw.cocktail_calm
            45 -> R.raw.wine_calm
            46 -> R.raw.sake_calm
            47 -> R.raw.champagne_calm
            48 -> R.raw.highball_calm

            50 -> R.raw.soju_congratulate
            56 -> R.raw.sake_congratulate
            55 -> R.raw.wine_congratulate
            54 -> R.raw.cocktail_congratulate
            53 -> R.raw.liquor_congratulate
            52 -> R.raw.makgeolli_congratulate
            58 -> R.raw.highball_congratulate
            57 -> R.raw.champagne_congratulate
            51 -> R.raw.beer_congratulate

            60 -> R.raw.soju_drunk
            61 -> R.raw.beer_drunk
            62 -> R.raw.makgeolli_drunk
            63 -> R.raw.liquor_drunk
            64 -> R.raw.cocktail_drunk
            65 -> R.raw.wine_drunk
            66 -> R.raw.sake_drunk
            67 -> R.raw.champagne_drunk
            68 -> R.raw.highball_drunk

            else -> return R.raw.soju_happy
        }
        //return R.drawable.aggro_beer
    }
}