# High Level Use Case V1

## **User Account Management**

### **Use Case: Register Account**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Create a new profile to access the system |
| **TUCBW** | The User selects the "Register Account" option on the landing page |
| **TUCEW** | The system successfully creates the account, sends a verification email |

### **Use Case: Login**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Login into user profile  |
| **TUCBW** | The User enters their credentials (email and password) and clicks the "Login" button |
| **TUCEW** | The system authenticates the credentials and redirects the User to their personalized home dashboard |

---

## **Task Management**

### **Use Case: Create Task**

| **Primary Actor** | Requester |
| --- | --- |
| **Goal** | Post a new task requiring assistance |
| **TUCBW** | The User clicks the "Create Task" button on their Task Creation Screen |
| **TUCEW** | The system saves the task details to the database and reflects the new task on the task tab |

### **Use Case: View Nearby Tasks**

| **Primary Actor** | Helper |
| --- | --- |
| **Goal** | Browse available tasks based on current geographic location |
| **TUCBW** | The User navigates to the "View Nearby Tasks" screen |
| **TUCEW** | The system checks tasks within range, and renders them on a list view |

---

## **Communication**

### **Use Case: Real-Time Chat**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Exchange instant messages between Helper & Requester on a task |
| **TUCBW** | The User opens a chat conversation window with another user |
| **TUCEW** | The system closes the active chat stream when the user leaves the conversation screen |

### **Use Case: Create Bulletin Post**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Share information or an announcement on the community board |
| **TUCBW** | The User types content into the bulletin field and can add images and clicks "Post" |
| **TUCEW** | The post goes live on the shared board for all community members to see |

---

## **Gamification**

### **Use Case: View Leaderboard**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Check user rankings and standing in the community |
| **TUCBW** | The User selects the "View Leaderboard" menu option |
| **TUCEW** | The system calculates up-to-date user rankings and displays the top scorers on screen |

---

## **Settings**

### **Use Case: Swap Themes**

| **Primary Actor** | User |
| --- | --- |
| **Goal** | Change the visual appearance of the application interface to light or dark mode |
| **TUCBW** | The User toggles the theme selection switch in the settings panel |
| **TUCEW** | The application re-renders its layout with the new color palette instantly |

---

## **Administration**

### **Use Case: Admin Login**

| **Primary Actor** | Admin |
| --- | --- |
| **Goal** | Authenticate and gain access to the admin dashboard on the website |
| **TUCBW** | The Admin enters their credentials (email and password) on the admin login page and clicks "Login" |
| **TUCEW** | The system authenticates the Admin's credentials, confirms their `isAdmin` status, and redirects them to the admin dashboard |

### **Use Case: View Reports**

| **Primary Actor** | Admin |
| --- | --- |
| **Goal** | Review all reports submitted by users against tasks, bulletin posts, or other users |
| **TUCBW** | The Admin navigates to the "Reports" section of the admin dashboard |
| **TUCEW** | The system retrieves all report records and displays them in a list, showing report type, status, and reported entity |

### **Use Case: Action a Report**

| **Primary Actor** | Admin |
| --- | --- |
| **Goal** | Resolve a submitted report by issuing a moderation action against the offending user |
| **TUCBW** | The Admin opens a report from the report list and selects a suggested action (Warning / Suspension / Ban) |
| **TUCEW** | The system records the Admin's suggested and actual action against the report, updates the report status, and logs the moderation action against the offending user's account |

### **Use Case: Manage Admin Profile**

| **Primary Actor** | Admin |
| --- | --- |
| **Goal** | View and update personal account settings from the admin panel |
| **TUCBW** | The Admin navigates to the "Settings" section within the admin dashboard |
| **TUCEW** | The system saves the updated profile details and reflects the changes across the admin session |