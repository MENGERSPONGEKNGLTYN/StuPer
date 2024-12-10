<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <title>Вход в систему</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/login.css">
    <script src="js/login.js" defer></script>
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
        <div class="login-container">
            <h2>Вход в систему</h2>
            <form action="LoginServlet" method="post" onsubmit="return validateForm()">
                <div>
                    <label for="username">Логин:</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div>
                    <label for="password">Пароль:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit">Войти</button>
            </form>
            <!-- Контейнер для вывода ошибок -->
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
