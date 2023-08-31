package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.Getter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.MapDBContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableScheduling
@Service
public class PollsContainerService {

    private final DBContext db;
    @Getter
    private final List<String> movieMessagesDbList;
    @Getter
    private final List<String> pollsDbList;
    private final Map<Integer, Set<Integer>> movieMessages;
    @Getter
    private final Map<Integer, Map<Long, Integer>> polls;

    public PollsContainerService() {
        db = MapDBContext.onlineInstance("JetTopMovieBot");
        movieMessagesDbList = db.getList("movieMessagesDbList");
        pollsDbList = db.getList("pollsDbList");
        this.movieMessages = parseMovieMessages(movieMessagesDbList);
        this.polls = parsePolls(pollsDbList);
    }

    public void add(Integer movieId, Long userId, int pollRate, Integer messageId) {
        polls.computeIfAbsent(movieId, k -> new HashMap<>()).put(userId, pollRate);
        movieMessages.computeIfAbsent(movieId, k -> new HashSet<>()).add(messageId);
    }

    public Map<Long, Integer> get(Integer movieId) {
        return polls.get(movieId);
    }

    public Set<Integer> getLinkedMessages(Integer movieId) {
        Set<Integer> messages = movieMessages.get(movieId);
        if (messages == null) {
            return Collections.emptySet();
        }
        return messages;
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 60_000)
    public synchronized void saveDb() {
        movieMessagesDbList.clear();
        pollsDbList.clear();
        for (Map.Entry<Integer, Set<Integer>> messageEntry : movieMessages.entrySet()) {
            Integer movieId = messageEntry.getKey();
            movieMessagesDbList.addAll(messageEntry.getValue().stream().map(v -> movieId + ";" + v).toList());
        }
        for (Map.Entry<Integer, Map<Long, Integer>> entry : polls.entrySet()) {
            Integer movieId = entry.getKey();
            pollsDbList.addAll(entry.getValue().entrySet().stream().map(e -> movieId + ";" + e.getKey() + ";" + e.getValue()).toList());
        }
        db.commit();
    }

    private Map<Integer, Map<Long, Integer>> parsePolls(List<String> polls) {
        Map<Integer, Map<Long, Integer>> result = new HashMap<>();
        for (String poll : polls) {
            String[] pollData = poll.split(";");
            result.computeIfAbsent(Integer.parseInt(pollData[0]), k -> new HashMap<>())
                    .put(Long.parseLong(pollData[1]), Integer.parseInt(pollData[2]));
        }
        return result;
    }

    private static Map<Integer, Set<Integer>> parseMovieMessages(List<String> movieMessages) {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        for (String message : movieMessages) {
            String[] messageData = message.split(";");
            result.computeIfAbsent(Integer.parseInt(messageData[0]), k -> new HashSet<>())
                    .add(Integer.parseInt(messageData[1]));
        }
        return result;
    }

}
