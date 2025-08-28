# TODO App for Android

A simple and elegant TODO list application for Android, built with Kotlin and following modern Android development practices. This project serves as a demonstration of a clean and scalable Android application architecture.

## 🌟 Features

*   **Add Tasks:** Quickly add new tasks to your to-do list.
*   **View Tasks:** See all your tasks in a clean and intuitive list.
*   **Delete Tasks:** Easily remove tasks you've completed or no longer need.
*   **Modern UI:** A clean and user-friendly interface.

## 🛠️ Tech Stack & Architecture

This project is built with a focus on modern Android development standards and best practices.

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **Architecture:** MVVM (Model-View-ViewModel)
    *   **ViewModel:** Manages UI-related data and business logic.
    *   **Repository:** Acts as a single source of truth for data.
    *   **Entity:** Represents the data model for a task.
*   **UI:**
    *   XML for layouts.
    *   `RecyclerView` for displaying the list of tasks.
*   **Build Tool:** [Gradle](https://gradle.org/)

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   [Android Studio](https://developer.android.com/studio) (latest version recommended)
*   JDK (Java Development Kit)

### Installation

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/OtakuSanskar/Kotlin-Projects.git
    cd 'Kotlin-Projects/TODO App'
    ```
2.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Click on "Open an existing Android Studio project".
    *   Navigate to the cloned repository and select it.
3.  **Build and Run:**
    *   Let Android Studio sync the Gradle files.
    *   Click on the "Run" button (green play icon) to build and run the app on an emulator or a physical device.

## 📈 Future Improvements

This project has a solid foundation that can be extended with more features. Some ideas for future improvements include:

*   **Edit Tasks:** The ability to edit existing tasks.
*   **Mark as Complete:** Mark tasks as completed with a visual indicator (e.g., strikethrough).
*   **Data Persistence:** Implement a local database using [Room](https://developer.android.com/training/data-storage/room) to persist tasks even when the app is closed.
*   **Priority Levels:** Assign different priority levels to tasks (e.g., high, medium, low).
*   **Notifications:** Set reminders for tasks and receive notifications.
*   **Themes:** Add support for light and dark themes.

## 👤 Author

**Sanskar Sahu**

*   GitHub: [@Sanskar Sahu](https://github.com/OtakuSanskar)
*   LinkedIn: [@SanskarSahu765](https://www.linkedin.com/in/SanskarSahu765)

---
