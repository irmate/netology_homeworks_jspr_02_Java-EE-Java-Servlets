package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private final String[] VALID_PATHS = {
            "/api/posts",
            "/api/posts/\\d+"
    };
    private PostController controller;

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    private long extractId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/")).replace("/", ""));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals("GET") && path.equals(VALID_PATHS[0])) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches(VALID_PATHS[1])) {
                // easy way
                controller.getById(extractId(path), resp);
                return;
            }
            if (method.equals("POST") && path.equals(VALID_PATHS[0])) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(VALID_PATHS[1])) {
                // easy way
                controller.removeById(extractId(path), resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}