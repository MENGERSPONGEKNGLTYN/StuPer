package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем параметры из запроса
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");
        String group = userType.equals("student") ? request.getParameter("group") : "teacher";
        String regPass = request.getParameter("regPassword");

        // Добавление нового пользователя в базу данных
        boolean isRegistered = DatabaseHelper.registerUser(username, password, userType, group, regPass);

        if (isRegistered) {
            // Перенаправляем на страницу входа после успешной регистрации
            response.sendRedirect("login.jsp");
        } else {
            // Если ошибка, выводим сообщение об ошибке
            request.setAttribute("errorMessage", "Пользователь с таким логином уже существует или введен неправильный регистрационный пароль.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
