# TimeSync

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

TimeSync is an Android app that allows users to track and categorize their time entries. It features
account creation, time entry logging, category management, filtered reports, and user goal settings.

## Table of Contents

1. [Features](#features)
2. [Recent Updates](#recent-updates)
3. [Getting Started](#getting-started)
4. [Contribute](#contribute)
5. [License](#license)

## Features

- Sign Up / Sign In with additional fields for Name and Occupation.
- Home Screen displaying all time entries and a chart that visualizes progress towards daily goals.
- Categories Screen for categorizing time entries and adding new categories.
- Add Time Entry with details: date, category, start time, end time, description, image.
- Reports Screen for filtered reports based on date range and a detailed chart showcasing user activity against set goals.
- Settings Screen for setting min and max daily hour goals.
- Log out functionality.

## Recent Updates

- Added a chart on the Home Screen that helps visualize progress towards daily goals.
- Included a detailed chart on the Reports Screen showcasing user activity against set goals.
- Enhanced the Sign Up process by adding fields for Name and Occupation.
- Fixed several bugs to improve user experience.

## Getting Started

To clone and run this application, you'll need [Git](https://git-scm.com)
and [Android Studio](https://developer.android.com/studio) installed on your computer. From your
command line:

```bash
# Clone this repository
$ git clone https://github.com/johanngeustyn/TimeSync.git

# Go into the repository
$ cd TimeSync

# Open with Android Studio and run the app on your emulator or actual device.
```

## Built With

- [Kotlin](https://kotlinlang.org/)
- [Firebase](https://firebase.google.com/)
    - Firebase Authentication: Used for managing user sign up/sign in.
    - Firebase Firestore: NoSQL cloud database to store and sync data for client- and server-side
      development.
    - Firebase Storage: Used for storing images related to time entries.

## Contribute

We love to receive contributions from the community and hear your opinions! We want to make
contributing to this project as easy and as transparent as possible, whether it's:

- Reporting a bug
- Submitting a fix
- Proposing new features


## License

This project is licensed under the terms of the MIT license.
