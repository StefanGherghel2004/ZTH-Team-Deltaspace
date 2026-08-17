package com.example.demo.servicetests;

import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.SubredditUpdateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.SubredditNotFoundException;
import com.example.demo.mapper.SubredditMapper;
import com.example.demo.model.Post;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;
import com.example.demo.repository.SubredditRepository;
import com.example.demo.service.ProfanityFilterService;
import com.example.demo.service.SubredditService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubredditServiceTest {

    @Mock
    private SubredditRepository subredditRepository;

    @Mock
    private UserService userService;

    @Mock
    private SubredditMapper subredditMapper;

    @Mock
    private ProfanityFilterService profanityFilterService;

    @InjectMocks
    private SubredditService subredditService;

    private User sampleUser;
    private User otherUser;
    private Subreddit sampleSubreddit;
    private UUID sampleSubredditId;

    @BeforeEach
    void setUp() {
        sampleSubredditId = UUID.randomUUID();

        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID());
        sampleUser.setUsername("testuser");
        sampleUser.setDeleted(false);

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("otheruser");
        otherUser.setDeleted(false);

        sampleSubreddit = new Subreddit();
        sampleSubreddit.setId(sampleSubredditId);
        sampleSubreddit.setName("gaming");
        sampleSubreddit.setDisplayName("Gaming Community");
        sampleSubreddit.setDescription("All about gaming");
        sampleSubreddit.setIconUrl("https://example.com/icon.png");
        sampleSubreddit.setAuthor(sampleUser);
        sampleSubreddit.setMembers(new HashSet<>(Set.of(sampleUser)));
        sampleSubreddit.setPosts(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Should successfully create a subreddit and return its DTO")
    void addSubredditSuccess() {
        SubredditCreateDto dto = new SubredditCreateDto();
        dto.setName("gaming");
        dto.setDisplayName("Gaming Community");
        dto.setDescription("All about gaming");
        dto.setIconUrl("https://example.com/icon.png");

        SubredditResponseDto responseDto = new SubredditResponseDto();

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("All about gaming")).thenReturn("All about gaming");
        when(subredditRepository.save(any(Subreddit.class))).thenReturn(sampleSubreddit);
        when(subredditMapper.toResponseDto(sampleSubreddit)).thenReturn(responseDto);

        SubredditResponseDto result = subredditService.addSubreddit(dto);

        assertThat(result).isNotNull();
        verify(subredditRepository, times(1)).save(any(Subreddit.class));
    }

    @Test
    @DisplayName("Should list all subreddits successfully")
    void listAllSubredditsSuccess() {
        when(subredditRepository.findAll()).thenReturn(List.of(sampleSubreddit));

        List<Subreddit> result = subredditService.listAllSubreddits();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("gaming");
        verify(subredditRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should convert subreddit to response DTO calculating member and post counts correctly")
    void toDtoSuccess() {
        SubredditResponseDto baseDto = new SubredditResponseDto();
        sampleSubreddit.setPosts(new ArrayList<>(List.of(new Post())));

        when(subredditMapper.toResponseDto(sampleSubreddit)).thenReturn(baseDto);

        SubredditResponseDto result = subredditService.toDto(sampleSubreddit);

        assertThat(result).isNotNull();
        assertThat(result.getMemberCount()).isEqualTo(1);
        assertThat(result.getPostCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should successfully delete subreddit when user is the owner and subreddit has no posts")
    void deleteSubredditByNameSuccess() {
        mockAuthentication("testuser");
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        subredditService.deleteSubredditByName("gaming");

        verify(subredditRepository, times(1)).delete(sampleSubreddit);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner attempts to delete subreddit")
    void deleteSubredditByNameNotOwnerThrowsAccessDeniedException() {
        mockAuthentication("otheruser");
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> subredditService.deleteSubredditByName("gaming"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not allowed to perform this operation! You are not the owner!");

        verify(subredditRepository, never()).delete(any(Subreddit.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when deleting subreddit that still has posts")
    void deleteSubredditByNameWithPostsThrowsAccessDeniedException() {
        mockAuthentication("testuser");
        sampleSubreddit.setPosts(new ArrayList<>(List.of(new Post())));

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        assertThatThrownBy(() -> subredditService.deleteSubredditByName("gaming"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Cannot Delete! subreddit still has posts!");

        verify(subredditRepository, never()).delete(any(Subreddit.class));
    }

    @Test
    @DisplayName("Should successfully update subreddit when caller is the owner")
    void updateSubredditSuccess() {
        mockAuthentication("testuser");
        SubredditUpdateDto updateDto = new SubredditUpdateDto();
        updateDto.setTopic("GAMING");
        updateDto.setDescription("Updated description");
        updateDto.setDisplayName("Updated Gaming");
        updateDto.setIconUrl("https://example.com/new-icon.png");

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("Updated description")).thenReturn("Updated description");
        when(profanityFilterService.censor("Updated Gaming")).thenReturn("Updated Gaming");
        when(subredditRepository.save(sampleSubreddit)).thenReturn(sampleSubreddit);

        Subreddit result = subredditService.updateSubreddit("gaming", updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getDisplayName()).isEqualTo("Updated Gaming");
        assertThat(result.getIconUrl()).isEqualTo("https://example.com/new-icon.png");
        verify(subredditRepository, times(1)).save(sampleSubreddit);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner attempts to update subreddit")
    void updateSubredditNotOwnerThrowsAccessDeniedException() {
        mockAuthentication("otheruser");
        SubredditUpdateDto updateDto = new SubredditUpdateDto();
        updateDto.setDescription("Trying to update");

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> subredditService.updateSubreddit("gaming", updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not allowed to perform this operation. You are not the owner");

        verify(subredditRepository, never()).save(any(Subreddit.class));
    }

    @Test
    @DisplayName("Should return subreddit when found by name")
    void findByNameSuccess() {
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));

        Subreddit result = subredditService.findByName("gaming");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("gaming");
    }

    @Test
    @DisplayName("Should throw SubredditNotFoundException when subreddit name does not exist")
    void findByNameNotFoundThrowsSubredditNotFoundException() {
        when(subredditRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subredditService.findByName("unknown"))
                .isInstanceOf(SubredditNotFoundException.class)
                .hasMessageContaining("subreddit not found with name=unknown");
    }

    @Test
    @DisplayName("Should successfully join subreddit when user is not already a member")
    void joinSubredditSuccess() {
        mockAuthentication("otheruser");
        sampleSubreddit.setMembers(new HashSet<>());

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);
        when(subredditRepository.save(sampleSubreddit)).thenReturn(sampleSubreddit);

        subredditService.joinSubreddit("gaming");

        assertThat(sampleSubreddit.getMembers()).contains(otherUser);
        verify(subredditRepository, times(1)).save(sampleSubreddit);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user tries to join subreddit they are already in")
    void joinSubredditAlreadyMemberThrowsAccessDeniedException() {
        mockAuthentication("testuser");
        sampleSubreddit.setMembers(new HashSet<>(Set.of(sampleUser)));

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        assertThatThrownBy(() -> subredditService.joinSubreddit("gaming"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are already a member of this subreddit!");

        verify(subredditRepository, never()).save(any(Subreddit.class));
    }

    @Test
    @DisplayName("Should successfully leave subreddit when user is currently a member")
    void leaveSubredditSuccess() {
        mockAuthentication("testuser");
        sampleSubreddit.setMembers(new HashSet<>(Set.of(sampleUser)));

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(subredditRepository.save(sampleSubreddit)).thenReturn(sampleSubreddit);

        subredditService.leaveSubreddit("gaming");

        assertThat(sampleSubreddit.getMembers()).doesNotContain(sampleUser);
        verify(subredditRepository, times(1)).save(sampleSubreddit);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when user tries to leave subreddit they are not in")
    void leaveSubredditNotMemberThrowsIllegalStateException() {
        mockAuthentication("otheruser");
        sampleSubreddit.setMembers(new HashSet<>());

        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(sampleSubreddit));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> subredditService.leaveSubreddit("gaming"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("You are no longer member of this subreddit");

        verify(subredditRepository, never()).save(any(Subreddit.class));
    }
}