![alt text][logo]

[logo]: http://i.imgur.com/fzK5HKl.jpg

# ProjektDashboard 
[![Build Status](https://travis-ci.org/nicholaschum/ProjektDashboard.svg?branch=master)](https://travis-ci.org/nicholaschum/ProjektDashboard)

projekt dashboard. is a CyanogenMod Theme Engine modification utility for chummy development team themes; a universal Contextual Header Swapper for all themes out there on Play Store and a set of tools to help you diagnose and clean up your device.

### _Instructions: Color Switcher (cdt themes ONLY)_
- The first time using the switcher it is recommended to NOT clear your Theme Cache. 
- After picking a color and seeing my lame pickup lines, restart SystemUI on a cdt theme applied. 
- BY USING THIS ON A CDT THEME, YOU WILL BREAK BATTERY STATS due to Marshmallow's Java tint mode on the Battery Chart. We are actively working to fix this and as your nightlies progress, so should this be automatically fixed.

### _Instructions: Contextual Header Swapper_
- If you are looking to edit someone else's theme (non-cdt), make sure you know the package identifier of the third party theme e.g. "com.kohlewrrk.radius" and type it in. You get options for image selection and cropping.
- After that, it is REQUIRED to do a soft reboot. 
- If after soft rebooting you don't get anything changed, do it again with AUTOCLEAR SYSTEMUI CACHE on the theme enabled and soft reboot (at most twice). This will hardcode your drawables into the base apk, and you can patch up another theme apk if you want for example another version of Radius with Arcus. This process does not have to be repeated unless you update your theme.

### _ATTENTION_ 
- Color Picker works off the cache thus hotswapping is allowed, but when you modify the header, the cache is destroyed, so you must soft reboot...THEN pick a color.
