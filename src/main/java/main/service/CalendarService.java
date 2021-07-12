package main.service;

import main.dto.responses.CalendarResponseList;
import main.model.Post;
import main.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CalendarService {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private final static SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private final Calendar calendar = Calendar.getInstance();

    private final PostRepository postRepository;

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //TODO: Сделать вывод только за конкретный год, Если параметр year пустой..?
    public CalendarResponseList getPublications(String year) {
        List<Post> postsList = postRepository.findAllPosts();
        Set<Integer> years = new HashSet<>();
        Map<String, Integer> posts = new HashMap<>();

        try {
            for (int i = 0; i < postsList.size(); i++) {
                Date parsedDate = dateFormat.parse(postsList.get(i).getTime().toString());
                Date parsedDate2 = yearMonthDayFormat.parse(postsList.get(i).getTime().toString());
                years.add(Integer.parseInt(yearFormat.format(parsedDate)));

                calendar.setTimeInMillis(parsedDate2.getTime());
                String date = yearMonthDayFormat.format(calendar.getTime());

                if (posts.containsKey(date)) {
                    posts.put(date, posts.get(date) + 1);
                } else {
                    posts.put(date, 1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CalendarResponseList(years, posts);
    }
}