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

Verify it installed correctly:

```bash
flutter doctor
```

if you do see the folloing error:
[!] Android toolchain - develop for Android devices (Android SDK version 36.1.0)
    ✗ Android SDK file not found: adb.
    
This means that flutter isnt detecting Android Studio. This wont be a problem as we will not be using it. Some issues regarding it's setup were encoutered, thus it will not be used.

if you see this error:
[✗] Chrome - develop for the web (Cannot find Chrome executable at google-chrome)
    ! Cannot find Chrome. Try setting CHROME_EXECUTABLE to a Chrome executable.

This will be addressed in step 3

---

### Step 2 — Install project dependencies

Navigate to the frontend folder and install all Dart packages:

```bash
cd frontend/
flutter pub get
```

This is the Flutter equivalent of `npm install` — it reads `pubspec.yaml` and downloads all required packages.

---

### Step 3 — Point Flutter to your browser

Flutter needs to know where your Windows browser is located in order to open it from WSL2. Open your bash config file:

```bash
nano ~/.bashrc
```

Add this line at the bottom, replacing the path with wherever your browser is installed on Windows:

```bash
# For Brave:
export CHROME_EXECUTABLE='/mnt/c/Program Files/BraveSoftware/Brave-Browser/Application/brave.exe'

# For Chrome:
export CHROME_EXECUTABLE='/mnt/c/Program Files/Google/Chrome/Application/chrome.exe'

# For Edge:
export CHROME_EXECUTABLE='/mnt/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe'
```

Save the file (`Ctrl + X`, then `Y`, then `Enter`) and reload it:

```bash
source ~/.bashrc
```

---

### Step 4 — Run the app

Start the local development server:

```bash
flutter run -d web-server --web-port=8080
```

Then open your Windows browser and go to:

```
http://localhost:8080
```

You should see the Supa-Neighbour test screen. If the button and counter work, your setup is confirmed working.

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
| `flutter run -d web-server --web-port=8080` | Runs the web dashboard locally |
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