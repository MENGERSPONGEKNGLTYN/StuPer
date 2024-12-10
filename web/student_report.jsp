<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    List<Map<String, String>> stats = null;
    String username = (String) session.getAttribute("username");

    // Получаем список всех предметов для группы
    List<String> subjects = DatabaseHelper.getSubjectsForGroup(group);
    String selectedSubject = request.getParameter("subject");

    if (selectedSubject != null) {
        stats = DatabaseHelper.getStudentStatistic(username, group, selectedSubject);
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Статистика</title>
    <link rel="stylesheet" type="text/css" href="css/student_report.css">
</head>
<body>
<header>
    <h1>Статистика</h1>
    <div class="auth-buttons">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="student_profile.jsp">Мой профиль</a>
                <a href="schedule.jsp">Мое расписание</a>
                <a href="student_diary.jsp">Дневник</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>

<main>
    <div class="report-container">
        <!-- Форма выбора предмета -->
        <form action="student_report.jsp" method="get">
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

    <% if (stats != null) { %>
    <h2>Статистика по предмету <%= selectedSubject %></h2>
        <table>
            <thead>
            <tr>
                <th>Тип пары</th>
                <th>Всего</th>
                <th>Сдано</th>
                <th>Посещено</th>
            </tr>
            </thead>
            <tbody>
            <%
//                Map<String, String> labCount = stats.getFirst();
//                Map<String, String> praktCount = stats.get(1);
//                Map<String, String> lectCount = stats.get(2);
//                stats.remove(0);
//                stats.remove(1);
//                stats.remove(2);
                for (Map<String, String> stat : stats) {
                    String coupleType = stat.get("couple_type");
                    String total = stat.get("total");
                    String passed = stat.get("passed");
                    String attendance = stat.get("attendance");
//                    for (String keu : stat. keySet())
            %>
            <tr>
                <td><%=Objects.equals(coupleType, "Лабораторная") ? "Л/р" : Objects.equals(coupleType, "Практическая") ? "П/р" : coupleType%></td>
                <td><%= total %></td>
                <td><%= passed %></td>
                <td><%= attendance %></td>
            </tr>
            <% } %>
<%--            <%--%>
<%--                for (String labKey : labCount.keySet()) {--%>
<%--            %>--%>
<%--                <tr>--%>
<%--                    <td><%= labKey %></td>--%>
<%--                    <td>""</td>--%>
<%--                    <td><%=Objects.equals(labCount.get(labKey), "1") ? "Сдано" : "Не сдано" %></td>--%>
<%--                    <td>""</td>--%>
<%--                </tr>--%>
<%--            <% } %>--%>
            </tbody>
        </table>
    <% } %>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>

</body>
</html>
