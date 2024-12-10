<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String curTerm = DatabaseHelper.getCurrentTerm(group);

    // Получаем список всех недель для группы
    List<String> weeks = DatabaseHelper.getAllWeeks(group);

    // Загружаем расписание для выбранной недели (по умолчанию первая неделя)
    String selectedWeek = request.getParameter("week");
    List<Map<String, String>> schedule;
    if (selectedWeek != null) {
        schedule = DatabaseHelper.getScheduleForWeek(group, Integer.parseInt(selectedWeek), curTerm);
    } else {
        schedule = DatabaseHelper.getScheduleForWeek(group, 1, curTerm); // По умолчанию показываем расписание первой недели
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Расписание</title>
    <link rel="stylesheet" type="text/css" href="css/schedule.css">
</head>
<body>
<header>
    <h1>Ваше расписание</h1>
    <div class="auth-buttons">
        <!-- Кнопка профиля и дневника -->
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="student_profile.jsp">Мой профиль</a>
                <a href="student_diary.jsp">Мой дневник</a>
                <a href="student_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>

<main>
    <div class="schedule-container">
        <h2>Выберите неделю для просмотра расписания</h2>

        <!-- Форма для выбора недели -->
        <form action="schedule.jsp" method="get">
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
                        for (Map<String, String> entry : schedule) {
                            if (entry.get("day_of_week").equals(day)) {
                                currentDate = entry.get("date");
                                break;
                            }
                        }

                        out.print("<tr><td colspan='4' style='text-align: center; background-color: #f8f9fa; font-weight: bold;'>");
                        out.print(day + " (" + currentDate + ")"); // Заголовок для дня недели
                        out.print("</td></tr>");

                        // Для каждого дня выводим даты и уроки
                        for (Map<String, String> entry : schedule) {
                            if (entry.get("day_of_week").equals(day)) {
                                out.print("<tr>");

                                // Выводим время
                                out.print("<td>" + " (" + entry.get("start") + " - " + entry.get("end") + ")</td>");

                                // Выводим название предмета
                                out.print("<td>" + entry.get("subject") + "</td>");

                                // Преподаватель
                                out.print("<td>" + entry.get("teacher") + "</td>");

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
