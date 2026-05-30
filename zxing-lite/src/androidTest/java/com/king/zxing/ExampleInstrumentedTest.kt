package com.king.zxing

import android.content.Context
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext: Context = InstrumentationRegistry.getTargetContext()
        assertEquals("com.king.zxing.test", appContext.packageName)
    }
}
