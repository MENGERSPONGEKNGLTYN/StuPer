package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем параметры из запроса
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Проверка логина с помощью DatabaseHelper
        String userType = DatabaseHelper.checkLogin(username, password);

        // Если логин успешный, перенаправляем на панель управления
        if (userType != null) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("userType", userType);
            String group = DatabaseHelper.getUserGroup(username);
            session.setAttribute("group", group);
            Date loginTime = new Date(System.currentTimeMillis());;
            session.setAttribute("loginTime", loginTime);
            switch (userType) {
                case "admin" -> response.sendRedirect("admin.jsp");
                case "student" -> response.sendRedirect("schedule.jsp");
                case "teacher" -> response.sendRedirect("dashboard.jsp");
            }
        } else {
            // Если логин неверный, показываем ошибку
            request.setAttribute("errorMessage", "Неверный логин или пароль.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
