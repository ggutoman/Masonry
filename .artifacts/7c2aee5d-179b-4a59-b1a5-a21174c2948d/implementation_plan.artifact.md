# Fix NullPointerException in SMSApp

The `NullPointerException` occurs in the `SMSApp` constructor because the `SmsManager` is being retrieved using `Context.getSystemService(SmsManager.class)` on devices where this service is not yet available (API < 31), or it returns null. Specifically, the current check uses `Build.VERSION_CODES.Q` (API 29) as the threshold, but `SmsManager` was only added as a system service in API 31 (Android 12).

## Proposed Changes

### appdriver Component

#### [MODIFY] [SMSApp.kt](file:///D:/AndroidStudioProjects/Masonry/appdriver/src/main/java/org/gag/appdriver/App/Core/SMSApp.kt)
- Update the `smsManager` initialization to use `Build.VERSION_CODES.S` (API 31) as the threshold.
- Make `smsManager` nullable to safely handle cases where the system service might not be available.
- Update `SendSms` to use safe call `?.` when accessing `smsManager`.

## Verification Plan

### Automated Tests
- Run the app on a device/emulator with API 30 (Android 11) and trigger the SMS sending flow to ensure it no longer crashes.
- Run the app on a device/emulator with API 31+ to ensure it still works.

### Manual Verification
- Verify that the crash no longer occurs in `Fragment_Member` when trying to send the ALSM ID.
