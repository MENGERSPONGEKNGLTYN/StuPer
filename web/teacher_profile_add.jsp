<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Заполните свой профиль</title>
    <link rel="stylesheet" type="text/css" href="css/teacher_profile_change.css">
    <script src="js/teacher_profile_change.js" defer></script>
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
    <h1>Заполните информацию о себе</h1>
</header>

<main>
    <div class="profile-container">
        <h2>Заполнение профиля</h2>
        <!-- Форма отправляется только если валидация прошла -->
        <form action="AddTeacherProfileServlet" method="post" onsubmit="return validateForm()">
            <label for="first_name">Имя:</label>
            <input type="text" id="first_name" name="first_name" required>

            <label for="last_name">Фамилия:</label>
            <input type="text" id="last_name" name="last_name" required>

            <label for="birthdate">Дата рождения:</label>
            <input type="date" id="birthdate" name="birthdate" required>

            <label for="phone">Телефон:</label>
            <input type="tel" id="phone" name="phone">

            <label for="email">Email:</label>
            <input type="email" id="email" name="email">

            <label for="department">Отдел:</label>
            <input type="text" id="department" name="department">

            <button type="submit">Сохранить</button>
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
