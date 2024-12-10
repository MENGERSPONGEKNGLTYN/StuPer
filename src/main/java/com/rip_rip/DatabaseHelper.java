package com.rip_rip;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stuper";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            if (connection != null) {
                System.out.println("Connection to the database established successfully.");
            } else {
                System.err.println("Failed to make connection to the database.");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return connection;
    }

    public static String checkLogin(String username, String password) {
        // Хешируем пароль, чтобы сравнить его с хранимым в базе данных
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            return null; // Если хэширование не удалось, возвращаем null
        }

        // Специальный случай для администратора (можно сделать кастомное поведение)
        if (username.equals("admin") && password.equals("password")) {
            return "admin";
        }

        String userType = null; // Здесь будет храниться тип пользователя
        String query = "SELECT user_type FROM users WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userType = rs.getString("user_type"); // Получаем значение user_type
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userType; // Возвращаем тип пользователя или null, если пользователь не найден
    }

    // Метод для регистрации нового пользователя
    public static boolean registerUser(String username, String password, String userType, String group, String regPass) {
        // Хешируем регистрационный пароль
        String hashedRegPass = hashPassword(regPass);

        // Проверяем, совпадает ли хешированный регистрационный пароль с паролем для выбранной группы
        if (!checkRegPassword(group, hashedRegPass)) {
            return false;  // Если пароли не совпадают, возвращаем false
        }

        // Хешируем пароль
        String hashedPassword = hashPassword(password);

        // Проверяем, существует ли уже пользователь с таким логином
        if (userExists(username)) {
            return false;  // Если пользователь уже существует, возвращаем false
        }

        // Добавляем нового пользователя в базу данных
        String query = "INSERT INTO Users (username, password, user_type, group_) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, userType);
            ps.setString(4, group);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;  // Если запись добавлена, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkRegPassword(String group, String hashedRegPass) {
        // Запрос для выборки хешированного регистрационного пароля для указанной группы
        String query = "SELECT reg_password FROM reg_pass WHERE group_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, group);  // Устанавливаем параметр для группы

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Сравниваем хешированный регистрационный пароль с тем, что есть в базе
                    String storedHashedRegPass = rs.getString("reg_password");
                    return storedHashedRegPass.equals(hashedRegPass);  // Если пароли совпадают, возвращаем true
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;  // Если группа не найдена или пароли не совпадают, возвращаем false
    }

    // Метод для хеширования пароля
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для проверки, существует ли уже пользователь с таким логином
    private static boolean userExists(String username) {
        String query = "SELECT 1 FROM Users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  // Если результат найден, значит пользователь существует
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Map<String, String>> getScheduleForWeek(String group, int weekNumber, String curTerm) {
        List<Map<String, String>> schedule = new ArrayList<>();

        // Запрос для получения расписания для группы и недели
        String query = "SELECT * " +
                "FROM schedule WHERE term = ? AND group_ = ? AND week_number = ? ORDER BY FIELD(day_of_week, 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница'), start_time";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Устанавливаем параметры запроса
            stmt.setString(1, curTerm);
            stmt.setString(2, group);
            stmt.setInt(3, weekNumber);

            // Выполняем запрос
            try (ResultSet rs = stmt.executeQuery()) {

                // Обрабатываем результат
                while (rs.next()) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("day_of_week", rs.getString("day_of_week"));
                    entry.put("start", rs.getString("start_time"));
                    entry.put("end", rs.getString("end_time"));
                    entry.put("subject", rs.getString("subject"));
                    entry.put("teacher", rs.getString("teacher"));
                    entry.put("date", rs.getString("date"));
                    entry.put("couple_type", rs.getString("couple_type"));

                    schedule.add(entry);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Логирование ошибки
        }

        return schedule;
    }

    public static List<String> getAllWeeks(String group) {
        if (group.equals("teacher")) {
            String prequery = "SELECT * FROM reg_pass WHERE group_name != ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(prequery)) {

                stmt.setString(1, group);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    group = rs.getString("group_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        List<String> weeks = new ArrayList<>();

        // Запрос для получения уникальных номеров недель из таблицы schedule
        String query = "SELECT DISTINCT week_number FROM schedule WHERE group_ = ? ORDER BY week_number";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, group);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int weekNumber = rs.getInt("week_number");
                // Добавляем только номер недели
                weeks.add("Неделя " + weekNumber);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Логирование ошибки
        }

        return weeks;
    }


    public static String getUserGroup(String username) {
        String query = "SELECT group_ FROM Users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("group_");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Если группа не найдена
    }

    // Проверка, существует ли студент в базе данных
    public static boolean checkIfStudentExists(String username) {
        String query = "SELECT 1 FROM students WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  // Если результат найден, значит студент существует
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkIfTeacherExists(String username) {
        String query = "SELECT 1 FROM teachers WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получение данных студента
    public static Map<String, String> getStudentData(String username) {
        Map<String, String> studentData = new HashMap<>();
        String query = "SELECT * FROM students WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentData.put("last_name", rs.getString("last_name"));
                    studentData.put("first_name", rs.getString("first_name"));
                    studentData.put("birthdate", rs.getString("birthdate"));
                    studentData.put("phone", rs.getString("phone"));
                    studentData.put("email", rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentData;  // Возвращаем данные студента или null, если они не найдены
    }

    public static Map<String, String> getTeacherData(String username) {
        Map<String, String> teacherData = new HashMap<>();
        String query = "SELECT * FROM teachers WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    teacherData.put("last_name", rs.getString("last_name"));
                    teacherData.put("first_name", rs.getString("first_name"));
                    teacherData.put("birthdate", rs.getString("birthdate"));
                    teacherData.put("phone", rs.getString("phone"));
                    teacherData.put("email", rs.getString("email"));
                    teacherData.put("department", rs.getString("department"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teacherData;
    }

    public static boolean saveStudentProfile(String username, String firstName, String lastName, String birthdate, String phone, String email) {
        String query = "INSERT INTO students (username, first_name, last_name, birthdate, phone, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, birthdate);
            ps.setString(5, phone);
            ps.setString(6, email);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;  // Если данные успешно добавлены, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Если произошла ошибка, возвращаем false
        }
    }

    public static boolean saveTeacherProfile(String username, String firstName, String lastName, String birthdate, String phone, String email, String department) {
        String query = "INSERT INTO teachers (username, first_name, last_name, birthdate, phone, email, department) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, birthdate);
            ps.setString(5, phone);
            ps.setString(6, email);
            ps.setString(7, department);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для обновления данных студента в базе данных
    public static boolean updateStudentProfile(String username, String lastName, String firstName, String birthdate, String phone, String email) {
        String query = "UPDATE Students SET last_name = ?, first_name = ?, birthdate = ?, phone = ?, email = ? WHERE username = ?";
        int rowsUpdated = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, lastName); // Устанавливаем параметры для каждого поля
            ps.setString(2, firstName);
            ps.setString(3, birthdate);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, username);

            rowsUpdated = ps.executeUpdate(); // Выполняем обновление
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsUpdated > 0; // Возвращаем true, если обновление прошло успешно
    }

    public static boolean updateTeacherProfile(String username, String lastName, String firstName, String birthdate, String phone, String email, String department) {
        String query = "UPDATE Teachers SET last_name = ?, first_name = ?, birthdate = ?, phone = ?, email = ?, department = ? WHERE username = ?";
        int rowsUpdated = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, lastName);
            ps.setString(2, firstName);
            ps.setString(3, birthdate);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, department);
            ps.setString(7, username);

            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsUpdated > 0;
    }

    public static List<String> getSubjectsForGroup(String group) {
        List<String> subjects = new ArrayList<>();
        String query = "SELECT DISTINCT subject FROM Schedule WHERE group_ = ? AND term = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, group);
            ps.setString(2, getCurrentTerm(group));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(rs.getString("subject"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    public static List<Map<String, String>> getDiaryEntries(String lastName, String firstName, String group, String subject, String curTerm) {
        List<Map<String, String>> entries = new ArrayList<>();
        String query = "SELECT * FROM Diary WHERE group_ = ? AND subject = ? AND last_name = ? AND first_name = ? AND term = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, group);
            ps.setString(2, subject);
            ps.setString(3, lastName);
            ps.setString(4, firstName);
            ps.setString(5, curTerm);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("subject", rs.getString("subject"));
                    entry.put("date", rs.getString("date"));
                    entry.put("mark", rs.getString("mark"));
                    entry.put("comment", rs.getString("comment"));
                    entry.put("coupleType", rs.getString("couple_type"));

                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static List<Map<String, String>> getTeacherSchedule(String username, int weekNumber, Date date) {
        List<Map<String, String>> schedule = new ArrayList<>();

        String teacherLastName = getTeacherLastName(username);

        if (teacherLastName.isEmpty()) {
            return schedule; // Если преподаватель не найден, возвращаем пустой список
        }

        String query = "SELECT * " +
                "FROM schedule WHERE teacher IS NOT NULL and week_number = ? " +
                "ORDER BY FIELD(day_of_week, 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница'), start_time";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, String.valueOf(weekNumber));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teacherFullName = rs.getString("teacher");
                    String[] teacherParts = teacherFullName.split(" "); // Разделяем ФИО
                    String scheduleLastName = teacherParts[0]; // Первая часть - фамилия
                    Date start = rs.getDate("semester_start");
                    Date end = rs.getDate("semester_end");

                    if (scheduleLastName.equals(teacherLastName) && date.after(start) && date.before(end)) {
                        // Если фамилии совпадают, добавляем запись в расписание
                        Map<String, String> entry = new HashMap<>();
                        entry.put("day_of_week", rs.getString("day_of_week"));
                        entry.put("start", rs.getString("start_time"));
                        entry.put("end", rs.getString("end_time"));
                        entry.put("subject", rs.getString("subject"));
                        entry.put("group", rs.getString("group_"));
                        entry.put("date", rs.getString("date"));
                        entry.put("couple_type", rs.getString("couple_type"));
                        schedule.add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Логирование ошибки
        }

        return schedule;
    }

    public static List<String> getStudentName(String username) {
        List<String> names = new ArrayList<>();
        String query = "SELECT last_name, first_name FROM students WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    names.add(rs.getString("first_name"));
                    names.add(rs.getString("last_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names;
    }

    public static List<Map<String, String>> getStudentsInGroup(String group) {
        List<Map<String, String>> students = new ArrayList<>();
        String query = """
            SELECT s.last_name, s.first_name
            FROM Students s
            INNER JOIN Users u ON s.username = u.username
            WHERE u.group_ = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, group);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("last_name", rs.getString("last_name"));
                    entry.put("first_name", rs.getString("first_name"));
                    students.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static String getTeacherLastName(String username) {
        String teacherLastName = "";
        String prequery = "SELECT last_name FROM Teachers WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(prequery)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teacherLastName = rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacherLastName;
    }

    public static List<String> getLessonDates(String group, String username, String subject, Date dateTime) {
        List<String> dates = new ArrayList<>();
        String teacherLastName = getTeacherLastName(username);

        String query = "SELECT date, teacher, semester_start, semester_end FROM Schedule WHERE group_ = ? AND subject = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, group);
            stmt.setString(2, subject);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teacherFullName = rs.getString("teacher");
                    String[] teacherParts = teacherFullName.split(" "); // Разделяем ФИО
                    String scheduleLastName = teacherParts[0]; // Первая часть - фамилия
                    Date start = rs.getDate("semester_start");
                    Date end = rs.getDate("semester_end");

                    if (scheduleLastName.equals(teacherLastName) && dateTime.after(start) && dateTime.before(end)) {
                        dates.add(rs.getString("date"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public static List<Map<String, String>> getDiaryEntriesForGroup(String group, String username, String subject, Date dateTime) {
        List<Map<String, String>> entries = new ArrayList<>();
        String teacherLastName = getTeacherLastName(username);

        String prequery = "SELECT semester_start, semester_end, teacher FROM Schedule";
        Date semStart = new Date(System.currentTimeMillis());
        Date semEnd = new Date(System.currentTimeMillis());

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(prequery)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teacherFullName = rs.getString("teacher");
                    String[] teacherParts = teacherFullName.split(" "); // Разделяем ФИО
                    String scheduleLastName = teacherParts[0]; // Первая часть - фамилия
                    Date start = rs.getDate("semester_start");
                    Date end = rs.getDate("semester_end");

                    if (scheduleLastName.equals(teacherLastName) && dateTime.after(start) && dateTime.before(end)) {
                        semStart = rs.getDate("semester_start");
                        semEnd = rs.getDate("semester_end");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String query = "SELECT * FROM Diary WHERE group_ = ? AND subject = ? AND date > ? AND date < ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, group);
            ps.setString(2, subject);
            ps.setDate(3, semStart);
            ps.setDate(4, semEnd);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("last_name", rs.getString("last_name"));
                    entry.put("first_name", rs.getString("first_name"));
                    entry.put("date", rs.getString("date"));
                    entry.put("mark", rs.getString("mark"));
                    entry.put("comment", rs.getString("comment"));
                    entry.put("couple_type", rs.getString("couple_type"));
                    entry.put("absence", rs.getString("absence"));
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static List<String> getAllGroups(Date date, String username) {
        List<String> groups = new ArrayList<>();

        // Шаг 1: Получаем фамилию преподавателя по username
        String teacherLastName = getTeacherLastName(username);

        if (teacherLastName.isEmpty()) {
            return groups; // Если преподаватель не найден, возвращаем пустой список
        }

        String query = "SELECT * FROM schedule WHERE teacher IS NOT NULL";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teacherFullName = rs.getString("teacher");
                    String[] teacherParts = teacherFullName.split(" "); // Разделяем ФИО
                    String scheduleLastName = teacherParts[0]; // Первая часть - фамилия
                    Date start = rs.getDate("semester_start");
                    Date end = rs.getDate("semester_end");

                    if (scheduleLastName.equals(teacherLastName) && date.after(start) && date.before(end)) {
                        groups.add(rs.getString("group_"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public static List<String> getAllSubjects(Date date, String username) {
        List<String> subjects = new ArrayList<>();

        String teacherLastName = getTeacherLastName(username);

        if (teacherLastName.isEmpty()) {
            return subjects; // Если преподаватель не найден, возвращаем пустой список
        }

        String query = "SELECT * FROM schedule WHERE teacher IS NOT NULL";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teacherFullName = rs.getString("teacher");
                    String[] teacherParts = teacherFullName.split(" "); // Разделяем ФИО
                    String scheduleLastName = teacherParts[0]; // Первая часть - фамилия
                    Date start = rs.getDate("semester_start");
                    Date end = rs.getDate("semester_end");

                    if (scheduleLastName.equals(teacherLastName) && date.after(start) && date.before(end)) {
                        subjects.add(rs.getString("subject"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    public static boolean validateTeacherDiary(String selectedGroup, String subject) {
        String query = "SELECT * FROM Schedule WHERE group_ = ? AND subject = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedGroup);
            stmt.setString(2, subject);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveDiaryEntries(String group, String subject, Map<String, String> coupleInfo, Map<String, Map<String, String>> diaryEntries) {
        String prequery = "UPDATE Schedule SET couple_type = ? WHERE date = ? AND group_ = ? AND subject = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(prequery)) {

            for (Map.Entry<String, Map<String, String>> entry : diaryEntries.entrySet()) {
                String key = entry.getKey();

                String[] parts = key.split("_");
                if (parts.length == 3) {
                    String date = parts[2];
                    String coupleType = coupleInfo.get(date);
                    stmt.setString(1, coupleType);
                    stmt.setString(2, date);
                    stmt.setString(3, group);
                    stmt.setString(4, subject);

                    stmt.addBatch();
                }
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String term = getCurrentTerm(group);
        String query = """
            INSERT INTO Diary (first_name, last_name, group_, subject, date, mark, absence, comment, couple_type, term)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                mark = VALUES(mark),
                comment = VALUES(comment),
                couple_type = VALUES(couple_type),
                absence = VALUES(absence),
                term = VALUES(term)
            """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Map.Entry<String, Map<String, String>> entry : diaryEntries.entrySet()) {
                String key = entry.getKey();
                Map<String, String> values = entry.getValue();

                String[] parts = key.split("_");
                if (parts.length == 3) {
                    String lastName = parts[0];
                    String firstName = parts[1];
                    String date = parts[2];

                    String mark = values.get("mark");
                    String coupleType = coupleInfo.get(date); // Получаем тип пары и номер для даты
                    boolean absence = (!Objects.equals(mark, "н") && coupleType != null && !coupleType.isEmpty());

                    String comment = values.get("comment");

                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, group);
                    preparedStatement.setString(4, subject);
                    preparedStatement.setString(5, date);
                    preparedStatement.setString(6, mark != null && !mark.isEmpty() ? mark : "");
                    preparedStatement.setBoolean(7, absence);
                    preparedStatement.setString(8, comment != null && !comment.isEmpty() ? comment : "");
                    preparedStatement.setString(9, coupleType != null && !coupleType.isEmpty() ? coupleType : "");
                    preparedStatement.setString(10, term);

                    preparedStatement.addBatch();
                }
            }

            preparedStatement.executeBatch();
            return true; // Успешная запись
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Ошибка записи
        }
    }

    public static String getCurrentTerm(String group) {
        String query = "SELECT current_semester FROM current_term WHERE group_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, group);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("current_semester");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return "";
    }

    public static List<Map<String, String>> getGroupStatistics(String username, String selectedGroup, String selectedSubject) {
        List<Map<String, String>> statData = new ArrayList<>();
        //String teacherLastName = getTeacherLastName(username);
        String query = "SELECT * FROM Diary WHERE group_ = ? AND subject = ? AND term = ? ORDER BY couple_type";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, selectedGroup);
            stmt.setString(2, selectedSubject);
            stmt.setString(3, getCurrentTerm(selectedGroup));

            // Выполняем запрос
            try (ResultSet rs = stmt.executeQuery()) {
                // Подсчитываем количество студентов, сдавших пару, по типу пары
                Map<String, Integer> coupleTypeCounts = new HashMap<>();
                Map<String, Integer> passedStudentsCounts = new HashMap<>();
                Map<String, Integer> attendedStudentsCounts = new HashMap<>();
                Map<String, Double> coupleTypeScores = new HashMap<>();

                while (rs.next()) {
                    String coupleType = rs.getString("couple_type");
                    String mark = rs.getString("mark");
                    boolean absence = rs.getBoolean("absence");

                    // Подсчитываем количество студентов по типу пары
                    if (!coupleType.equals(" ") && !coupleType.isEmpty()) {
                        coupleTypeCounts.put(coupleType, coupleTypeCounts.getOrDefault(coupleType, 0) + 1);
                    }

                    // Подсчитываем количество студентов, сдавших пару (оценка != null и не "н")
                    if (!mark.isEmpty() && !mark.equals("н")) {
                        passedStudentsCounts.put(coupleType, passedStudentsCounts.getOrDefault(coupleType, 0) + 1);
                    }

                    // Подсчитываем количество студентов, присутствовавших на паре (absence == true)
                    if (absence) {
                        attendedStudentsCounts.put(coupleType, attendedStudentsCounts.getOrDefault(coupleType, 0) + 1);
                    }
                }

                // Рассчитываем процент сдавших студентов по каждому типу пары
                for (String coupleType : coupleTypeCounts.keySet()) {
                    int totalStudents = coupleTypeCounts.get(coupleType);
                    int passedStudents = passedStudentsCounts.getOrDefault(coupleType, 0);
                    int attendedStudents = attendedStudentsCounts.getOrDefault(coupleType, 0);
                    double passPercentage;
                    double attendancePercentage = totalStudents > 0 ? (double) attendedStudents / totalStudents * 100 : 0;

                    if (coupleType.startsWith("Лекция")) {
                        passPercentage = attendancePercentage;
                        passedStudents = attendedStudents;
                    } else {
                        passPercentage = totalStudents > 0 ? (double) passedStudents / totalStudents * 100 : 0;
                    }

                    Map<String, String> coupleTypeData = new HashMap<>();
                    coupleTypeData.put("couple_type", coupleType);
                    coupleTypeData.put("pass_percentage", String.format("%.2f", passPercentage));
                    coupleTypeData.put("pass_details", passedStudents + "/" + totalStudents);
                    coupleTypeData.put("att_percentage", String.format("%.2f", attendancePercentage));
                    coupleTypeData.put("att_details", attendedStudents + "/" + totalStudents);

                    // Добавляем карту в список
                    statData.add(coupleTypeData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statData;
    }

    public static List<Map<String, String>> getStudentStatistic(String username, String group, String selectedSubject) {
        List<Map<String, String>> statData = new ArrayList<>();
        List<String> names = getStudentName(username);
        //String teacherLastName = getTeacherLastName(username);
        String query = "SELECT * FROM Diary WHERE group_ = ? AND subject = ? AND term = ? AND first_name = ? AND last_name = ? ORDER BY couple_type";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, group);
            stmt.setString(2, selectedSubject);
            stmt.setString(3, getCurrentTerm(group));
            stmt.setString(4, names.getFirst());
            stmt.setString(5, names.getLast());

            try (ResultSet rs = stmt.executeQuery()) {
                // Инициализация данных статистики
                Map<String, Integer> totalCount = new HashMap<>();
                Map<String, Integer> passedCount = new HashMap<>();
                Map<String, Integer> attendedCount = new HashMap<>();

//                Map<String, String> labCount = new HashMap<>();
//                Map<String, String> praktCount = new HashMap<>();
//                Map<String, String> lectCount = new HashMap<>();

                while (rs.next()) {
                    String fullCoupleType = rs.getString("couple_type");
                    String mark = rs.getString("mark");
                    boolean absence = rs.getBoolean("absence");
                    Date dateTime = (Date) rs.getDate("date");
                    Date curDate = new Date(System.currentTimeMillis());
                    if (dateTime.before(curDate)) {

                        // Учитываем пустой тип как отдельную категорию
                        if (!(fullCoupleType == null || fullCoupleType.isEmpty() || fullCoupleType.equals(" "))) {
                            String coupleType = fullCoupleType.split("\\s+")[0];

                            // Общее количество записей по типу пары
                            totalCount.put(coupleType, totalCount.getOrDefault(coupleType, 0) + 1);

//                    if (fullCoupleType.startsWith("Лабораторная")) {
//                        labCount.put(fullCoupleType, (!mark.equals("н") && !mark.isEmpty()) ? "1" : "0");
//                    } else if (fullCoupleType.startsWith("Практическая")) {
//                        praktCount.put(fullCoupleType, (!mark.equals("н") && !mark.isEmpty()) ? "1" : "0");
//                    } else {
//                        lectCount.put(fullCoupleType, (absence) ? "1" : "0");
//                    }

                            // Количество сданных работ
                            if (!mark.equals("н") && !mark.isEmpty()) {
                                passedCount.put(coupleType, passedCount.getOrDefault(coupleType, 0) + 1);
                            }

                            // Количество посещений и непосещений
                            if (absence) {
                                attendedCount.put(coupleType, attendedCount.getOrDefault(coupleType, 0) + 1);
                            }
                        }
                    }
                }

//                statData.add(labCount);
//                statData.add(praktCount);
//                statData.add(lectCount);

                // Формируем итоговую статистику
                for (String coupleType : totalCount.keySet()) {
                    Map<String, String> coupleStat = new HashMap<>();
                    int total = totalCount.get(coupleType);
                    int passed;
                    int attended = attendedCount.getOrDefault(coupleType, 0);

                    if (coupleType.equals("Лекция")) {
                        passed = attended;
                    } else {
                       passed = passedCount.getOrDefault(coupleType, 0);
                    }

                    coupleStat.put("couple_type", coupleType);
                    coupleStat.put("total", String.valueOf(total));
                    coupleStat.put("passed", passed + "/" + total);
                    coupleStat.put("attendance", attended + "/" + total);

                    statData.add(coupleStat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return statData;
    }

    public static boolean addGradeToGradeBook(
            String studentLastName,
            String studentFirstName,
            String subject,
            String term,
            String grade,
            String examDate,
            String comment
    ) {
        String query = "INSERT INTO GradeBook (student_last_name, student_first_name, subject, term, grade, exam_date, comment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, studentLastName);
            statement.setString(2, studentFirstName);
            statement.setString(3, subject);
            statement.setString(4, term);
            statement.setString(5, grade);
            statement.setDate(6, java.sql.Date.valueOf(examDate));
            statement.setString(7, comment);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Возвращаем true, если вставка прошла успешно

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для получения записей из зачётной книжки (GradeBook)
    public static List<Map<String, String>> getGradeBookEntries(String selectedGroup, String selectedSubject) {
        List<Map<String, String>> entries = new ArrayList<>();

        // SQL-запрос для получения записей из GradeBook
        String query = "SELECT student_last_name, student_first_name, grade, exam_date, comment " +
                "FROM GradeBook " +
                "WHERE group_ = ? AND subject = ?";

        // Устанавливаем соединение с базой данных
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "username", "password");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Устанавливаем параметры запроса
            stmt.setString(1, selectedGroup);
            stmt.setString(2, selectedSubject);

            // Выполняем запрос
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Создаем карту для каждой записи
                    Map<String, String> entry = new HashMap<>();
                    entry.put("last_name", rs.getString("student_last_name"));
                    entry.put("first_name", rs.getString("student_first_name"));
                    entry.put("grade", rs.getString("grade"));
                    entry.put("exam_date", rs.getDate("exam_date").toString());
                    entry.put("comment", rs.getString("comment"));

                    // Добавляем запись в список
                    entries.add(entry);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static void main(String[] args) throws SQLException {
        //Date loginTime = new Date(System.currentTimeMillis());
        getStudentStatistic("regnem", "g1", "Теория вероятностей");
        getGroupStatistics("nekto", "g1", "Теория вероятностей");
//        Connection conn = getConnection();
//        Map<String, Map<String, String>> diaryEntries = new HashMap<>();
//
//        // Запись 1
//        Map<String, String> entry1 = new HashMap<>();
//        entry1.put("mark", "5");
//        entry1.put("comment", "Отлично");
//        diaryEntries.put("Иванов_Иван_2024-09-01", entry1);
//
//        // Запись 2
//        Map<String, String> entry2 = new HashMap<>();
//        entry2.put("mark", "4");
//        entry2.put("comment", "Хорошо");
//        diaryEntries.put("Петров_Петр_2024-09-01", entry2);
        //boolean flag = saveDiaryEntries("g1", "Теория вероятностей", "Лаборатроная работа", "1", diaryEntries);
        //System.out.println(flag);
//        boolean flag = validateTeacherDiary("g1", "Теория автоматов");
//        System.out.println(flag);
//        List<Map<String, String>> weeks = getStudentsInGroup("g1");
//        List<String> weeks = getLessonDates("g1", "nekto", "Теория вероятностей");
//        System.out.println(weeks.size());
//        List<Map<String, String>> schedule = getTeacherSchedule("nekto", 2);
//        System.out.println(schedule.getFirst());
//        String regPass = "teacherpass";  // Пример регистрационного пароля
//        String hashedRegPass = hashPassword(regPass);
//
//        // Запрос на добавление хешированного регистрационного пароля в таблицу
//        String query = "INSERT INTO reg_pass (group_name, reg_password) VALUES (?, ?)";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setString(1, "teacher");
//            ps.setString(2, hashedRegPass);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
}