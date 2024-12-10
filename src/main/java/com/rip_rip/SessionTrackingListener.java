package com.rip_rip;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionTrackingListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // Получаем список активных сессий
        Set<HttpSession> sessions = (Set<HttpSession>) context.getAttribute("activeSessions");
        if (sessions == null) {
            sessions = Collections.synchronizedSet(new HashSet<>());
            context.setAttribute("activeSessions", sessions);
        }

        // Добавляем новую сессию в список
        sessions.add(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // Удаляем сессию из списка активных сессий
        Set<HttpSession> sessions = (Set<HttpSession>) context.getAttribute("activeSessions");
        if (sessions != null) {
            sessions.remove(session);
        }
    }
}

