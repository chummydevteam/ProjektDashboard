cd /Users/Nicholas/Documents/GitHub/ProjektDashboard/

./gradlew assembleDebug

cp /Users/Nicholas/Documents/GitHub/ProjektDashboard/app/build/outputs/apk/projekt_dashboard_cdt_beta_testers.apk /Users/Nicholas/Dropbox/Public/projekt_dashboard_cdt_beta_testers.apk

osascript -e 'tell application "Terminal" to quit' &
exit