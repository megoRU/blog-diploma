package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.responses.TagsResponse;
import main.dto.responses.TagsResponseImpl;
import main.dto.responses.TagsResponseList;
import main.repositories.PostRepository;
import main.repositories.TagsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagsRepository tagsRepository;
    private final PostRepository postRepository;

    public TagsResponseList getTags(String name) {
        List<TagsResponse> tagsResponseLists = new ArrayList<>();
        List<TagsResponseImpl> listTags;
        long postCount = postRepository.findAllPosts().size();

        if (name != null) {
            listTags = tagsRepository.getTagByName(name);
        } else {
            listTags = tagsRepository.getRecentTags();
        }
        if (!listTags.isEmpty()) {
            double normParam = (double) listTags.get(0).getCount() / postCount;

            for (TagsResponseImpl t : listTags) {
                tagsResponseLists.add(new TagsResponse(t.getName(), String.valueOf((double) t.getCount() / postCount / normParam)));
            }
            return new TagsResponseList(tagsResponseLists);
        }
        return new TagsResponseList(tagsResponseLists);

    }
}