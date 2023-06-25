package com.example.tiptime

import org.junit.Assert
import org.junit.Test
import java.text.NumberFormat

class TipCalculatorTests {

    @Test
    fun calculate_20_percent_tip_no_roundup(){
        val amount = 10.0
        val tipPercent = 20.0

        val actualTip = calculateTip(amount, false, tipPercent)

        val expectedTip = NumberFormat.getCurrencyInstance().format(2)

        Assert.assertEquals(expectedTip, actualTip)

    }
}