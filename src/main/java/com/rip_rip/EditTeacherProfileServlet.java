package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class EditTeacherProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String lastName = request.getParameter("last_name");
        String firstName = request.getParameter("first_name");
        String birthdate = request.getParameter("birthdate");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String department = request.getParameter("department");

        // Обновляем данные в базе данных
        boolean updateSuccessful = DatabaseHelper.updateTeacherProfile(username, lastName, firstName, birthdate, phone, email, department);

        if (updateSuccessful) {
            response.sendRedirect("teacher_profile.jsp"); // Перенаправляем на профиль
        } else {
            request.setAttribute("errorMessage", "Ошибка при обновлении профиля.");
            request.getRequestDispatcher("teacher_profile_edit.jsp").forward(request, response);
        }
    }
}
