<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");

    // Загружаем данные студента
    Map<String, String> studentData = DatabaseHelper.getStudentData(username);
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Редактировать профиль</title>
    <link rel="stylesheet" type="text/css" href="css/student_profile_change.css">
    <script src="js/student_profile_change.js" defer></script>
    <script>
        // Привязываем функцию очистки к полям формы
        window.onload = function() {
            const formElements = document.querySelectorAll('input');
            formElements.forEach(function(element) {
                    element.addEventListener('input', clearErrorMessage);
                }
            );
        };
    </script>
</head>
<body>
<header>
    <h1>Редактировать профиль</h1>
</header>

<main>
    <div class="profile-container">
        <h2>Редактирвоание профиля</h2>
        <!-- Форма отправляется только если валидация прошла -->
        <form action="EditStudentProfileServlet" method="POST" onsubmit="return validateForm()">
            <label for="last_name">Фамилия:</label>
            <input type="text" id="last_name" name="last_name" value="<%= studentData.get("last_name") %>" required>

            <label for="first_name">Имя:</label>
            <input type="text" id="first_name" name="first_name" value="<%= studentData.get("first_name") %>" required>

            <label for="birthdate">Дата рождения:</label>
            <input type="date" id="birthdate" name="birthdate" value="<%= studentData.get("birthdate") %>" required>

            <label for="phone">Телефон:</label>
            <input type="text" id="phone" name="phone" value="<%= studentData.get("phone") %>">

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<%= studentData.get("email") %>">

            <button type="submit">Сохранить изменения</button>
        </form>

        <!-- Блок для вывода ошибок -->
        <div id="errorMessage">
            <%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "" %>
        </div>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>

</body>
</html>
