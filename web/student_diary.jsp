<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String curTerm = DatabaseHelper.getCurrentTerm(group);

    // Получаем список всех предметов для группы
    List<String> subjects = DatabaseHelper.getSubjectsForGroup(group);
    String selectedSubject = request.getParameter("subject");

    // Если выбран предмет, загружаем записи
    List<Map<String, String>> diaryEntries = new ArrayList<>();
    if (selectedSubject != null) {
        String username = (String) session.getAttribute("username");
        List<String> names = DatabaseHelper.getStudentName(username);
        diaryEntries = DatabaseHelper.getDiaryEntries(names.getLast(), names.getFirst(), group, selectedSubject, curTerm);
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Мой дневник</title>
    <link rel="stylesheet" type="text/css" href="css/student_diary.css">
</head>
<body>
<header>
    <h1>Мой дневник</h1>
    <div class="auth-buttons">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="student_profile.jsp">Мой профиль</a>
                <a href="schedule.jsp">Мое расписание</a>
                <a href="student_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>

<main>
    <div class="diary-container">
        <h2>Записи по предметам</h2>

        <!-- Форма выбора предмета -->
        <form action="student_diary.jsp" method="get">
            <label for="subject">Выберите предмет:</label>
            <select id="subject" name="subject" required>
                <%
                    for (String subject : subjects) {
                        out.println("<option value='" + subject + "'>" + subject + "</option>");
                    }
                %>
            </select>

            <button type="submit">Показать записи</button>
        </form>

        <h3>Мои записи</h3>
        <table>
            <thead>
            <tr>
                <th>Предмет</th>
                <th>Дата</th>
                <th>Оценка</th>
                <th>Комментарий</th>
                <th>Тип пары</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (!diaryEntries.isEmpty()) {
                    for (Map<String, String> entry : diaryEntries) {
                        if (!entry.get("mark").equals("")) {
                            out.println("<tr>");
                            out.println("<td>" + entry.get("subject") + "</td>");
                            out.println("<td>" + entry.get("date") + "</td>");
                            out.println("<td>" + entry.get("mark") + "</td>");
                            out.println("<td>" + entry.get("comment") + "</td>");
                            out.println("<td>" + entry.get("coupleType") + "</td>");
                            out.println("</tr>");
                        }
                    }
                } else {
                    out.println("<tr><td colspan='4'>Записей нет</td></tr>");
                }
            %>
            </tbody>
        </table>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>
</body>
</html>
