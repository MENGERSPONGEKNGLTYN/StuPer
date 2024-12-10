<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");

    // Проверяем, существует ли пользователь в базе данных
    boolean userExists = DatabaseHelper.checkIfTeacherExists(username);

    // Если пользователь не существует, показываем форму для заполнения
    if (!userExists) {
        // Перенаправляем на форму для заполнения информации
        response.sendRedirect("teacher_profile_add.jsp");
        return;
    }

    // Если пользователь существует, загружаем его данные
    Map<String, String> studentData = DatabaseHelper.getTeacherData(username);
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Мой профиль</title>
    <link rel="stylesheet" type="text/css" href="css/teacher_profile.css">
</head>
<body>
<header>
    <h1>Профиль преподавателя</h1>
    <div class="auth-buttons">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="dashboard.jsp">Расписание</a>
                <a href="teacher_diary.jsp">Журнал</a>
                <a href="teacher_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>

<main>
    <h2>Личная информация</h2>
    <div class="profile-info">
        <div class="profile-item">
            <span class="label">Фамилия:</span>
            <span class="value"><%= studentData.get("last_name") %></span>
        </div>
        <div class="profile-item">
            <span class="label">Имя:</span>
            <span class="value"><%= studentData.get("first_name") %></span>
        </div>
        <div class="profile-item">
            <span class="label">Дата рождения:</span>
            <span class="value"><%= studentData.get("birthdate") %></span>
        </div>
        <div class="profile-item">
            <span class="label">Телефон:</span>
            <span class="value"><%= studentData.get("phone") %></span>
        </div>
        <div class="profile-item">
            <span class="label">Email:</span>
            <span class="value"><%= studentData.get("email") %></span>
        </div>
        <div class="profile-item">
            <span class="label">Отдел:</span>
            <span class="value"><%= studentData.get("department") %></span>
        </div>
        <div class="edit-button-container">
            <a href="teacher_profile_edit.jsp" class="edit-button">Редактировать профиль</a>
        </div>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>

</body>
</html>

