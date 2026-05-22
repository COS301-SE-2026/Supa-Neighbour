# Supa Neighbour Brand Style Guide

The brand style defines the visual identity of the system and ensures a consistent and professional appearance across all interfaces. It provides guidelines that all developers and designers must follow when implementing the user interface.

---

## 1. Design Principles

Our high-level principles guide the overall look and feel of the system, ensuring an optimal user experience:

* **Consistency:** Uniform use of colors, typography, and UI elements across all screens to build familiarity and trust.
* **Simplicity:** A clean, uncluttered interface that prioritizes essential information. Soft rounded corners and ample whitespace reduce cognitive load.
* **Responsiveness:** Fluid layouts that adapt seamlessly to different screen sizes and orientations.
* **Accessibility:** An "Accessibility-First" approach ensuring usability for individuals with diverse abilities, adhering to standards like WCAG.

---

## 2. Color Palette

The **Fresh and Modern** palette is selected with accessibility in mind, ensuring sufficient contrast for readability while keeping the app inviting and energetic.

| Role | Colour Name | Hex Code | Usage |
| :--- | :--- | :--- | :--- |
| **Primary** | Vibrant Teal | `#2A9D8F` | Buttons, active navigation, input borders, primary icons, FAB |
| **Secondary** | Citrus Yellow | `#E9C46A` | XP badges, trust scores, highlights |
| **Background** | Clean White | `#FFFFFF` | App background, card backgrounds |
| **Text Dark** | Charcoal | `#264653` | Headings, primary body text, dark text on light backgrounds |
| **Text Light** | White | `#FFFFFF` | Text on primary teal buttons, active tab text |
| **Success** | Mint | `#69B578` | Success states, positive confirmations |
| **Error** | Coral | `#F4A261` | Error messages, destructive actions |

---

## 3. Typography

Standardized font families, sizes, and weights for headings, body text, and other UI elements to ensure readability and clear visual hierarchy.

### 3.1 Font Family

| Type | Font | Fallback | Notes |
| :--- | :--- | :--- | :--- |
| **Headings** | Poppins | Roboto, sans-serif | Used for titles, section headers, modal titles |
| **Body Text** | Open Sans | Roboto, sans-serif | Used for paragraphs, descriptions, list items |
| **Badges/Chips** | Open Sans (Semi bold) | Roboto, sans-serif | Used for XP badges, status pills, tags, helper level badges |

### 3.2 Font Sizes & Weights

| Element | Font Family | Size | Weight | Letter Spacing |
| :--- | :--- | :--- | :--- | :--- |
| **H1 (Screen Title)** | Poppins | 24px | 600 | -0.3px |
| **H2 (Section Header)** | Poppins | 20px | 500 | -0.2px |
| **H3 (Card Title)** | Poppins | 18px | 500 | -0.2px |
| **Body Large** | Open Sans | 16px | 400 | 0px |
| **Body Medium** | Open Sans | 14px | 400 | 0px |
| **Body Small** | Open Sans | 12px | 400 | 0.2px |
| **Badge Text** | Open Sans | 12px | 600 | 0.3px |
| **Button Text** | Open Sans | 14px | 600 | 0.5px |
| **Tab Label** | Open Sans | 14px | 500 | 0.3px |
| **Input Field** | Open Sans | 16px | 400 | 0px |
| **Helper/Timestamp** | Open Sans | 12px | 400 | 0.2px |
| **Navigation Bar Label**| Open Sans | 12px | 500 | 0.2px |

### 3.3 Line Height & Spacing

| Element | Line Height | Margin Bottom | Notes |
| :--- | :--- | :--- | :--- |
| **H1** | 32px | 16px | Screen titles |
| **H2** | 28px | 12px | Section headers |
| **H3** | 24px | 8px | Card titles |
| **Body Large** | 24px | 8px | Descriptions |
| **Body Medium** | 20px | 4px | List items, labels |
| **Body Small** | 18px | 4px | Helper text, timestamps |
| **Paragraph spacing** | — | 12px | Between paragraphs |
| **List item spacing** | — | 8px | Between list items |
| **Card padding** | — | 16px | Inside cards |
| **Screen padding** | — | 20px | Screen edges (left/right) |

---

## 4. Logo and Iconography

* **Icon Style:** Minimalist, soft, and friendly. A mix of solid and thick-line icons.
* **Icon Colors:** Primary Teal (`#2A9D8F`) for interactive icons and main graphics. Charcoal (`#264653`) or subtle greys for inactive states.
* **Trust Badges:** Local trust badges use a recognizable ribbon/medal icon paired with distinct colors:
    * *Bronze Helper:* Bronze background with dark text.
    * *Silver Helper:* Light silver/blue background with dark text.
    * *Gold Helper:* Citrus Yellow (`#E9C46A`) background with dark text.
* **Sizing & Placement:** Icons within task cards should be contained within soft-rounded square backgrounds (e.g., pale teal) to maintain a neat grid. Navigation icons are vertically stacked above their respective labels.

---

## 5. UI Component Styling

Standard styles for common interface elements to ensure uniform implementation across the system:

* **Buttons:**
    * *Primary Action:* Full-width or highly visible pill-shaped (fully rounded corners). Background: Vibrant Teal (`#2A9D8F`), Text: Clean White (`#FFFFFF`).
    * *Floating Action Button (FAB):* Circular, positioned at the bottom right. Used for primary actions like "Create Task" (+). Background: Vibrant Teal.
* **Forms & Inputs:**
    * Fields use heavily rounded pill-shape borders.
    * Border color defaults to dark teal/charcoal, highlighting to Vibrant Teal upon focus.
    * Inner text and placeholders use Charcoal (`#264653`).
    * Inline icons (like the password visibility toggle 'eye') are aligned to the right.
* **Cards:**
    * Used for "My Tasks" and "Stats".
    * Background: Clean White (`#FFFFFF`).
    * Border Radius: Approximately 16px to maintain the soft aesthetic.
    * Shadow: Soft, subtle drop shadow to lift the card slightly off the Clean White app background.
* **Navigation Bar:**
    * Bottom tab navigation featuring 5 distinct tabs (Home, Tasks, Inbox, Stats, Profile).
    * Active State: Vibrant Teal for both icon and text.
    * Inactive State: Muted grey to establish visual hierarchy.
* **Badges/Chips:**
    * Used extensively for gamification (XP points) and trust scores.
    * Shape: Fully rounded pill shape.
    * Color: Citrus Yellow (`#E9C46A`) background with dark text for high visibility.

---

## 6. Accessibility

Guidelines to ensure the interface is usable by individuals with diverse abilities:

* **Color Contrast:** Ensure sufficient contrast ratios (minimum 4.5:1 for normal text, 3:1 for large text) between text elements and backgrounds. White text on Vibrant Teal meets these requirements.
* **Touch Targets:** All interactive elements (buttons, navigation tabs, FAB) must have a minimum touch target size of 44x44 points.
* **Keyboard Navigability:** Interfaces must be fully navigable using a keyboard (e.g., logical tab order through login forms and list items).
* **Screen Reader Compatibility:** Proper ARIA labels and alt-text must be utilized for all icons (e.g., leaf icon, package icon) and image-based badges.
* **Visual Cues:** Do not rely solely on color to convey information. Use icons, text labels, and structural grouping (like the distinct layout of XP badges vs. regular text) to ensure clarity for visually impaired users.
