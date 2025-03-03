# Shattered Pixel Dungeon Deeper Darkness - TODO List

This file contains a list of suggested changes from the recommended-changes.md document that should be implemented for our fork.

## Application Icon and Title Screen
- Update the title screen graphics located at `/core/src/main/assets/interfaces/banners.png`
- Update icons for each platform:
  - Android (debug): `/android/src/debug/res`
  - Android (release): `/android/src/main/res`
  - Desktop: `/desktop/src/main/assets/icons`
  - iOS: `/ios/assets/Assets.xcassets`

## Credits & Supporter Button
- Add ourselves to the credits in `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/AboutScene.java`
- Consider modifying the supporter link in `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/SupporterScene.java`
- Consider disabling the supporter nag window by modifying `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSupportPrompt.java` or its trigger in `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/keys/SkeletonKey.java`
- Remember that due to GPLv3 license, we must not remove existing credits from AboutScene.java

## Update Notification
- Disable the GitHub update notification by changing `:services:updates:githubUpdates` to `:services:updates:debugUpdates` in the build.gradle files for desktop and android modules
- OR modify the GitHub updates in `/services/updates/githubUpdates/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/updates/GitHubUpdates.java` to point to our own GitHub repository

## News Feed
- Consider disabling the news feed by commenting out the line `add(btnNews);` in `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java`
- OR modify the news feed URLs in `/services/news/shatteredNews/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/news/ShatteredNews.java` to point to our own feed

## Translations
- If we plan to add new text to the game, consider:
  - Removing all languages except English from `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/messages/Languages.java`
  - Removing translated .properties files from `/core/src/main/assets/messages`
  - Removing the language picker from `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSettings.java`

## Package Structure
- Note that changing the package structure (folder names) is optional since we've updated the appPackageName variable in build.gradle