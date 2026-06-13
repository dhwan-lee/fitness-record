# 🏋️‍♂️ Fitness Tracker Application

A robust, desktop-based fitness logging and analytics application designed for tracking strength training and cardio progression. This project utilizes an object-oriented domain hierarchy to compute workout volume over time, ensuring data integrity through strict encapsulation and automated unit testing.

---

## 🛠️ Features

* **Workout Session Logging:** Group individual exercises chronologically by date.
* **Volume Analytics:** Automatically calculates total structural volume ($Volume = \text{Sets} \times \text{Reps} \times \text{Weight}$) for individual exercises as well as aggregate totals for entire workout sessions to track progressive overload.
* **Granular Filtering:** Query historical data rapidly by matching calendar dates or specific targeted muscle groups (`CHEST`, `BACK`, `LEGS`, etc.).
* **Data Persistence:** Save and load tracking data locally using a structured JSON serialization framework.
* **Foolproof Keyboard-Driven UI:** A polished Java Swing interface featuring complete modal window chaining, defensive placeholder text management, and instant keyboard navigation (Arrows for selection, `Enter` to confirm, and `ESC` to dismiss dialogs).

---

## 🏗️ Architectural Design & Patterns

### Domain Model Hierarchy
The core business logic is structured using a strict relational ownership hierarchy:
* **Logbook:** Manages the collective timeline of training blocks.
* **WorkoutSession:** Represents a single calendar day's routine, implementing defensive copying techniques on constructors and getters to prevent reference aliasing.
* **Exercise:** Holds individual performance metrics (weight metrics in kilograms, sets, reps) and handles string capitalization cleaning automatically.

### Implemented Design Patterns
* **Singleton Design Pattern:** Implemented globally via `EventLog` to maintain a single, universally accessible system telemetry engine tracking user modifications and structural states.
* **Composite UI Structure:** Separates views cleanly using specialized Swing components (e.g., custom `ImagePanel` rendering engines and modal `JDialog` managers).

---

## 💾 JSON Data Format Example

The application stores data structured locally inside `./data/fitness_log.json` adhering to the following structure:

```json
[
  {
    "date": "2026/06/13",
    "exercises": [
      {
        "exercise name": "Bench press",
        "muscle Type": "CHEST",
        "weight": 100,
        "number of Sets": 10,
        "number of Repetitions": 3
      }
    ]
  }
]