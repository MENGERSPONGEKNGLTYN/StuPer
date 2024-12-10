<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Завершение текущей сессии пользователя
    if (session != null) {
        session.invalidate(); // Инвалидируем сессию
    }

    // Перенаправление на страницу входа
    response.sendRedirect("index.jsp");
%>
