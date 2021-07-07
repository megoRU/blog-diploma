package main.service;

import main.dto.responses.TagsResponse;
import main.dto.responses.TagsResponseImpl;
import main.dto.responses.TagsResponseList;
import main.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    private final TagsRepository tagsRepository;

    @Autowired
    public TagService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public TagsResponseList getTags(String name) {
        List<TagsResponse> tagsResponseLists = new ArrayList<>();
        List<TagsResponseImpl> listTags;
        if (name != null) {
            listTags = tagsRepository.getTagByName(name);
        } else {
            listTags = tagsRepository.getRecentTags();
        }
        double normParam = (double) listTags.get(0).getCount() / listTags.size();

        for (TagsResponseImpl t : listTags) {
            tagsResponseLists.add(new TagsResponse(t.getName(), String.valueOf((double) t.getCount() / listTags.size() / normParam)));
        }
        return new TagsResponseList(tagsResponseLists);
    }
}