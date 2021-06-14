package main.service;

import main.api.responses.PostResponseForList;
import main.api.responses.PostsResponse;
import main.model.Posts;
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

    @Autowired
    private PostRepository postRepository;

    public PostsResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Posts> postsPage;

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

        return createPostsResponse(postsPage, postRepository.findAllPosts().size());
    }

    private PostsResponse createPostsResponse(Page<Posts> pageOfTags, int size){
        List<PostResponseForList> postResponseList = new ArrayList<>();
        for (Posts p : pageOfTags) {
            postResponseList.add(new PostResponseForList(p));
        }

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setPosts(postResponseList);
        postsResponse.setCount(size);

        return postsResponse;
    }

}
