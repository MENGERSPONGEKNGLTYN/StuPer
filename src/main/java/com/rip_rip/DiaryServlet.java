package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiaryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем группу и предмет из параметров запроса
        String group = request.getParameter("group");
        String subject = request.getParameter("subject");

        // Извлекаем параметры формы
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Map<String, String>> diaryEntries = new HashMap<>();
        Map<String, String> coupleInfo = new HashMap<>();

        for (String key : parameterMap.keySet()) {
            if (key.startsWith("couple_type")) {
                String date = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                coupleInfo.put(date, parameterMap.get(key)[0]); // Сохраняем тип пары
            } else if (key.startsWith("couple_number")) {
                String date = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                String coupleType = coupleInfo.get(date);
                coupleInfo.put(date, coupleType + " " + parameterMap.get(key)[0]); // Добавляем номер пары
            }
        }

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();

            if (key.startsWith("marks") || key.startsWith("comments")) {
                String[] parts = key.substring(key.indexOf("[") + 1, key.indexOf("]")).split("_");
                if (parts.length == 3) {
                    String lastName = parts[0];
                    String firstName = parts[1];
                    String date = parts[2];

                    diaryEntries.putIfAbsent(lastName + "_" + firstName + "_" + date, new HashMap<>());
                    String valueKey = key.startsWith("marks") ? "mark" : "comment";
                    diaryEntries.get(lastName + "_" + firstName + "_" + date).put(valueKey, entry.getValue()[0]);
                }
            }
        }

        // Сохраняем данные в базе через DatabaseHelper
        boolean isSaved = DatabaseHelper.saveDiaryEntries(group, subject, coupleInfo, diaryEntries);

        if (isSaved) {
            // Успешная запись
            request.setAttribute("errorMessage", "Успешно.");
            response.sendRedirect("teacher_diary.jsp");
        } else {
            // Ошибка записи
            request.setAttribute("errorMessage", "Произошла ошибка при сохранении записей. Попробуйте снова.");
            request.getRequestDispatcher("teacher_diary.jsp").forward(request, response);
        }
    }
}



