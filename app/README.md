WGU Capstone
Purpose
A native Android application that allows users to plan, organize, and track personal vacations and associated excursions. All data is stored locally on the device using the Room persistence library (SQLite). Users can manage vacation details, attach multiple excursions to each vacation, set date-based alerts, and share vacation information.
---
How to Operate the Application
Home Screen
Launch the app to land on the Home Screen.
Tap "View My Vacations" to navigate to the vacation list.
---
Vacation List (B1a, B2, C)
Displays all saved vacations as cards showing the title and date range.
If no vacations exist, an empty-state message is shown.
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
Android Version
The signed APK targets and is deployed to Android 8.0 (API 26 / Oreo) and higher, as required by the project specification.
`minSdkVersion`: 26
`targetSdkVersion`: 36
Tested on Android 8.0 emulator (API 26) and Android 14 (API 34)
---
Git Repository
https://gitlab.com/wgu-gitlab-environment/student-repos/syauss1/d308-mobile-application-development-android/-/tree/working_branch
---
Project Structure
```
app/src/main/java/com/example/d308vacationplanner/
├── UI/
│   ├── MainActivity.java          -- Home screen
│   ├── VacationList.java          -- List of all vacations
│   ├── VacationDetails.java       -- Add / edit / delete vacation
│   ├── VacationAdapter.java       -- RecyclerView adapter for vacations
│   ├── ExcursionDetails.java      -- Add / edit / delete excursion
│   └── ExcursionAdapter.java      -- RecyclerView adapter for excursions
├── database/
│   ├── VacationDatabase.java      -- Room database singleton
│   ├── VacationDAO.java           -- Data access object for vacations
│   ├── ExcursionDAO.java          -- Data access object for excursions
│   └── Repository.java            -- Single data access point for all UI classes
├── entities/
│   ├── Vacation.java              -- Room entity: vacationID, title, hotel, startDate, endDate
│   └── Excursion.java             -- Room entity: excursionID, title, date, vacationID
└── receivers/
    └── AlarmReceiver.java         -- BroadcastReceiver that fires system notifications
```
---
Dependencies (app/build.gradle)
```gradle
dependencies {
    implementation "androidx.room:room-runtime:2.6.1"
    annotationProcessor "androidx.room:room-compiler:2.6.1"
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
}
```
---
Generating the Signed APK
In Android Studio: Build → Generate Signed Bundle / APK
Select APK → Next
Choose or create a keystore file
Fill in key alias, passwords, and validity
Select release build variant → Create
The signed APK is output to `app/release/app-release.apk`