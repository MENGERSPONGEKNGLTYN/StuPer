<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.sql.Date, com.rip_rip.DatabaseHelper" %>

<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    // Получаем имя преподавателя из сессии
    String username = (String) session.getAttribute("username");
    Date date = (Date) session.getAttribute("loginTime");
    // Получаем список всех недель
    List<String> weeks = DatabaseHelper.getAllWeeks(group);

    // Получаем выбранную неделю
    String selectedWeek = request.getParameter("week");
    List<Map<String, String>> teacherSchedule;
    if (selectedWeek != null) {
        teacherSchedule = DatabaseHelper.getTeacherSchedule(username, Integer.parseInt(selectedWeek), date); // Получаем расписание для выбранной недели
    } else {
        teacherSchedule = DatabaseHelper.getTeacherSchedule(username, 1, date); // По умолчанию показываем расписание первой недели
    }
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Расписание</title>
    <link rel="stylesheet" type="text/css" href="css/dashboard.css">
</head>
<body>
<header>
    <h1>Ваше расписание</h1>
    <div class="auth-buttons">
        <!-- Кнопка профиля и дневника -->
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="teacher_profile.jsp">Мой профиль</a>
                <a href="teacher_diary.jsp">Журнал</a>
                <a href="teacher_report.jsp">Статистика</a>
<%--                <a href="teacher_grade_book.jsp">Зачетка</a>--%>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>

<main>
    <div class="schedule-container">
        <h2>Выберите неделю для просмотра расписания</h2>

        <!-- Форма для выбора недели -->
        <form action="dashboard.jsp" method="get">
            <label for="week">Выберите неделю:</label>
            <select id="week" name="week" required>
                <%
                    for (String week : weeks) {
                        String weekNumber = week.split(" ")[1];
                        out.println("<option value='" + weekNumber + "'"
                                + (week.equals(selectedWeek) ? " selected" : "") // Устанавливаем выбранную неделю
                                + ">" + week + "</option>");
                    }
                %>
            </select>
            <button type="submit">Показать расписание</button>
        </form>

        <h3>Расписание на неделю <%= selectedWeek != null ? selectedWeek : "1" %></h3>
        <div class="table-container">
            <table>
                <tbody>
                <%
                    // Для каждого дня недели создаем строку с расписанием
                    String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница"};
                    for (String day : daysOfWeek) {
                        // Находим дату для текущего дня
                        String currentDate = "";
                        for (Map<String, String> entry : teacherSchedule) {
                            if (entry.get("day_of_week").equals(day)) {
                                currentDate = entry.get("date");
                                break;
                            }
                        }

                        out.print("<tr><td colspan='4' style='text-align: center; background-color: #f8f9fa; font-weight: bold;'>");
                        out.print(day + " (" + currentDate + ")"); // Заголовок для дня недели
                        out.print("</td></tr>");

                        // Для каждого дня выводим даты и уроки
                        for (Map<String, String> entry : teacherSchedule) {
                            if (entry.get("day_of_week").equals(day)) {
                                out.print("<tr>");

                                // Выводим время
                                out.print("<td>" + " (" + entry.get("start") + " - " + entry.get("end") + ")</td>");

                                // Группа
                                out.print("<td>" + entry.get("group") + "</td>");

                                // Выводим название предмета
                                out.print("<td>" + entry.get("subject") + "</td>");

                                // Тип пары
                                out.print("<td>" + (!entry.get("couple_type").isEmpty() ? entry.get("couple_type") : "") + "</td>");

                                out.print("</tr>");
                            }
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</main>

<footer>
    <p>&copy; 2024 Система мониторинга успеваемости студентов</p>
</footer>
</body>
</html>
