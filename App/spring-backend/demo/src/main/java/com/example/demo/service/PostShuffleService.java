package com.example.demo.service;

import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Post;
import com.example.demo.model.PostVote;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PostVoteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostShuffleService {

    private static final Logger log = LoggerFactory.getLogger(PostShuffleService.class);

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final UserService userService;
    private final PostService postService;
    private final SubredditService subredditService;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    private static final double VOTE_WEIGHT = 1.0;
    private static final double COMMENT_WEIGHT = 2.5;
    private static final double AFFINITY_BOOST = 15.0;
    private static final double TIME_DECAY_GRAVITY = 1.2;
    private static final double JITTER_MIN = 0.85;
    private static final double JITTER_MAX = 1.15;

    @Transactional(readOnly = true)
    public List<Post> getShuffledPosts(String subredditName) {
        List<Post> candidatePosts;
        boolean isGlobalFeed = (subredditName == null || subredditName.isBlank());

        if (!isGlobalFeed) {
            Subreddit subreddit = subredditService.findByName(subredditName);
            candidatePosts = (subreddit != null && subreddit.getPosts() != null)
                    ? new ArrayList<>(subreddit.getPosts())
                    : Collections.emptyList();
        } else {
            candidatePosts = postRepository.findAllByOrderByCreatedAtDesc()
                    .stream()
                    .limit(200)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        if (candidatePosts.isEmpty()) {
            return Collections.emptyList();
        }

        Post pinnedNasaPost = null;

        if (isGlobalFeed) {
            pinnedNasaPost = candidatePosts.stream()
                    .filter(p -> p.getAuthor() != null && "NasaBot".equals(p.getAuthor().getUsername()))
                    .max(Comparator.comparing(Post::getCreatedAt))
                    .orElse(null);

            if (pinnedNasaPost != null) {
                candidatePosts.remove(pinnedNasaPost);
            }
        }

        Set<UUID> preferredSubredditIds = getPreferredSubredditIds();

        try {
            List<Post> result = candidatePosts.stream()
                    .filter(Objects::nonNull)
                    .map(post -> new ScoredPost(post, calculateScore(post, preferredSubredditIds)))
                    .sorted(Comparator.comparingDouble(ScoredPost::score).reversed())
                    .map(ScoredPost::post)
                    .map(postService::filteredPost)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (pinnedNasaPost != null) {
                result.add(0, pinnedNasaPost);
            }

            return result;
        } catch (Exception e) {
            log.error("Error during post scoring/shuffling. Falling back to candidate order.", e);
            List<Post> fallbackList = candidatePosts.stream()
                    .map(postService::filteredPost)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (pinnedNasaPost != null) {
                fallbackList.add(0, pinnedNasaPost);
            }

            return fallbackList;
        }
    }

    private double calculateScore(Post post, Set<UUID> preferredSubredditIds) {
        int upvotes = post.getUpvotes();
        int downvotes = post.getDownvotes();
        int netVotes = upvotes - downvotes;

        int commentCount = 0;
        try {
            if (post.getComments() != null) {
                commentCount = post.getComments().size();
            }
        } catch (Exception ignored) {
            // LazyInitializationException protection
        }

        double engagementScore = (netVotes * VOTE_WEIGHT) + (commentCount * COMMENT_WEIGHT);

        double affinityScore = 0.0;
        if (post.getSubreddit() != null
                && post.getSubreddit().getId() != null
                && preferredSubredditIds.contains(post.getSubreddit().getId())) {
            affinityScore = AFFINITY_BOOST;
        }

        OffsetDateTime createdAt = post.getCreatedAt() != null ? post.getCreatedAt() : OffsetDateTime.now();
        long hoursOld = Math.max(0, Duration.between(createdAt, OffsetDateTime.now()).toHours());

        double timeDecay = Math.pow(hoursOld + 2, TIME_DECAY_GRAVITY);
        double baseScore = (engagementScore + affinityScore) / timeDecay;
        double jitter = ThreadLocalRandom.current().nextDouble(JITTER_MIN, JITTER_MAX);

        return baseScore * jitter;
    }

    private Set<UUID> getPreferredSubredditIds() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Collections.emptySet();
        }

        try {
            User currentUser = userService.getAuthenticatedUser();
            if (currentUser == null) return Collections.emptySet();

            List<PostVote> userVotes = postVoteRepository.findByUser(currentUser);
            if (userVotes == null || userVotes.isEmpty()) return Collections.emptySet();

            return userVotes.stream()
                    .filter(vote -> vote != null && vote.getVoteType() == VoteType.UPVOTE)
                    .map(PostVote::getPost)
                    .filter(Objects::nonNull)
                    .map(Post::getSubreddit)
                    .filter(Objects::nonNull)
                    .map(Subreddit::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("Could not retrieve preferred subreddits: {}", e.getMessage());
            return Collections.emptySet();
        }
    }
    private record ScoredPost(Post post, double score) {}
}