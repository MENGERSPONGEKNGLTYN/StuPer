<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    String group = (String) session.getAttribute("group");
    if (group != null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
    <link rel="stylesheet" type="text/css" href="css/register.css">
    <script src="js/register.js" defer></script>
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
        <h1>Система мониторинга успеваемости студентов</h1>
    </header>
    <main>
        <div class="register-container">
            <h2>Регистрация</h2>
            <!-- Форма отправляется только если валидация прошла -->
            <form action="RegisterServlet" method="post" onsubmit="return validateForm()">
                <label for="username">Логин:</label>
                <input type="text" id="username" name="username" required>

                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" required>

                <label for="confirmPassword">Подтвердите пароль:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>

                <!-- Выбор типа пользователя -->
                <label for="userType">Тип пользователя:</label>
                <select id="userType" name="userType" required onchange="toggleGroupSelection()">
                    <option value="teacher">Преподаватель</option>
                    <option value="student">Студент</option>
                </select>

                <div id="groupSelection" style="display:none;">
                    <label for="group">Выберите группу:</label>
                    <select id="group" name="group">
                        <option value="G1">G1</option>
                        <option value="G2">G2</option>
                        <option value="G3">G3</option>
                        <option value="G4">G4</option>
                    </select>
                </div>

                <label for="regPassword">Регистрационный пароль:</label>
                <input type="password" id="regPassword" name="regPassword" required>

                <button type="submit">Зарегистрироваться</button>
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
