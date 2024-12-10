<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper" %>
<%@ page import="java.sql.Date" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    String selected_group = request.getParameter("group");
    String subject = request.getParameter("subject");
    String username = (String) session.getAttribute("username");

    List<Map<String, String>> students = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    List<Map<String, String>> diaryEntries = new ArrayList<>();

    Date dateTime = (Date) session.getAttribute("loginTime");

    if (DatabaseHelper.validateTeacherDiary(selected_group, subject)) {
        // Получение списка студентов
        students = DatabaseHelper.getStudentsInGroup(selected_group);

        // Получение дат занятий
        dates = DatabaseHelper.getLessonDates(selected_group, username, subject, dateTime);

        // Получение существующих записей в дневнике
        diaryEntries = DatabaseHelper.getDiaryEntriesForGroup(selected_group, username, subject, dateTime);
    }
    System.out.println(students.size());
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Дневник</title>
    <link rel="stylesheet" type="text/css" href="css/teacher_diary.css">
</head>
<body>
<header>
    <h1>Электронный журнал</h1>
    <div class="auth-buttons">
        <div class="dropdown">
            <button class="dropbtn">Меню</button>
            <div class="dropdown-content">
                <a href="teacher_profile.jsp">Профиль</a>
                <a href="dashboard.jsp">Расписание</a>
                <a href="teacher_report.jsp">Статистика</a>
                <a href="logout.jsp">Выйти</a>
            </div>
        </div>
    </div>
</header>
<main>
    <form action="teacher_diary.jsp" method="get">
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

    <% if (!students.isEmpty() && !dates.isEmpty()) { %>
    <div class="table-container">
        <form action="DiaryServlet" method="post">
            <input type="hidden" name="group" value="<%= request.getParameter("group") %>">
            <input type="hidden" name="subject" value="<%= request.getParameter("subject") %>">
            <table>
                <thead>
                <tr>
                    <th>Фамилия Имя</th>
                    <% for (String date : dates) { %>
                    <th>
                        <div><%= date %></div>
                        <div>
                            <label for="<%= date + "_type" %>">Тип пары:</label>
                            <select id="<%= date + "_type" %>" name="couple_type[<%= date %>]">
                                <%
                                    String fullCoupleType = "";
                                    String coupleType = "";
                                    String coupleNumber = "";
                                    for (Map<String, String> entry : diaryEntries) {
                                        if (entry.get("date").equals(date)) {
                                            fullCoupleType = entry.get("couple_type");

                                            if (fullCoupleType != null && !fullCoupleType.isEmpty()) {
                                                int lastSpaceIndex = fullCoupleType.lastIndexOf(' ');

                                                if (lastSpaceIndex != -1) {
                                                    coupleType = fullCoupleType.substring(0, lastSpaceIndex);
                                                    coupleNumber = fullCoupleType.substring(lastSpaceIndex + 1);
                                                } else {
                                                    coupleType = fullCoupleType;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                %>
                                <option value="" <%= coupleType.isEmpty() ? "selected" : "" %>>-</option>
                                <option value="Лабораторная работа" <%= "Лабораторная работа".equals(coupleType) ? "selected" : "" %>>Л/р</option>
                                <option value="Практическая работа" <%= "Практическая работа".equals(coupleType) ? "selected" : "" %>>П/р</option>
                                <option value="Лекция" <%= "Лекция".equals(coupleType) ? "selected" : "" %>>Лекция</option>
                            </select>

                            <label for="<%= date + "_number" %>"></label>
                            <input type="number" id="<%= date + "_number" %>" name="couple_number[<%= date %>]" placeholder="Номер"
                                   value="<%= !coupleNumber.isEmpty() ? coupleNumber : "" %>" min="0" max="20">
                        </div>
                    </th>
                    <% } %>
                </tr>
                </thead>
                <tbody>
                <% for (Map<String, String> student : students) {
                    String firstName = student.get("first_name");
                    String lastName = student.get("last_name");
                    String studentName = lastName + " " + firstName;
                %>
                <tr>
                    <td><%= studentName %></td>
                    <% for (String date : dates) {
                        String entryKey = lastName + "_" + firstName + "_" + date;
                        String mark = "";
                        String comment = "";

                        // Проверка существующих записей
                        for (Map<String, String> entry : diaryEntries) {
                            if (entry.get("last_name").equals(lastName) &&
                                    entry.get("first_name").equals(firstName) &&
                                    entry.get("date").equals(date)) {
                                mark = entry.get("mark");
                                comment = entry.get("comment");
                                break;
                            }
                        }
                    %>
                    <td>
                        <label for="<%= entryKey + "_mark" %>"></label>
                        <input type="text" id="<%= entryKey + "_mark" %>"
                               name="marks[<%= entryKey %>]"
                               value="<%= mark %>"
                               placeholder="Оценка"
                               pattern="[02345н]"
                               title="Разрешены только цифры 0, 2, 3, 4, 5 или буква 'н'">

                        <label for="<%= entryKey + "_comment" %>"></label>
                        <input type="text" id="<%= entryKey + "_comment" %>"
                               name="comments[<%= entryKey %>]"
                               value="<%= comment %>"
                               placeholder="Комментарий"
                               maxlength="39">
                    </td>
                    <% } %>
                </tr>
                <% }} %>
                </tbody>
            </table>
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
