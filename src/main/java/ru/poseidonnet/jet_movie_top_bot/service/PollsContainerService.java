package ru.poseidonnet.jet_movie_top_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableScheduling
@Service
public class PollsContainerService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final File backupFile;
    @Getter
    private Backup backup;

    @SneakyThrows
    public PollsContainerService(@Value("${backup.path}") String backupPath) {
        backupFile = new File(backupPath);
        if (!backupFile.exists()) {
            backupFile.getParentFile().mkdirs();
            backup = new Backup();
        } else {
            backup = MAPPER.readValue(backupFile, Backup.class);
        }
    }

    public void add(Integer movieId, Long userId, int pollRate, Integer messageId) {
        Map<Long, Integer> pollMap = backup.getPolls().computeIfAbsent(movieId, k -> new HashMap<>());
        Integer currentRate = pollMap.get(userId);
        if (currentRate != null && currentRate == pollRate) {
            pollMap.remove(userId);
        } else {
            pollMap.put(userId, pollRate);
        }
        backup.getMovieMessages().computeIfAbsent(movieId, k -> new HashSet<>()).add(messageId);
    }

    public Map<Long, Integer> getMoviePolls(Integer movieId) {
        return backup.getPolls().get(movieId);
    }

    public Set<Integer> getLinkedMessages(Integer movieId) {
        Set<Integer> messages = backup.getMovieMessages().get(movieId);
        if (messages == null) {
            return Collections.emptySet();
        }
        return messages;
    }

    @SneakyThrows
    public void loadBackup(String backup) {
        this.backup = MAPPER.readValue(backup, Backup.class);
    }

    @SneakyThrows
    public String getBackup() {
        return MAPPER.writeValueAsString(backup);
    }


    public Map<Integer, Map<Long, Integer>> getPolls() {
        return backup.getPolls();
    }

    public void addWillView(long userId, int movieId, Integer messageId) {
        Set<Integer> movies = backup.getWillView().computeIfAbsent(userId, k -> new HashSet<>());
        if (movies.contains(movieId)) {
            movies.remove(movieId);
        } else {
            movies.add(movieId);
        }
        backup.getMovieMessages().computeIfAbsent(movieId, k -> new HashSet<>()).add(messageId);
    }

    public Set<Integer> getWillView(long userId) {
        return backup.getWillView().containsKey(userId) ? backup.getWillView().get(userId) : new HashSet<>();
    }

    public int countWillView(int movieId) {
        int count = 0;
        for (Set<Integer> movies : backup.getWillView().values()) {
            if (movies.contains(movieId)) {
                count++;
            }
        }
        return count;
    }

    @SneakyThrows
    @Scheduled(fixedRate = 60_000, initialDelay = 60_000)
    public synchronized void saveBackup() {
        Files.write(backupFile.toPath(), List.of(MAPPER.writeValueAsString(backup)));
    }

    @RequiredArgsConstructor
    @Data
    private static class Backup {

        private Map<Integer, Set<Integer>> movieMessages = new HashMap<>();
        private Map<Integer, Map<Long, Integer>> polls = new HashMap<>();
        //user - movie
        private Map<Long, Set<Integer>> willView = new HashMap<>();

    }

}
