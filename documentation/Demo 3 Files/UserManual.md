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
2. Tap **"Create Account"** on the welcome screen
3. Enter your details:
   - **Email Address** — A valid email address for verification
   - **Phone Number** — For important notifications
   - **First and Last Name** — How you'll be known in the community
   - **Residential Address** — To match you with your neighbourhood zone
4. Create a **password** (minimum 8 characters with a mix of letters, numbers, and special characters)
5. Tap **"Sign Up"**
6. Check your email/phone for the **OTP verification code**
7. Enter the code to activate your account

***Sign Up Screenshots:***
- [View Sign Up Page a](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20Screen.png)

- [View Sign Up Page b](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20Confirm%20OTP%20Screen.png)

- [View Sign Up Page c](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Identity%20Screen.png)

- [View Sign Up Page d](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Residential%20Address%20Screen.png)

- [Vire Sign Up page e](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Sign%20up%20-%20User%20Other%20Details%20Screen.png)

***Forgot password screenshots:***
- [View Forgot Password a](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Forgot%20Password%20Screen.png)

- [View Sign Up Password b](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Forgot%20Password%20-%20Set%20Password%20Screen.png)

### 2.2 Logging In

1. Open the app
2. Enter your **Email** and **Password**
3. Tap **"Login"**
4. If you've forgotten your password, tap **"Forgot Password"** and follow the reset instructions

***Screenshots:***
- [View Login Page a](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Spash%20Screen.png)
- [View Login Page b](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Splash%20-%20LoginSignup%20Screen.png)
- [View Login Page c](/documentation/Demo%202%20Files/Images/Wireframes/Auth/Login%20Screen.png)

---

## 3. Home Screen

The Home screen is your dashboard for everything happening in your neighbourhood.

| Element | What It Does |
|---------|--------------|
| **Welcome section** | Displays your name and trust score |
| **Stats cards** | Shows Helps Given, Tasks Posted, and Active Tasks |
| **Available Nearby** | Shows tasks from neighbours that you can help with |
| **+ Button** | Create a new task |
| **See All** | View all available tasks |

**To view a task:** Tap on any task card to see full details and instructions.

**To refresh the list:** Pull down from the top of the screen.

***Screenshots:***

- [View Home Page a](/frontend/assets/screenshots/home-screen.png)
- [View Home Page b](/frontend/assets/screenshots/create-task.png)


---

## 4. Tasks Screen

The Tasks screen helps you manage all your tasks in one place. It has three tabs:

### 4.1 Posted Tab
Shows tasks you have created. From here you can:
- **View task details** — Tap any task to see full information
- **Edit a task** — If the task is still open, tap the edit icon (pencil) in the top right
- **Find helpers** — For open tasks, tap the task to view and invite available helpers
- **Approve completion** — For tasks marked as complete, review and approve

***Screenshots:***

- [View Posted Page a](/frontend/assets/screenshots/tasks-posted.png)

- [View Posted Page b](/frontend/assets/screenshots/rating.png)

- [View Posted Page c](/frontend/assets/screenshots/available-helpers.png)

### 4.2 Accepted Tab
Shows tasks you have accepted to help with. From here you can:
- **Start a task** — Tap "Start" when you begin working on the task
- **Mark as complete** — Tap "Complete" when you've finished the task
- **Submit for approval** — After completion, the requester will review your work
- **View task details** — Tap any task to see full information

***Screenshots:***

- [View Accepted Tab a](/frontend/assets/screenshots/tasks-accepted.png)


### 4.3 Available Tab
Shows tasks from neighbours that you can help with. From here you can:
- **Accept a task** — Swipe right on a task card or tap and select "Accept"
- **Pass on a task** — Swipe left on a task card or tap and select "Pass"
- **View task details** — Tap any task to see full information before deciding

- [View Available Tab a](/frontend/assets/screenshots/tasks-available.png)

- [View Available Tab b](/frontend/assets/screenshots/task-detail.png)

---

## 5. Chat Screen

The Chat screen helps you communicate with neighbours and stay informed about your community.

### 5.1 Inbox Tab
Shows your direct messages with neighbours.
- **Tap a chat** to open it and view messages
- **Send messages** — Type your message and tap the send button
- **Share images** — Tap the attachment icon to send photos
- **Real-time updates** — Messages appear instantly

***Screenshots:***

- [View Chat Page a](/frontend/assets/screenshots/chat-inbox.png)


### 5.2 Community Bulletin Tab
Shows community announcements and updates.
- **View posts** — Browse announcements from neighbours
- **Create a post** — Tap the + button to share news or ask for help
- **Filter posts** — Use the filter button to view specific categories
- **Search posts** — Use the search bar to find specific content
- **React to posts** — Tap "Helpful" to show appreciation for a post
- **Report inappropriate content** — Tap the flag icon to report a post

***Screenshots:***

- [View Bulletin Board Page a](/frontend/assets/screenshots/bulletin-feed.png)

- [View Bulletin Board Page b](/frontend/assets/screenshots/create-bulletin.png)

---

## 6. Leaderboard Screen

The Leaderboard shows the top helpers in your neighbourhood.

| Element | What It Shows |
|---------|---------------|
| **Last Week's Top 3** | The three highest-ranked helpers from the previous week |
| **This Week** | The current week's rankings |
| **Your Rank Card** | Your current position and progress to the next rank |

**To view a helper's profile:** Tap on any helper in the list.

**How rankings work:**
- **Gold** — Trust score ≥ 4.8
- **Silver** — Trust score ≥ 4.5
- **Bronze** — Trust score ≥ 4.0

***Screenshots:***

- [View Leaderboard Page a](/frontend/assets/screenshots/leaderboard.png)

- [View Leaderboard Page b](/frontend/assets/screenshots/helper-profile.png)

---

## 7. Profile Screen

The Profile screen displays your community standing and allows you to manage your account.

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

***Screenshots:***

- [View  rivacy settings Page](/frontend/assets/screenshots/privact-settings.png)

- [View Serttings Page](/frontend/assets/screenshots/settings.png)

### 7.8 Logout
- Tap "Logout" to sign out of your account

***Screenshots:***

- [View Profile Page](/frontend/assets/screenshots/profile.png)

---

## 8. Help & Support

Access help at any time by:
- Tapping the **info icon ** on any main screen
- Going to **Profile → Settings → Help Center**

The Help Menu includes:
- **Frequently Asked Questions** — Common questions and answers
- **User Manual** — This document
- **Contact Support** — Reach out to our support team

***Screenshots***

- [View Help and Support Page a](/frontend/assets/screenshots/help-and-support.png)

---

## 9. Troubleshooting

### 9.1 Can't Log In
- Check that your email and password are correct
- Use the "Forgot Password" option to reset your password
- Ensure you have a stable internet connection

### 9.2 Can't Find a Task
- Check the Available tab for tasks from neighbours
- Pull down to refresh the list
- Check your filter settings

### 9.3 Task Not Updating
- Pull down to refresh the screen
- Check your internet connection
- Try restarting the app

### 9.4 Chat Messages Not Sending
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

---

**Document Maintained by:** Parse&Co  
**Last Updated:** July 2026