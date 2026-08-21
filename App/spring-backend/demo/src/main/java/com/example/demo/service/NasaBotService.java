package com.example.demo.service;

import com.example.demo.dto.dailycontent.NasaApodResponse;
import com.example.demo.dto.dailycontent.NasaSearchResponse;
import com.example.demo.dto.dailycontent.NasaNeoResponse;
import com.example.demo.logger.Logger;
import com.example.demo.model.Post;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.SubredditRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Service responsible for automatically fetching live NASA data from multiple APIs
 * (APOD, NASA Image Library, and NeoWs) and generating scheduled posts inside the Space subreddit.
 */
@Service
@RequiredArgsConstructor
public class NasaBotService {

    private static final String BOT_USERNAME = "NasaBot";
    private static final String BOT_EMAIL = "nasabot@system.local";

    private static final String SUBREDDIT_NAME = "Space";
    private static final String SUBREDDIT_DISPLAY_NAME = "Space & Astronomy";
    private static final String SUBREDDIT_DESCRIPTION = "Daily Astronomy Pictures, NASA Archive treasures, and Asteroid updates.";
    private static final String SUBREDDIT_ICON_URL = "https://upload.wikimedia.org/wikipedia/commons/9/97/The_Earth_seen_from_Apollo_17.jpg";

    private static final String APOD_API_URL = "https://api.nasa.gov/planetary/apod?api_key=%s&count=5";
    private static final String LIBRARY_SEARCH_API_URL = "https://images-api.nasa.gov/search?q=%s&media_type=image";
    private static final String ASTEROID_API_URL = "https://api.nasa.gov/neo/rest/v1/feed?start_date=%s&end_date=%s&api_key=%s";

    private static final String[] SEARCH_KEYWORDS = {"nebula", "galaxy", "apollo", "black hole", "supernova", "hubble"};

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubredditRepository subredditRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${nasa.api.key}")
    private String nasaApiKey;

    private final RestClient restClient = RestClient.create();
    private final Random random = new Random();

    /**
     * Scheduled task that runs periodically to generate a diverse NASA-sourced post.
     * Randomly picks one of three available content sources (APOD, Image Library, or Asteroid data).
     */
   // @Scheduled(cron = "0 0 12 * * ?")
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void generateNasaPost() {
        try {
            User botUser = getOrCreateNasaBotUser();
            Subreddit spaceSubreddit = getOrCreateSpaceSubreddit();

            int fetchType = random.nextInt(3);
            Logger.info("Content Type: %d", fetchType);

            switch (fetchType) {
                case 0 -> fetchApodAndPost(botUser, spaceSubreddit);
                case 1 -> fetchLibrarySearchAndPost(botUser, spaceSubreddit);
                case 2 -> fetchAsteroidAndPost(botUser, spaceSubreddit);
            }

        } catch (Exception e) {
            Logger.warning("Failed to generate NASA post: %s", e.getMessage());
        }
    }

    /**
     * Fetches Astronomy Picture of the Day (APOD) items from NASA and creates a post for the first valid image.
     * Falls back to the NASA Image Library search if unavailable.
     *
     * @param botUser   the user acting as the author of the post
     * @param subreddit the target subreddit where the post will be saved
     */
    private void fetchApodAndPost(User botUser, Subreddit subreddit) {
        String url = String.format(APOD_API_URL, nasaApiKey);

        try {
            NasaApodResponse[] nasaDataArray = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NasaApodResponse[].class);

            if (nasaDataArray != null) {
                for (NasaApodResponse nasaData : nasaDataArray) {
                    if ("image".equals(nasaData.media_type())) {
                        createAndSavePost(
                                "APOD: " + nasaData.title(),
                                nasaData.explanation(),
                                nasaData.url(),
                                botUser,
                                subreddit
                        );
                        Logger.info("Posted APOD image successfully: %s", nasaData.title());
                        return;
                    }
                }
            }
            fetchLibrarySearchAndPost(botUser, subreddit);
        } catch (Exception e) {
            Logger.warning("Failed to fetch APOD, trying NASA Library Search. Error: %s", e.getMessage());
            fetchLibrarySearchAndPost(botUser, subreddit);
        }
    }

    /**
     * Searches the NASA Image and Video Library using a random space keyword and posts a random result.
     * Falls back to Asteroid data if the search fails.
     *
     * @param botUser   the user acting as the author of the post
     * @param subreddit the target subreddit where the post will be saved
     */
    private void fetchLibrarySearchAndPost(User botUser, Subreddit subreddit) {
        String keyword = SEARCH_KEYWORDS[random.nextInt(SEARCH_KEYWORDS.length)];
        String url = String.format(LIBRARY_SEARCH_API_URL, keyword);

        try {
            NasaSearchResponse searchResponse = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NasaSearchResponse.class);

            if (searchResponse != null && searchResponse.collection() != null && !searchResponse.collection().items().isEmpty()) {
                List<NasaSearchResponse.Item> items = searchResponse.collection().items();
                NasaSearchResponse.Item randomItem = items.get(random.nextInt(items.size()));

                String title = randomItem.data().get(0).title();
                String description = randomItem.data().get(0).description();
                String imageUrl = randomItem.links().get(0).href();

                if (description != null && description.length() > 1000) {
                    description = description.substring(0, 997) + "...";
                }

                createAndSavePost("Archive Spotlight: " + title, description, imageUrl, botUser, subreddit);
                Logger.info("Posted NASA Library image successfully: %s", title);
            } else {
                fetchAsteroidAndPost(botUser, subreddit);
            }
        } catch (Exception e) {
            Logger.warning("Failed to fetch NASA Library Search, trying Asteroids. Error: %s", e.getMessage());
            fetchAsteroidAndPost(botUser, subreddit);
        }
    }

    /**
     * Fetches Near-Earth Objects (NeoWs) data for the current date and posts statistics about a random asteroid.
     * Falls back to APOD if no asteroid data is available.
     *
     * @param botUser   the user acting as the author of the post
     * @param subreddit the target subreddit where the post will be saved
     */
    private void fetchAsteroidAndPost(User botUser, Subreddit subreddit) {
        String today = LocalDate.now().toString();
        String url = String.format(ASTEROID_API_URL, today, today, nasaApiKey);

        try {
            NasaNeoResponse feedResponse = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NasaNeoResponse.class);

            if (feedResponse != null && feedResponse.nearEarthObjects() != null) {
                List<NasaNeoResponse.Asteroid> asteroids = feedResponse.nearEarthObjects().values().stream()
                        .flatMap(List::stream)
                        .toList();

                if (!asteroids.isEmpty()) {
                    NasaNeoResponse.Asteroid asteroid = asteroids.get(random.nextInt(asteroids.size()));

                    double minSize = asteroid.estimatedDiameter().kilometers().min() * 1000;
                    double maxSize = asteroid.estimatedDiameter().kilometers().max() * 1000;

                    String title = "Asteroid Watch: " + asteroid.name() + " passing by today";
                    String content = String.format(
                            "Estimated diameter: Between %.0f meters and %.0f meters. Potentially hazardous: %s.",
                            minSize, maxSize, asteroid.isHazardous() ? "YES ⚠️" : "No 🟢"
                    );

                    createAndSavePost(title, content, null, botUser, subreddit);
                    Logger.info("Posted Asteroid update successfully: %s", asteroid.name());
                    return;
                }
            }
            fetchApodAndPost(botUser, subreddit);

        } catch (Exception e) {
            Logger.warning("Failed to fetch Asteroid data, falling back to APOD. Error: %s", e.getMessage());
            fetchApodAndPost(botUser, subreddit);
        }
    }

    /**
     * Helper method to build, populate, and save a Post entity into the database without
     * using the authenticated user.
     *
     * @param title    the title of the post
     * @param content  the main body content/description of the post
     * @param imageUrl the media link associated with the post (can be null)
     * @param author   the user who authored the post
     * @param subreddit the subreddit where the post belongs
     */
    private void createAndSavePost(String title, String content, String imageUrl, User author, Subreddit subreddit) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setAuthor(author);
        post.setSubreddit(subreddit);
        postRepository.save(post);
    }

    /**
     * Retrieves the dedicated automated NasaBot user, or creates it if it does not yet exist.
     *
     * @return the NasaBot User entity
     */
    private User getOrCreateNasaBotUser() {
        return userRepository.findByUsername(BOT_USERNAME).orElseGet(() -> {
            User newBot = new User();
            newBot.setUsername(BOT_USERNAME);
            newBot.setEmail(BOT_EMAIL);
            newBot.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            return userRepository.save(newBot);
        });
    }

    /**
     * Retrieves the "Space" subreddit, or creates it automatically if it does not yet exist.
     *
     * @return the "Space" Subreddit entity
     */
    private Subreddit getOrCreateSpaceSubreddit() {
        return subredditRepository.findByName(SUBREDDIT_NAME).orElseGet(() -> {
            User bot = getOrCreateNasaBotUser();

            Subreddit newSubreddit = Subreddit.builder()
                    .name(SUBREDDIT_NAME)
                    .description(SUBREDDIT_DESCRIPTION)
                    .displayName(SUBREDDIT_DISPLAY_NAME)
                    .iconUrl(SUBREDDIT_ICON_URL)
                    .author(bot)
                    .member(bot)
                    .build();

            return subredditRepository.save(newSubreddit);
        });
    }
}