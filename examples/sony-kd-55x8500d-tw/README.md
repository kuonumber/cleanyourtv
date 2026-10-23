# Sony BRAVIA KD-55X8500D, Taiwan firmware (Android TV 7.0)

Owner-tested on **Android TV 7.0 (API 24)**, Sony firmware **5.455** (`SVP4KDTV15_TW-user 7.0 NRD91N.S43`, built
September 2020), 1.5 GB RAM, 32-bit MediaTek. Android reports the generic model name `BRAVIA 4K 2015` and the device
`SVP-DTV15`: the same platform as the KD-65X9300D and XBR-55X850D lists, which run the Android 8.0 firmware that the
Taiwan variant never received. Sony Taiwan's support page marks the set "software updates ended" (1 November 2024).

## Debloat

[`debloat.txt`](debloat.txt): 24 packages disabled by default, derived from `sony-kd-65x9300d` (26 of its 31
packages exist on this build) plus the Sony recommendation feeders that fill the Leanback launcher's rows. USB media
players, DLNA, the Opera-based browser, the Japanese keyboard and Google Assistant are listed but commented out.
`com.google.android.backdrop` is kept (the only screensaver with the stock launcher) and so is
`com.sony.dtv.networkrecommendation` (on 7.0 that name matches the Wi-Fi network recommendation provider).

The privacy audit found no ACR package: no Samba TV on this firmware. Reboot-checked: all 24 stay disabled.

One promo card survives the list: `com.sony.dtv.netflixmanager` posts a "watch Netflix" recommendation 15 to 35
times a day. It also answers the remote's NETFLIX key (`GLOBAL_BUTTON`, `WAKE_ON_NETFLIX`), so it is kept and its
notifications are silenced instead:

    tools/tv.py 'appops set com.sony.dtv.netflixmanager POST_NOTIFICATION ignore'   # undo: ... allow

AppOps are written to disk lazily: a hard `reboot` over ADB lost this setting once. Set it, then reboot with
`svc power reboot` (a clean shutdown) if you want it checked across a boot.

## Launcher on Android 7.0

The upstream launcher has `minSdk 26`. The `android7-launcher` branch lowers it to 24 with:

- `coreLibraryDesugaringEnabled` + `desugar_jdk_libs` for `java.time` (eight files, no source change);
- `PackageManager.getChangedPackages` (API 26) replaced below 26 by a hash of the installed set
  (`MainActivity.legacyPackageFingerprint`);
- `Resources.getFont` replaced by `ResourcesCompat.getFont` (`UiFont`, needs `androidx.core`);
- `Files.readAllLines` replaced by a `BufferedReader` (`WallpaperCaptions`);
- `Bitmap.Config.HARDWARE` skipped below 26 (`WallpaperPlayer`).

Lint reports no `NewApi` issue at minSdk 24; the 37 + unit tests pass; the release APK is about 700 KB (the
desugared runtime adds ~230 KB). On the TV: displayed in about 3 s on a cold start, Inputs/Network/Settings pills
and the dock (Netflix, YouTube, Prime Video, Live TV = `com.sony.dtv.tvx`) render, the Home key returns to it.

`am start -W` on this firmware answers `Status: timeout` after about a second although the activity is displayed
two seconds later, so `build-and-install.sh` now watches `mResumedActivity` for up to 20 s before calling the start
a failure. Config: `live_tv_package` = `com.sony.dtv.tvx` (it also plays every HDMI input), dock as above.

Layouts that set `android:fontFamily="@string/font_*"` in XML fall back to the system font on 7.0 (the framework
cannot read `res/font` there); text built in code goes through `UiFont` and keeps Outfit.

## Restore

    TV_HOST=<tv ip> tools/restore.sh examples/sony-kd-55x8500d-tw/debloat.txt com.google.android.leanbacklauncher

The second argument matters: `restore.sh` assumes `com.google.android.tvlauncher` otherwise.
