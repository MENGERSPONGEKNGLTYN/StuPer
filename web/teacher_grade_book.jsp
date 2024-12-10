<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.rip_rip.DatabaseHelper, java.sql.Date" %>
<%
    String group = (String) session.getAttribute("group");
    if (group == null || !group.equals("teacher")) {
        response.sendRedirect("logout.jsp");
        return;
    }

    // Получаем имя преподавателя из сессии
    String username = (String) session.getAttribute("username");
    Date dateTime = (Date) session.getAttribute("loginTime");
    String selected_group = request.getParameter("group");
    String selectedSubject = request.getParameter("subject");
    List<Map<String, String>> students = null;
    if (selected_group != null) {
        students = DatabaseHelper.getStudentsInGroup(selected_group);
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Заполнение зачётной книжки</title>
    <link rel="stylesheet" href="css/gradebook.css">
</head>
<body>
<header>
    <h1>Заполнение зачётной книжки</h1>
</header>
<main>
    <form action="teacher_grade_book.jsp" method="get">
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
    <% if (students != null) {%>
    <form action="GradeBookServlet" method="post">
        <input type="hidden" name="group" value="<%= request.getParameter("group") %>">
        <input type="hidden" name="subject" value="<%= request.getParameter("subject") %>">
        <label for="student">Студент:</label>
        <select id="student" name="student" required>
            <%
                // Получение списка студентов
                for (Map<String, String> student : students) {
                    String studentLastName = student.get("last_name");
                    String studentFirstName = student.get("first_name");
                    out.println("<option value='" + studentLastName + "," + studentFirstName + "'>"
                            + studentLastName + " " + studentFirstName + "</option>");
                }
            %>
        </select>

        <label for="term">Семестр:</label>
        <input type="text" id="term" name="term" value="<%= DatabaseHelper.getCurrentTerm(selected_group) %>" readonly>

        <label for="grade">Оценка:</label>
        <input type="text" id="grade" name="grade" required>

        <label for="exam_date">Дата зачёта:</label>
        <input type="date" id="exam_date" name="exam_date" required>

        <label for="comment">Комментарий:</label>
        <textarea id="comment" name="comment"></textarea>

        <button type="submit">Сохранить</button>
    </form>
    <% } %>
</main>
</body>
</html>
