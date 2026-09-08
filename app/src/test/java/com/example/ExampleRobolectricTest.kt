package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("YT Voice", appName)
  }

  @Test
  fun `verify voice catalog contains required voices`() {
    assertEquals(4, VoiceCatalog.maleVoices.size)
    assertEquals(4, VoiceCatalog.femaleVoices.size)

    val maleNames = VoiceCatalog.maleVoices.map { it.name }
    assertTrue(maleNames.contains("Pria 1"))
    assertTrue(maleNames.contains("Pria 2"))
    assertTrue(maleNames.contains("Pria 3"))
    assertTrue(maleNames.contains("Pria 4"))

    val femaleNames = VoiceCatalog.femaleVoices.map { it.name }
    assertTrue(femaleNames.contains("Wanita 1"))
    assertTrue(femaleNames.contains("Wanita 2"))
    assertTrue(femaleNames.contains("Wanita 3"))
    assertTrue(femaleNames.contains("Wanita 4"))
  }
}

