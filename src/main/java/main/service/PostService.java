package main.service;

import main.dto.responses.*;
import main.model.Post;
import main.model.PostComment;
import main.repositories.CommentsRepository;
import main.repositories.PostRepository;
import main.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagsRepository tagsRepository;
    private final CommentsRepository commentsRepository;
    private static final String dateStart = " 00:00:00";
    private static final String dateEnd = " 23:59:59";
    private static final String dateRegex = "\\d.+-\\d{2}-\\d{2}";

    @Autowired
    public PostService(PostRepository postRepository, TagsRepository tagsRepository, CommentsRepository commentsRepository) {
        this.postRepository = postRepository;
        this.tagsRepository = tagsRepository;
        this.commentsRepository = commentsRepository;
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

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        if (date.matches(dateRegex)) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByDate(date + dateStart, date + dateEnd, pageable);
            List<PostResponseForList> postResponseList = new ArrayList<>();

            for (Post p : postsPage) {
                postResponseList.add(new PostResponseForList(p));
            }
            return new PostsResponse(postsPage.getTotalPages(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        if (!tag.equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTag(tag, pageable);
            List<PostResponseForList> postResponseList = new ArrayList<>();

            for (Post p : postsPage) {
                postResponseList.add(new PostResponseForList(p));
            }
            return new PostsResponse(postsPage.getTotalPages(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    //TODO: Переписать когда будет Spring Security
    public ResponseEntity<?> getPostsById(Integer id) {
        Post post = postRepository.findPostById(id);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<PostComment> commentsList = commentsRepository.findComments(id);
        List<String> tagList = tagsRepository.getTagsByPost(id);
        List<PostCommentsResponse> commentsResponseList = new ArrayList<>();
        for (PostComment c : commentsList) {
            commentsResponseList.add(new PostCommentsResponse(c));
        }
        return new ResponseEntity<>(new PostResponseForList(post, commentsResponseList, tagList), HttpStatus.OK);
    }

}