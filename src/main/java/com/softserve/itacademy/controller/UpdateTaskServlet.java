package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.TaskRepository;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/edit-task")
public class UpdateTaskServlet extends HttpServlet {

    private TaskRepository taskRepository;

    @Override
    public void init() {
        taskRepository = TaskRepository.getTaskRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(idParam);
            Task task = taskRepository.read(id);

            if (task != null) {
                request.setAttribute("task", task);
                request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "Task with ID '" + id + "' not found in To-Do List!");
                request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("message", "Invalid Task ID!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String priorityStr = request.getParameter("priority");

        try {
            int id = Integer.parseInt(idStr);
            Task task = taskRepository.read(id);

            if (task != null) {

                boolean titleExists = taskRepository.all().stream()
                        .anyMatch(t -> t.getTitle().equalsIgnoreCase(title) && t.getId() != id);

                if (titleExists) {
                    request.setAttribute("task", task);
                    request.setAttribute("message", "Task with ID " + id + " already exists!");
                    request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp").forward(request, response);
                    return;
                }

                task.setTitle(title);
                task.setPriority(Priority.valueOf(priorityStr.toUpperCase()));
                taskRepository.update(task);
            }
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/tasks-list");
    }
}