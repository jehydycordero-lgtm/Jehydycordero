package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AddonSeedData
import com.example.data.model.AddonSource
import com.example.data.pack.McAddonPackager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `app name matches BedrockHub`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BedrockHub", appName)
  }

  @Test
  fun `seed catalog includes both free in-app and marketplace addons`() {
    val addons = AddonSeedData.initialAddons
    assertTrue(addons.any { it.source == AddonSource.FREE_IN_APP && it.isFreeInApp })
    assertTrue(addons.any { it.source == AddonSource.MARKETPLACE && it.minecoins > 0 })
    assertTrue(addons.any { it.source == AddonSource.MARKETPLACE && it.minecoins == 0 })
  }

  @Test
  fun `packager builds valid mcaddon zip with manifest and icon`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val packager = McAddonPackager(context)
    val freeAddon = AddonSeedData.initialAddons.first { it.isFreeInApp }
    val file = packager.buildPackFile(freeAddon)

    assertTrue(file.exists())
    ZipFile(file).use { zip ->
      assertTrue(zip.getEntry("behavior_pack/manifest.json") != null)
      assertTrue(zip.getEntry("behavior_pack/pack_icon.png") != null)
      assertTrue(zip.getEntry("resource_pack/manifest.json") != null)
    }
    file.delete()
  }
}
