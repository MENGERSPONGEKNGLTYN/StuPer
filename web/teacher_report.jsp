<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper, java.sql.Date" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");
    String selectedGroup = request.getParameter("group");
    String selectedSubject = request.getParameter("subject");
    Date dateTime = (Date) session.getAttribute("loginTime");

    List<Map<String, String>> groupStats = null;

    if (selectedGroup != null && selectedSubject != null) {
        groupStats = DatabaseHelper.getGroupStatistics(username, selectedGroup, selectedSubject);

        // Сортировка данных по алфавиту по типу пары (couple_type)
        Collections.sort(groupStats, (stat1, stat2) -> stat1.get("couple_type").compareTo(stat2.get("couple_type")));
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Статистика</title>
    <link rel="stylesheet" type="text/css" href="css/teacher_report.css">
</head>
<body>
<header>
    <h1>Статистика</h1>
    <div class="auth-buttons">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="teacher_profile.jsp">Профиль</a>
                <a href="dashboard.jsp">Расписание</a>
                <a href="teacher_diary.jsp">Дневник</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>
<main>
    <div class="schedule-container">
        <form action="teacher_report.jsp" method="get">
            <label for="group">Выберите группу:</label>
            <select id="group" name="group" required>
                <option value="" disabled selected>Выберите группу</option>
                <%
                    // Получаем список групп из базы
                    List<String> groups = DatabaseHelper.getAllGroups(dateTime, username).stream().distinct().toList();
                    for (String grp : groups) {
                        out.println("<option value='" + grp + "'"
                                + (grp.equals(request.getParameter("group")) ? " selected" : "")
                                + ">" + grp + "</option>");
                    }
                %>
            </select>

            <label for="subject">Предмет:</label>
            <select id="subject" name="subject" required>
                <option value="" disabled selected>Выберите предмет</option>
                <%
                    // Получаем список предметов из базы
                    List<String> subjects = DatabaseHelper.getAllSubjects(dateTime, username).stream().distinct().toList();
                    for (String subj : subjects) {
                        out.println("<option value='" + subj + "'"
                                + (subj.equals(request.getParameter("subject")) ? " selected" : "")
                                + ">" + subj + "</option>");
                    }
                %>
            </select>

            <button type="submit">Загрузить</button>
        </form>

        <% if (groupStats != null) { %>
        <table>
            <thead>
            <tr>
                <th>Тип пары</th>
                <th>Процент сдавших</th>
                <th>Процент посещаемости</th>
            </tr>
            </thead>
            <tbody>
            <%
                for (Map<String, String> stat : groupStats) {
                    String coupleType = stat.get("couple_type");
                    String passPercentage = stat.get("pass_percentage");
                    String passDetails = stat.get("pass_details");
                    String attendancePercentage = stat.get("att_percentage");
                    String attendanceDetails = stat.get("att_details");
            %>
            <tr>
                <td><%= coupleType %></td>
                <td><%= passPercentage %> % (<%= passDetails %>)</td>
                <td><%= attendancePercentage %> % (<%= attendanceDetails %>)</td>
            </tr>
            <% } %>
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
