# StuPer


# Student Performance Monitoring System

This repository contains the **Student Performance Monitoring System**, a web application designed to assist teachers in managing and monitoring student performance in real time. The system provides features for recording attendance, grades, and comments, as well as generating analytical reports.

## Features

- **Teacher Dashboard**: View and manage student performance data for specific classes and subjects.
- **Attendance Management**: Record and analyze attendance for specific lessons and groups.
- **Diary**: Maintain a digital record of lessons, including marks, attendance, and notes.
- **Analytics**: Generate performance reports for individual students or groups.

## Project Structure

The project is organized as follows:

```
├───lib
│       jakarta.servlet-api-6.0.0.jar
│       javax.annotation.jar
│       javax.ejb.jar
│       javax.jms.jar
│       javax.persistence.jar
│       javax.resource.jar
│       javax.servlet.jar
│       javax.servlet.jsp.jar
│       javax.servlet.jsp.jstl.jar
│       javax.transaction.jar
│       jstl-api-1.2.jar
│       mysql-connector-j-8.0.33.jar
├───src
│   └───main
│       └───java
│           └───com
│               └───rip_rip
│                       AddStudentProfileServlet.java
│                       AddTeacherProfileServlet.java
│                       DatabaseHelper.java
│                       DiaryServlet.java
│                       EditStudentProfileServlet.java
│                       EditTeacherProfileServlet.java
│                       LoginServlet.java
│                       RegisterServlet.java
│
└───web
    │   admin.jsp
    │   dashboard.jsp
    │   index.jsp
    │   login.jsp
    │   logout.jsp
    │   register.jsp
    │   schedule.jsp
    │   student_diary.jsp
    │   student_profile.jsp
    │   student_profile_add.jsp
    │   student_profile_edit.jsp
    │   student_report.jsp
    │   teacher_diary.jsp
    │   teacher_profile.jsp
    │   teacher_profile_add.jsp
    │   teacher_profile_edit.jsp
    │   teacher_report.jsp
    │
    ├───css
    │       dashboard.css
    │       index.css
    │       login.css
    │       register.css
    │       schedule.css
    │       student_diary.css
    │       student_profile.css
    │       student_profile_change.css
    │       student_report.css
    │       teacher_diary.css
    │       teacher_profile.css
    │       teacher_profile_change.css
    │       teacher_report.css
    │
    ├───js
    │       login.js
    │       register.js
    │       student_profile_change.js
    │       teacher_profile_change.js
    │
    └───WEB-INF
            web.xml
```

## Database Design

The system uses a MySQL database to store student performance data. Key tables include:
# Database Schema Documentation

This document provides an overview of the database schema used in the **Student Performance Monitoring System**.

## Table Structures

### `Users`

Stores user information, including login credentials and user type.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `user_id`           | `INT`              | Unique identifier for the user (primary key). |
| `username`          | `VARCHAR(255)`     | Unique username for the user.               |
| `password`          | `VARCHAR(255)`     | Hashed password for the user.               |
| `user_type`         | `ENUM`             | Type of user (`student` or `teacher`).      |
| `group_`            | `ENUM`             | User's group (`g1`, `g2`, `g3`, `g4`, `teacher`). |
| `registration_date` | `TIMESTAMP`        | Date and time of user registration.         |

---

### `Schedule`

Defines the schedule of lessons for groups.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `id`                | `INT`              | Unique identifier for the schedule entry.   |
| `term`              | `ENUM`             | Semester number (1–8).                      |
| `group_`            | `ENUM`             | Group assigned to the schedule.             |
| `date`              | `DATE`             | Date of the lesson.                         |
| `week_number`       | `INT`              | Week number of the semester.                |
| `day_of_week`       | `ENUM`             | Day of the week (`Monday`, `Tuesday`, etc.). |
| `start_time`        | `TIME`             | Start time of the lesson.                   |
| `end_time`          | `TIME`             | End time of the lesson.                     |
| `subject`           | `VARCHAR(255)`     | Subject name.                               |
| `teacher`           | `VARCHAR(255)`     | Teacher assigned to the lesson.             |
| `couple_type`       | `VARCHAR(25)`      | Type of lesson (e.g., Lecture, Lab).        |
| `semester_start`    | `DATE`             | Start date of the semester (default: 2024-08-01). |
| `semester_end`      | `DATE`             | End date of the semester (default: 2025-01-30). |

**Unique Index**: A unique combination of `group_`, `subject`, `date`, and `start_time`.

---

### `Diary`

Stores attendance and performance records for students.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `id`                | `INT`              | Unique identifier for the diary entry.      |
| `first_name`        | `VARCHAR(255)`     | Student's first name.                       |
| `last_name`         | `VARCHAR(255)`     | Student's last name.                        |
| `group_`            | `ENUM`             | Group of the student (`g1`, `g2`, etc.).    |
| `subject`           | `VARCHAR(255)`     | Subject of the lesson.                      |
| `date`              | `DATE`             | Date of the lesson.                         |
| `mark`              | `VARCHAR(1)`       | Mark given to the student.                  |
| `absence`           | `BOOLEAN`          | Whether the student was absent (default: FALSE). |
| `comment`           | `VARCHAR(40)`      | Additional comments.                        |
| `couple_type`       | `VARCHAR(25)`      | Type of the lesson.                         |

**Unique Index**: A unique combination of `first_name`, `last_name`, `group_`, `subject`, and `date`.

---

### `current_term`

Tracks the current semester for each group.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `group_name`        | `VARCHAR(255)`     | Group name (primary key).                   |
| `current_semester`  | `INT`              | Current semester number (up to 8).          |

**Events**:
- **`update_semester_february`**: Increments the semester on February 1st each year.
- **`update_semester_august`**: Increments the semester on August 1st each year.

---

### `reg_pass`

Stores registration passwords for groups.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `group_name`        | `VARCHAR(50)`      | Group name (primary key).                   |
| `reg_password`      | `VARCHAR(255)`     | Registration password for the group.        |

---

### `students`

Contains detailed information about students.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `id`                | `INT`              | Unique identifier for the student.          |
| `username`          | `VARCHAR(255)`     | Unique username for the student.            |
| `first_name`        | `VARCHAR(255)`     | Student's first name.                       |
| `last_name`         | `VARCHAR(255)`     | Student's last name.                        |
| `birthdate`         | `DATE`             | Birthdate of the student.                   |
| `phone`             | `VARCHAR(15)`      | Phone number of the student.                |
| `email`             | `VARCHAR(255)`     | Email address of the student.               |
| `registration_date` | `TIMESTAMP`        | Registration date (default: current date).  |

---

### `Teachers`

Stores detailed information about teachers.

| Column              | Type                | Description                                  |
|---------------------|---------------------|----------------------------------------------|
| `id`                | `INT`              | Unique identifier for the teacher.          |
| `username`          | `VARCHAR(255)`     | Unique username for the teacher.            |
| `first_name`        | `VARCHAR(255)`     | Teacher's first name.                       |
| `last_name`         | `VARCHAR(255)`     | Teacher's last name.                        |
| `birthdate`         | `DATE`             | Birthdate of the teacher.                   |
| `phone`             | `VARCHAR(15)`      | Phone number of the teacher.                |
| `email`             | `VARCHAR(255)`     | Email address of the teacher.               |
| `department`        | `VARCHAR(255)`     | Department of the teacher.                  |
| `registration_date` | `TIMESTAMP`        | Registration date (default: current date).  |

---

## Notes

- Event Scheduler must be enabled:  
  ```sql
  SET GLOBAL event_scheduler = ON;

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/MENGERSPONGEKNGLTYN/StuPer.git
   cd StuPer
   ```

2. Configure the database:
   - Create a MySQL database.
   - Import the provided SQL scripts to set up tables (`Diary`, etc.).

3. Deploy the project:
   - Copy the project files to the Tomcat `webapps` directory.
   - Start the Tomcat server.

4. Access the application:
   Open a web browser and navigate to:
   ```
   http://localhost:8080/StuPer/
   ```

## Technologies Used

- **Backend**: Java Servlets, JDBC
- **Frontend**: JSP, HTML, CSS, JavaScript
- **Database**: MySQL
- **Server**: Apache Tomcat 11.0.1

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with detailed descriptions of your changes.

## License

This project is licensed under the [MIT License](LICENSE).
