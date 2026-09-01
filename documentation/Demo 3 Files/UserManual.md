# User Manual
## SupaNeighbour — Parse&Co

**Version:** 2.0
**Date:** July 2026
**Author:** Michelle W Njoroge (UI Engineer/Designer)

---

## 1. Introduction

Welcome to SupaNeighbour! This user manual will guide you through the key features of the application, helping you get the most out of your neighbourhood community platform.

SupaNeighbour connects residents within the same neighbourhood by enabling them to request and provide short-term assistance for small household tasks. Whether you need help with plant care, pet feeding, bin collection, package collection, or home check-ins, SupaNeighbour makes it easy to find trusted neighbours willing to help.

---

## 2. Getting Started

### 2.1 Creating an Account

1. Open the SupaNeighbour app

   ![Splash screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Spash%20Screen.png)

2. Tap **"Create Account"** on the welcome screen

   ![Login/Signup splash screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Splash%20-%20LoginSignup%20Screen.png)

3. Enter your **Email Address** and create a **password** (minimum 8 characters with a mix of letters, numbers, and special characters), then tap **"Sign Up"**

   ![Sign up screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20Screen.png)

4. Check your inbox for a **verification email** and click the link inside it to confirm your email address.

   > **Don't see it?** Verification emails sometimes land in your **Spam** or **Junk** folder — check there if it hasn't arrived within a few minutes.

   *(Note: this step now uses an email verification link rather than an OTP code — the screenshot below should be updated to match.)*

   ![Sign up - confirm verification screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20Confirm%20OTP%20Screen.png)

5. Once your email is verified, you'll be taken to finish setting up your profile. Enter the remaining details:
   - **First and Last Name** — How you'll be known in the community

     ![Sign up - user identity screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Identity%20Screen.png)

   - **Residential Address** — To match you with your neighbourhood zone

     ![Sign up - residential address screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Residential%20Address%20Screen.png)

   - **Phone Number** — For important notifications, plus any other remaining details

     ![Sign up - other details screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Other%20Details%20Screen.png)

6. Once these details are submitted, your account is fully set up and you'll land on the **Home screen** (see [Section 3](#3-home-screen)).

**Forgot your password?**

- Tap **"Forgot Password"** on the login screen

  ![Forgot password screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Forgot%20Password%20Screen.png)

- Set a new password when prompted

  ![Forgot password - set password screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Forgot%20Password%20-%20Set%20Password%20Screen.png)

### 2.2 Logging In

1. Open the app

   ![Login screen](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Login%20Screen.png)

2. Enter your **Email** and **Password**
3. Tap **"Login"**
4. If you've forgotten your password, tap **"Forgot Password"** and follow the reset instructions (see [Section 2.1](#21-creating-an-account))

---

## 3. Home Screen

Once you're signed in — whether for the first time after registering, or on a return visit — you land on the Home screen, your dashboard for everything happening in your neighbourhood.

| Element | What It Does |
|---------|--------------|
| **Welcome section** | Displays your name and trust score |
| **Stats cards** | Shows Helps Given, Tasks Posted, and Active Tasks |
| **Available Nearby** | Shows tasks from neighbours that you can help with |
| **+ Button** | Create a new task |
| **See All** | View all available tasks |

![Home screen](/frontend/assets/screenshots/home-screen.png)

**To create a task**, tap the **+ Button**:

1. Tap the **+ Button** on the Home screen
2. Fill in your task details (category, description, timing, and any other requirements)
3. Submit the task — it will now appear under the **Posted** tab of the Tasks screen (see [Section 4.1](#41-posted-tab)), where neighbours can find and accept it

   ![Create task screen](/frontend/assets/screenshots/create-task.png)

**To view a task**, tap on any task card to see full details and instructions. To see everything at once, tap **"See All"** — this takes you to the Tasks screen (see [Section 4](#4-tasks-screen)).

**To refresh the list**, pull down from the top of the screen.

---

## 4. Tasks Screen

The Tasks screen helps you manage all your tasks in one place. It has three tabs:

### 4.1 Posted Tab

Shows tasks you have created — including any you just posted from the Home screen.

![Posted tasks tab](/frontend/assets/screenshots/tasks-posted.png)

From here you can:
- **View task details** — Tap any task to see full information
- **Find helpers** — For open tasks, tap the task to view and invite available helpers

  ![Available helpers screen](/frontend/assets/screenshots/available-helpers.png)

- **Approve completion** — For tasks marked as complete, review and approve

  ![Rating screen](/frontend/assets/screenshots/rating.png)

### 4.2 Accepted Tab

Shows tasks you have accepted to help with.

![Accepted tasks tab](/frontend/assets/screenshots/tasks-accepted.png)

From here you can:
- **Start a task** — Tap "Start" when you begin working on the task
- **Mark as complete** — Tap "Complete" when you've finished the task
- **Submit for approval** — After completion, the requester will review your work
- **View task details** — Tap any task to see full information

### 4.3 Available Tab

Shows tasks from neighbours that you can help with.

![Available tasks tab](/frontend/assets/screenshots/tasks-available.png)

From here you can:
- **Accept a task** — Swipe right on a task card or tap and select "Accept"
- **Pass on a task** — Swipe left on a task card or tap and select "Pass"
- **View task details** — Tap any task to see full information before deciding

  ![Task detail screen](/frontend/assets/screenshots/task-detail.png)

---

## 5. Chat Screen

The Chat screen helps you communicate with neighbours and stay informed about your community.

### 5.1 Inbox Tab

Shows your direct messages with neighbours.

![Chat inbox](/frontend/assets/screenshots/chat-inbox.png)

- **Tap a chat** to open it and view messages
- **Send messages** — Type your message and tap the send button
- **Share images** — Tap the attachment icon to send photos
- **Real-time updates** — Messages appear instantly

### 5.2 Community Bulletin Tab

Shows community announcements and updates.

![Bulletin board feed](/frontend/assets/screenshots/bulletin-feed.png)

- **View posts** — Browse announcements from neighbours
- **Create a post** — Tap the + button to share news or ask for help

  ![Create bulletin post screen](/frontend/assets/screenshots/create-bulletin.png)

- **Filter posts** — Use the filter button to view specific categories
- **Search posts** — Use the search bar to find specific content
- **React to posts** — Tap "Helpful" to show appreciation for a post
- **Report inappropriate content** — Tap the flag icon to report a post

---

## 6. Leaderboard Screen

The Leaderboard shows the top helpers in your neighbourhood.

| Element | What It Shows |
|---------|---------------|
| **Last Week's Top 3** | The three highest-ranked helpers from the previous week |
| **This Week** | The current week's rankings |
| **Your Rank Card** | Your current position and progress to the next rank |

![Leaderboard screen](/frontend/assets/screenshots/leaderboard.png)

**To view a helper's profile:** Tap on any helper in the list.

![Helper profile screen](/frontend/assets/screenshots/helper-profile.png)

**How rankings work:**
- **Gold** — Trust score ≥ 4.8
- **Silver** — Trust score ≥ 4.5
- **Bronze** — Trust score ≥ 4.0

---

## 7. Profile Screen

The Profile screen displays your community standing and allows you to manage your account.

![Profile screen](/frontend/assets/screenshots/profile.png)

### 7.1 Your Profile Card
- **Display Name** — Your name in the community
- **Level** — Your current helper level (Bronze, Silver, or Gold)
- **Trust Score** — Your rating based on completed tasks and reviews
- **Neighbourhood** — Your registered neighbourhood zone

### 7.2 XP and Progress
- **XP Points** — Earned by completing tasks
- **Progress Bar** — Shows how far you are from the next level milestone

### 7.3 Stats
- **Tasks Created** — Number of tasks you've posted
- **Tasks Completed** — Number of tasks you've helped with
- **Active Tasks** — Tasks currently in progress

### 7.4 Skills & Services
- View and edit your skills (Plants, Pets, Bins, Packages, etc.)
- Tap "Edit" to add or remove skills
- Skills help neighbours find you for relevant tasks

### 7.5 Achievements
- View earned badges and achievements
- Tap "View All" to see all available achievements

### 7.6 Recent Tasks
- Shows your most recent completed tasks
- Each task shows the XP earned

### 7.7 Privacy Settings
- Manage your privacy preferences
- Control location and data sharing

  ![Privacy settings screen](/frontend/assets/screenshots/privact-settings.png)

  ![Settings screen](/frontend/assets/screenshots/settings.png)

### 7.8 Logout
- Tap "Logout" to sign out of your account

---

## 8. Help & Support

Access help at any time by:
- Tapping the **info icon** on any main screen
- Going to **Profile → Settings → Help Center**

![Help and support screen](/frontend/assets/screenshots/help-and-support.png)

The Help Menu includes:
- **Frequently Asked Questions** — Common questions and answers
- **User Manual** — This document
- **Contact Support** — Reach out to our support team

---

## 9. Troubleshooting

### 9.1 Can't Log In
- Check that your email and password are correct
- Use the "Forgot Password" option to reset your password
- Ensure you have a stable internet connection

### 9.2 Didn't Receive the Verification Email
- Check your **Spam** or **Junk** folder
- Make sure you entered your email address correctly during sign up
- Wait a few minutes, then check again before requesting a new one

### 9.3 Can't Find a Task
- Check the Available tab for tasks from neighbours
- Pull down to refresh the list
- Check your filter settings

### 9.4 Task Not Updating
- Pull down to refresh the screen
- Check your internet connection
- Try restarting the app

### 9.5 Chat Messages Not Sending
- Check your internet connection
- Ensure you're connected to a stable network
- Try restarting the app

---

## 10. Frequently Asked Questions

### 10.1 How do I earn XP?
You earn XP by completing tasks. The amount of XP varies based on the task difficulty.

### 10.2 How is my trust score calculated?
Your trust score is calculated based on completed tasks and ratings from other users.

### 10.3 What are the helper levels?
- **Bronze** — Trust score ≥ 4.0
- **Silver** — Trust score ≥ 4.5
- **Gold** — Trust score ≥ 4.8

### 10.4 Can I change my neighbourhood?
Yes — go to Profile → Privacy Settings and update your residential address.

### 10.5 How do I contact a helper?
Once a helper accepts your task, you can chat with them through the Chat tab.

### 10.6 Why did I get an email link instead of an OTP code during sign up?
SupaNeighbour verifies your email using a confirmation link rather than a one-time code — just click the link in the email we send you (check Spam if it's not in your inbox).

---

**Document Maintained by:** Parse&Co
**Last Updated:** July 2026