package com.rip_rip;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GradeBookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем параметры из формы
        String studentParam = request.getParameter("student"); // Получаем строку "LastName,FirstName"
        String[] studentData = studentParam.split(","); // Разделяем по запятой
        String studentLastName = studentData[0]; // Фамилия
        String studentFirstName = studentData[1]; // Имя
        String subject = request.getParameter("subject");
        String term = request.getParameter("term");
        String grade = request.getParameter("grade");
        String examDate = request.getParameter("exam_date");
        String comment = request.getParameter("comment");

        // Проверяем, успешно ли добавлена запись
        boolean success = DatabaseHelper.addGradeToGradeBook(
                studentLastName, studentFirstName, subject, term, grade, examDate, comment
        );

        // Перенаправляем на страницу с результатом
        if (success) {
            response.sendRedirect("teacher_grade_book.jsp"); // Страница успеха
        } else {
            request.setAttribute("errorMessage", "Произошла ошибка при сохранении данных.");
            response.sendRedirect("teacher_grade_book.jsp");
        }
    }
}
