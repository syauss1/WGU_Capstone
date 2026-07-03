WGU Capstone
Purpose
A native Android application that allows users to plan, organize, and track personal vacations and associated excursions. All data is stored locally on the device using the Room persistence library (SQLite). Users can manage vacation details, attach multiple excursions to each vacation, set date-based alerts, and share vacation information.
---
How to Operate the Application
PIN Lock
On first launch, the app asks you to create a PIN (at least 4 digits) to protect your data. Only a salted SHA-256 hash of the PIN is stored — never the PIN itself.
On every launch after that, you must enter the correct PIN before the Home Screen is reachable.
---
Home Screen
After unlocking, you land on the Home Screen.
Tap "View My Vacations" to navigate to the vacation list.
Tap "Trip Report" to open the combined search/report screen.
---
Vacation List (B1a, B2, C)
Displays all saved vacations as cards showing the title and date range.
Search box at the top filters vacations live by title or hotel.
If no vacations exist (or none match the search), an empty-state message is shown.
Tap the floating action button (FAB / +) in the bottom-right corner to create a new vacation.
Tap any vacation card to open its detail view.
---
Vacation Detail View (B2, B3a–h, C)
This screen handles both creating and editing vacations.
Field	Requirement covered
Vacation Title	B2
Hotel / Accommodation	B2
Start Date (tap to open date picker)	B2, B3c
End Date (tap to open date picker)	B2, B3c, B3d
Actions available from the top menu (⋮):
Menu item	What it does	Requirement
Save	Inserts (new) or updates (existing) the vacation	B3b
Delete	Removes the vacation — blocked if excursions exist	B3b, B1b
Share	Opens Android share sheet with all vacation details and excursions	B3f
Alert: Start Date	Schedules a notification to fire on the vacation start date	B3e
Alert: End Date	Schedules a notification to fire on the vacation end date	B3e
Validation:
All fields must be filled before saving.
Dates are selected via a date picker, ensuring correct `MM/dd/yyyy` format (B3c).
End date must be after start date (B3d).
A vacation with associated excursions cannot be deleted (B1b).
Excursion list (B3g, B3h):
The bottom of the vacation detail screen shows all excursions linked to this vacation.
Tap the FAB (+) to add a new excursion (the vacation must be saved first).
Tap any excursion row to open its detail view for editing or deletion.
---
Excursion Detail View (B4, B5a–e, C)
This screen handles both creating and editing excursions.
Field	Requirement covered
Excursion Title	B4
Excursion Date (tap to open date picker)	B4, B5c
Category (dropdown, populated from the database)	Scalable design
The category dropdown is loaded from the Category table rather than hardcoded. Selecting "Add new category..." opens a dialog that inserts a new row into the database — the new option is immediately available everywhere, with no code changes required.
Actions available from the top menu (⋮):
Menu item	What it does	Requirement
Save	Inserts (new) or updates (existing) the excursion	B5b
Delete	Removes the excursion	B5b
Set Alert	Schedules a notification to fire on the excursion date	B5d
Validation:
All fields must be filled before saving.
Date is selected via a date picker, ensuring correct `MM/dd/yyyy` format (B5c).
Excursion date must fall within the parent vacation's start and end dates (B5e).
---
Alerts and Notifications (B3e, B5d)
Alerts are set per-vacation (start or end) and per-excursion.
On the scheduled date, the device fires a system notification:
Vacation start: `"\[Vacation Title] is starting today!"`
Vacation end: `"\[Vacation Title] is ending today!"`
Excursion: `"\[Excursion Title] excursion is today!"`
Notifications require the app to have notification permissions granted (prompted on first launch on Android 13+).
---
Sharing a Vacation (B3f)
From the Vacation Detail menu, select Share.
The Android share sheet opens pre-populated with all vacation details including any linked excursions.
You can share via SMS, email, clipboard, or any installed share target.
---
Trip Report
Accessed from the Home Screen ("Trip Report" button).
Combines every vacation and excursion into one report, each row showing Type, Title, Date, and a Detail column (hotel for vacations, category + parent vacation for excursions).
A search box filters the report by keyword across both types.
The report header shows the report title and a "Generated: <date-time>" timestamp that refreshes every time the results change.
---
Android Version
The signed APK targets and is deployed to Android 8.0 (API 26 / Oreo) and higher, as required by the project specification.
`minSdkVersion`: 26
`targetSdkVersion`: 36
Tested on Android 8.0 emulator (API 26) and Android 14 (API 34)
---
Git Repository
https://github.com/syauss1/WGU_Capstone
---
Project Structure
```
app/src/main/java/com/example/wgucapstone/
├── UI/
│   ├── PinLockActivity.java       -- App-entry PIN lock (launcher activity)
│   ├── MainActivity.java          -- Home screen
│   ├── VacationList.java          -- List of all vacations, with search
│   ├── VacationDetails.java       -- Add / edit / delete vacation
│   ├── VacationAdapter.java       -- RecyclerView adapter for vacations
│   ├── ExcursionDetails.java      -- Add / edit / delete excursion, category spinner
│   ├── ExcursionAdapter.java      -- RecyclerView adapter for excursions
│   ├── TripReportActivity.java    -- Combined search + multi-column report screen
│   └── ReportAdapter.java         -- RecyclerView adapter for the polymorphic report rows
├── dao/
│   ├── VacationDAO.java           -- Data access object for vacations (incl. search)
│   ├── ExcursionDAO.java          -- Data access object for excursions
│   └── CategoryDAO.java           -- Data access object for excursion categories
├── database/
│   ├── VacationDatabase.java      -- Room database singleton, seeds default categories
│   └── Repository.java            -- Single data access point for all UI classes
├── entities/
│   ├── Vacation.java              -- Room entity: vacationID, title, hotel, startDate, endDate
│   ├── Excursion.java             -- Room entity: excursionID, title, date, vacationID, category
│   ├── Category.java              -- Room entity: categoryID, name
│   ├── TripItem.java              -- Abstract base for the polymorphic Trip Report
│   ├── VacationTripItem.java      -- TripItem wrapper around a Vacation
│   └── ExcursionTripItem.java     -- TripItem wrapper around an Excursion
├── security/
│   └── PinHasher.java             -- Salted SHA-256 hashing for the PIN lock
├── util/
│   └── DateRangeValidator.java    -- Pure-Java date validation, unit tested
└── receivers/
    └── AlarmReceiver.java         -- BroadcastReceiver that fires system notifications
```
---
Dependencies (app/build.gradle, versions managed in gradle/libs.versions.toml)
```gradle
dependencies {
    implementation libs.activity.ktx
    implementation libs.appcompat
    implementation libs.constraintlayout
    implementation libs.material
    implementation libs.room.common.jvm
    implementation libs.room.runtime
    testImplementation libs.junit
    androidTestImplementation libs.espresso.core
    androidTestImplementation libs.ext.junit
    annotationProcessor libs.room.compiler
}
```
---
Building a Signed Release APK
Release builds are signed via a Gradle `signingConfig` that reads credentials from `app/keystore.properties` — a file that is intentionally **not** committed to the repository (see `.gitignore`).

To build one yourself:
1. Generate a keystore (one time only):
   ```
   keytool -genkeypair -v -keystore app/tripwise-release.jks -alias tripwise -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Create `app/keystore.properties` (same folder, also gitignored):
   ```properties
   storeFile=tripwise-release.jks
   storePassword=<your store password>
   keyAlias=tripwise
   keyPassword=<your key password>
   ```
3. Build the release APK:
   ```
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

If `app/keystore.properties` doesn't exist, the release build type simply omits `signingConfig` and Gradle produces an unsigned release APK — the project still compiles either way.

Distribution: the current signed release is published as a GitHub Release and linked from the GitHub Pages project site.
- Project site: https://syauss1.github.io/WGU_Capstone/
- Latest APK: https://github.com/syauss1/WGU_Capstone/releases/latest