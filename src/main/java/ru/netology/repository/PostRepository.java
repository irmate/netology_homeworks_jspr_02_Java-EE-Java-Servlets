package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PostRepository {
    private final long INITIAL_COUNT = 1;
    private final AtomicLong count;
    private final Map<Long, Post> repository;

    public PostRepository() {
        count = new AtomicLong(INITIAL_COUNT);
        repository = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        return new ArrayList<>(repository.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(repository.get(id));
    }

    public Post save(Post post) {
        long postId = post.getId();
        if (postId == 0) {
            post.setId(count.getAndIncrement());
            repository.put(post.getId(), post);
            return post;
        }
        if (repository.containsKey(postId)) {
            repository.put(postId, post);
            return post;
        } else {
            return null;
        }
    }

    public void removeById(long id) {
        if (repository.remove(id) == null) {
            throw new NotFoundException();
        }
    }
}