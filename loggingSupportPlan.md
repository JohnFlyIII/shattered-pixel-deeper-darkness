# Logging Support Plan using Bugfender

## Overview
This plan outlines the implementation of robust logging capabilities using Bugfender for remote log capture and analysis. This will help with troubleshooting, crash reporting, and understanding user behavior patterns.

## Implementation Status
All core logging infrastructure has been implemented and is ready for testing. Strategic logging points can be added incrementally to enhance the system over time. The implementation includes:

1. ✅ Bugfender SDK integration
2. ✅ GameLogger abstraction layer
3. ✅ User preferences for controlling logging
4. ✅ UI integration in settings
5. ✅ Privacy-respecting options (opt-in only)
6. ✅ Basic crash handling

## Implementation Steps

### 1. Bugfender SDK Integration
- **Task**: Add Bugfender SDK dependencies and initialize with API key
- **Status**: Completed
- **Notes**:
  - Add dependency to build.gradle:
    ```gradle
    implementation 'com.bugfender.sdk:android:3.+'
    ```
  - Initialize in ShatteredPixelDungeon class (or create Application subclass if needed):
    ```java
    Bugfender.init(this, "tnPDmLKTLQSskiYKlgZMZ1D1mD6ss17J", BuildConfig.DEBUG);
    Bugfender.enableCrashReporting();
    Bugfender.enableUIEventLogging(this);
    ```
  - Add required permissions to AndroidManifest.xml:
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    ```

### 2. Create GameLogger Abstraction Layer
- **Task**: Create a logging wrapper to handle both local and remote logs
- **Status**: Completed
- **Notes**:
  - Implement a GameLogger class to abstract logging implementation details
  - Provide methods for different log levels (debug, info, warning, error)
  - Add toggle capability for local logging (logcat) and remote logging (Bugfender)
  - Example implementation:
    ```java
    public class GameLogger {
        private static boolean localLoggingEnabled = true;
        private static boolean remoteLoggingEnabled = false;
        private static final String TAG = "ShatteredPD";
        
        public static void init(boolean localEnabled, boolean remoteEnabled) {
            localLoggingEnabled = localEnabled;
            remoteLoggingEnabled = remoteEnabled;
        }
        
        public static void debug(String message) {
            if (localLoggingEnabled) Log.d(TAG, message);
            if (remoteLoggingEnabled) Bugfender.d(TAG, message);
        }
        
        // Similar methods for info, warning, error
    }
    ```

### 3. Integrate with Existing Logging
- **Task**: Identify and utilize existing logging infrastructure
- **Status**: Completed
- **Notes**:
  - Inspect the codebase for existing logging functionality (e.g., in Game.java or utils)
  - Modify existing logging calls to use the new GameLogger instead of direct logging
  - Minimize code changes by creating adapters or extending existing logging classes
  - Consider adding a utils.GameDebug class that wraps existing logging mechanisms

### 4. Add User Preferences for Logging
- **Task**: Implement user-configurable logging settings
- **Status**: Completed
- **Notes**:
  - Add settings in SPDSettings.java:
    ```java
    public static boolean localLogging() {
        return SPDSettings.getBoolean("localLogging", false);
    }
    
    public static boolean remoteLogging() {
        return SPDSettings.getBoolean("remoteLogging", false);
    }
    ```
  - Create UI toggles in the settings screen with appropriate descriptions
  - Ensure clear language about data collection and privacy implications
  - Add logging preference saving/loading in the settings class

### 5. Implement Uncaught Exception Handler
- **Task**: Add custom exception handler for crash reporting
- **Status**: Completed
- **Notes**:
  - Implement Thread.UncaughtExceptionHandler in the application class
  - Log complete stack traces to Bugfender before crash
  - Consider adding device information and game state to crash reports
  - Example implementation:
    ```java
    Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
        GameLogger.error("FATAL CRASH: " + ex.getMessage());
        GameLogger.error(Log.getStackTraceString(ex));
        // Add game state information if available
        if (Dungeon.hero != null) {
            GameLogger.error("Game state: Level=" + Dungeon.depth + ", Hero=" + Dungeon.hero.heroClass);
        }
        // Let the default handler run
        defaultExceptionHandler.uncaughtException(thread, ex);
    });
    ```

### 6. Add Strategic Logging Points
- **Task**: Identify and implement key logging checkpoints
- **Status**: Started
- **Notes**:
  - Add logging in application lifecycle methods (create, resume, pause, destroy)
  - Log performance metrics at critical points (level loading, scene transitions)
  - Log user events (game start, level completion, death, important item acquisition)
  - Log error conditions and recovery attempts
  - Focus on information that helps troubleshoot crashes and performance issues

### 7. Device Identification
- **Task**: Implement secure device identification for logs
- **Status**: Completed
- **Notes**:
  - Generate a unique device ID without personal identifiers
  - Use Bugfender's device association capabilities
  - Allow users to provide their device ID when reporting issues
  - Example:
    ```java
    String deviceId = Bugfender.getDeviceIdentifier();
    // Display this in an accessible location in settings for support
    ```

### 8. Test and Validate
- **Task**: Verify logging functionality and performance impact
- **Status**: Started
- **Notes**:
  - Test on various devices to ensure minimal performance impact
  - Verify logs are properly captured in Bugfender dashboard
  - Test crash reporting functionality
  - Ensure toggle settings work correctly
  - Document any performance concerns

## Privacy Considerations
- Ensure all logging respects user privacy
- Do not log personally identifiable information
- Make remote logging opt-in only
- Add clear privacy policy language about log data collection
- Implement data retention policies in Bugfender