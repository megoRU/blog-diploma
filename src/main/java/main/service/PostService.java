package main.service;

import main.dto.responses.PostResponseForList;
import main.dto.responses.PostsResponse;
import main.model.Post;
import main.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostsResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Post> postsPage;

        switch (mode) {
            case "popular":
                postsPage = postRepository.findAllPostsByCommentsDesc(pageable);
                break;
            case "best":
                postsPage = postRepository.findAllPostsByVotesDesc(pageable);
                break;
            case "early":
                postsPage = postRepository.findAllPostsByTime(pageable);
                break;
            default:
                postsPage = postRepository.findAllPostsByTimeDesc(pageable);
                break;
        }

        return getPosts(postsPage, postRepository.findAllPosts().size());
    }

    private PostsResponse getPosts(Page<Post> postsPages, int size) {
        List<PostResponseForList> postResponseList = new ArrayList<>();
        for (Post p : postsPages) {
            postResponseList.add(new PostResponseForList(p));
        }

        return new PostsResponse(size, postResponseList);
    }

    public PostsResponse getPostsSearch(int offset, int limit, String query) {
        if (query.trim().equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTimeDesc(pageable);
            return getPosts(postsPage, postRepository.findAllPosts().size());
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postsPage = postRepository.findAllPostsByName(query, pageable);
        List<PostResponseForList> postResponseList = new ArrayList<>();

        for (Post p : postsPage) {
            postResponseList.add(new PostResponseForList(p));
        }

        return new PostsResponse(postsPage.getTotalPages(), postResponseList);
    }

}
