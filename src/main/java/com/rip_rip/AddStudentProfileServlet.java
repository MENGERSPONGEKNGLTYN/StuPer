package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AddStudentProfileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Получаем параметры из формы
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String birthdate = request.getParameter("birthdate");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        // Сохраняем данные студента в базе
        boolean success = DatabaseHelper.saveStudentProfile(username, firstName, lastName, birthdate, phone, email);

        if (success) {
            response.sendRedirect("student_profile.jsp");  // Перенаправляем на профиль
        } else {
            response.getWriter().write("Произошла ошибка при сохранении данных.");
        }
    }
}
