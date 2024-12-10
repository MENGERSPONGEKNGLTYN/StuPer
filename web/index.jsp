<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Проверяем, есть ли активная сессия и имя пользователя
    String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>StuPer</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/index.css">
</head>
<body>
<header>
    <div class="header-container">
        <h1>Система мониторинга успеваемости студентов</h1>
        <p>Легко отслеживайте успеваемость студентов, посещаемость и прогресс!</p>
    </div>
    <% if (username != null) {
        String group = (String) session.getAttribute("group");
        if (group.equals("teacher")) {
    %>
    <!-- Если пользователь вошел, показываем имя и кнопку выхода -->
    <span class="welcome-message">Здравствуйте, <%= username %>!</span>
    <div class="auth-buttons-menu">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="teacher_profile.jsp">Профиль</a>
                <a href="dashboard.jsp">Расписание</a>
                <a href="teacher_diary.jsp">Журнал</a>
                <a href="teacher_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
        <% } else { %>
    <div class="auth-buttons_menu">
        <!-- Кнопка профиля и дневника -->
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="student_profile.jsp">Мой профиль</a>
                <a href="schedule.jsp">Расписание</a>
                <a href="student_diary.jsp">Мой дневник</a>
                <a href="student_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
        <% } %>
    <% } else { %>
    <div class="auth-buttons">
        <!-- Если пользователь не вошел, показываем кнопки входа и регистрации -->
        <a href="login.jsp" class="btn login-btn">Войти</a>
        <a href="register.jsp" class="btn register-btn">Зарегистрироваться</a>
    </div>
    <% } %>
</header>

<main>
    <div class="features">
        <h2>Особенности системы</h2>
        <ul>
            <li><strong>Мониторинг успеваемости студентов:</strong> преподаватели могут отслеживать текущие оценки студентов по своим предметам в реальном времени.</li>
            <li><strong>Отслеживание посещаемости:</strong> система позволяет вести учет посещаемости студентов, включая информацию по конкретным занятиям и группам.</li>
            <li><strong>Групповые расписания:</strong> преподаватели могут легко управлять расписанием групп, добавлять новые занятия и изменять время проведения пар.</li>
            <li><strong>Статистика по типам пар:</strong> система позволяет просматривать преподавателям и студентам статистику посещаемости и сданных работ по своим предметам.</li>
            <li><strong>Инструментарий преподавателя:</strong> инструменты для преподавателей, позволяющие обновлять информацию о студентах, их успеваемости и посещаемости.</li>
        </ul>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>
</body>
</html>
