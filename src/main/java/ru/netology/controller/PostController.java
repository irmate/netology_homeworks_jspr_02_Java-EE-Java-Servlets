package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.function.Supplier;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    public void returnResponse(Supplier<?> dataCreator, HttpServletResponse response) {
        try {
            response.setContentType(APPLICATION_JSON);
            var data = dataCreator.get();
            var gson = new Gson();
            response.getWriter().print(gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void all(HttpServletResponse response) {
        returnResponse(service::all, response);
    }

    public void getById(long id, HttpServletResponse response) {
        try {
            returnResponse(() -> service.getById(id), response);
        } catch (NotFoundException notFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        final var post = gson.fromJson(body, Post.class);
        final var data = service.save(post);
        if (data == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.getWriter().print(gson.toJson(data));
    }

    public void removeById(long id, HttpServletResponse response) {
        try {
            service.removeById(id);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}