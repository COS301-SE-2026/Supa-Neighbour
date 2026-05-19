# Supa-Neighbour — Frontend

This is the Flutter frontend for the Supa-Neighbour project. It serves two audiences from a single codebase:
- **Mobile app** — for normal users (Android)
- **Web dashboard** — for admin users (runs in a browser)

---

## Prerequisites

Before running the frontend, make sure you have the following set up on your machine:

- WSL2 (Ubuntu) — all commands below are run inside WSL2
- Flutter SDK installed via snap
- A Chromium-based browser on Windows (e.g. Brave, Chrome, Edge) for previewing the web dashboard

---

## First time setup

> **Note:** The Flutter project files are already committed to the repository — you do **not** need to run `flutter create` again. That command was only run once to initialise the project. Skip straight to step 2.

### Step 1 — Install Flutter (WSL2 only)

If you do not have Flutter installed on your machine yet, run:

```bash
sudo snap install flutter --classic
```

### Step 2 — Install project dependencies

Navigate to the frontend folder and install all Dart packages:

```bash
cd frontend/
flutter pub get
```

This is the Flutter equivalent of `npm install` — it reads `pubspec.yaml` and downloads all required packages.
---

### Step 3: Verify that everything installed correctly
Verify it installed correctly:

```bash
flutter doctor
```

## Troubleshooting flutter doctor errors

Below are the two most common errors you may see and how to handle them.

---

### Android toolchain error

```bash
[!] Android toolchain - develop for Android devices
    ✗ Android SDK file not found: adb.
```

This means Flutter cannot detect Android tooling on your machine. You have two options depending on how you want to test the mobile app:

**Option A — Use Android Studio (emulator on your computer)**

1. Download and install Android Studio on the **Windows side** (not inside WSL2) from [developer.android.com/studio](https://developer.android.com/studio)
2. Open Android Studio → SDK Manager → install **Android SDK Platform 34**
3. Add this to your `~/.bashrc` in WSL2 — replacing `YOUR_USERNAME` with your Windows username:
```bash
export ANDROID_HOME='/mnt/c/Users/YOUR_USERNAME/AppData/Local/Android/Sdk' - should just be the path to the Sdk of Android Studio
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
```
4. Reload it:
```bash
source ~/.bashrc
```
5. Open Android Studio → Device Manager → create and start a virtual device
6. Run the app:
flutter run

---

**Option B — Use a physical Android phone**

1. On your Android phone go to **Settings → About Phone** and tap **Build Number** 7 times to enable Developer Mode
2. Go to **Settings → Developer Options** and enable **USB Debugging**
3. Connect your phone to your computer via USB
4. Verify Flutter detects it:
```bash
flutter devices
```
You should see your phone listed.
5. Run the app:
```bash
flutter run
```

---

> If you are only working on the **web admin dashboard** currently and not the mobile app, you can safely ignore this error entirely and proceed with `flutter run -d web-server --web-port=3000`.(Step 4)

---

### Chrome not found error

```bash
[✗] Chrome - develop for the web
    ! Cannot find Chrome. Try setting CHROME_EXECUTABLE to a Chrome executable.
```

This means Flutter cannot find a browser to run the web dashboard. Fix it by pointing Flutter to your Windows browser. Open `~/.bashrc`:

```bash
nano ~/.bashrc
```

Add the line that matches your browser — replace `YOUR_USERNAME` with your Windows username:

```bash
# Brave
export CHROME_EXECUTABLE='/mnt/c/Program Files/BraveSoftware/Brave-Browser/Application/brave.exe'

# Chrome
export CHROME_EXECUTABLE='/mnt/c/Program Files/Google/Chrome/Application/chrome.exe'

# Edge
export CHROME_EXECUTABLE='/mnt/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe'
```

Reload it:
```bash
source ~/.bashrc
```
This should enable you to run the web admin page now

### Step 4 — Run the web page

Start the local development server:

```bash
flutter run -d web-server --web-port=3000
```

Then open your Windows browser and go to:

```
http://localhost:3000
```

For now,you should see the Supa-Neighbour test screen. If the button and counter work, your setup is confirmed working.

---

## Folder structure

```
frontend/
└── lib/
    ├── main.dart        ← entry point — loads admin or mobile app based on platform
    ├── app/             ← mobile app screens and widgets (normal users)
    └── admin/           ← web dashboard screens and widgets (admin users)
```

---

## Common commands

| Command | What it does |
|---|---|
| `flutter pub get` | Installs/updates dependencies from pubspec.yaml |
| `flutter run -d web-server --web-port=3000` | Runs the web dashboard locally |
| `flutter test` | Runs all unit and widget tests |
| `flutter build web` | Builds the web dashboard for deployment |
| `flutter doctor` | Checks your Flutter setup for any issues |

---

## Troubleshooting

**`CHROME_EXECUTABLE` not a valid identifier**
Make sure you used single quotes around the path, not double quotes. Spaces in the path (e.g. `Program Files`) break bash when double quoted.

**Browser opens but shows a blank page**
Wait a few seconds and refresh — Flutter sometimes takes a moment to finish compiling on first run.

**`flutter doctor` shows missing dependencies**
Follow the instructions it prints — it tells you exactly what to install and how.