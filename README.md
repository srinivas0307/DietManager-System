# DietManager

_A Java CLI-based diet tracking assistant developed for a software design assignment._

## Date
April 6, 2025

## Team Members
- M. Bheema Siddartha
- B. Srinivas

---

## Overview

**DietManager** is a modular, text-based Java application designed to help users track food intake, manage nutrition data, and calculate daily calorie needs based on user profiles. 

### Features

#### Food Database
- Add **Basic Foods** with calories and nutritional values (e.g., protein, fat).
- Add **Composite Foods** composed of other foods with quantities.
- List all foods stored in the database.
- Save foods to a file (`food_database.txt`).

#### Daily Log
- Add food entries by date.
- View and delete daily food entries.
- Undo the last food log operation.
- View calories consumed per day.

#### User Profile
- Create/load a user profile with gender, height, weight, age, and activity level.
- Track daily changes to:
  - Age
  - Weight
  - Activity level
- Supports calorie calculation via:
  - **Mifflin-St Jeor Equation**
  - **Harris-Benedict Equation**
- Save/load data from `user_profile.txt`.

---

## Data Files

All data is stored in the `/data` folder:

| File Name            | Description                                       |
|---------------------|---------------------------------------------------|
| `food_database.txt` | Stores all basic and composite food items         |
| `daily_log.txt`     | Tracks user’s daily food log entries              |
| `user_profile.txt`  | Stores user profile and daily variations          |

---

## 🧪 How to Use the App

1. Run the program via terminal by below command.
2. ./run.sh
3. You'll see the main menu:

```plaintext
1. Daily Log
2. Food Database
3. User Profile
4. Exit
