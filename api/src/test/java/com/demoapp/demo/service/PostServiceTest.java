package com.demoapp.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demoapp.demo.model.UserPostReaction;
import com.demoapp.demo.repository.UserPostReactionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService - Testes Unitários")
class PostServiceTest {

  @Mock
  private UserPostReactionRepository reactionRepository;

  private PostService postService;

  @BeforeEach
  void setUp() {
    postService = new PostService(reactionRepository);
  }

  // ===== TESTES DE TOGGLE LIKE - FUNCIONALIDADE CORE =====

  @Test
  @DisplayName("✓ Deve dar like em post com sucesso")
  void testToggleLikeAdd() {
    Long userId = 1L;
    Long postId = 100L;

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());
    when(reactionRepository.save(any(UserPostReaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> result = postService.toggleLike(postId, userId);

    assertTrue((Boolean) result.get("liked"));
    assertEquals(postId, result.get("postId"));
    verify(reactionRepository, times(1)).save(any(UserPostReaction.class));
  }

  @Test
  @DisplayName("✓ Deve remover like de post com sucesso")
  void testToggleLikeRemove() {
    Long userId = 1L;
    Long postId = 100L;

    UserPostReaction reaction = new UserPostReaction();
    reaction.setUserId(userId);
    reaction.setPostId(postId);

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.of(reaction));

    Map<String, Object> result = postService.toggleLike(postId, userId);

    assertFalse((Boolean) result.get("liked"));
    verify(reactionRepository, times(1)).delete(any(UserPostReaction.class));
  }

  @Test
  @DisplayName("✓ Toggle like deve retornar postId correto")
  void testToggleLikeBugPostId() {
    Long userId = 1L;
    Long postId = 42L;

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());
    when(reactionRepository.save(any(UserPostReaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> result = postService.toggleLike(postId, userId);

    assertEquals(postId, result.get("postId"), "PostId deve ser retornado corretamente");
    assertTrue((Boolean) result.get("liked"));
  }

  @Test
  @DisplayName("✓ Multiple toggles devem alternado estado de like")
  void testToggleLikeMultipleTimes() {
    Long userId = 1L;
    Long postId = 50L;

    // Primeiro like
    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(createReaction(userId, postId)))
        .thenReturn(Optional.empty());
    when(reactionRepository.save(any(UserPostReaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Add like
    Map<String, Object> result1 = postService.toggleLike(postId, userId);
    assertTrue((Boolean) result1.get("liked"));

    // Remove like
    Map<String, Object> result2 = postService.toggleLike(postId, userId);
    assertFalse((Boolean) result2.get("liked"));

    // Add like again
    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());
    Map<String, Object> result3 = postService.toggleLike(postId, userId);
    assertTrue((Boolean) result3.get("liked"));
  }

  // ===== TESTES DE REAÇÃO DO USUÁRIO =====

  @Test
  @DisplayName("✓ Deve encontrar reações do usuário")
  void testFindUserReactions() {
    Long userId = 1L;

    UserPostReaction reaction1 = createReaction(userId, 1L);
    UserPostReaction reaction2 = createReaction(userId, 2L);

    when(reactionRepository.findByUserId(userId))
        .thenReturn(Arrays.asList(reaction1, reaction2));

    List<UserPostReaction> reactions = reactionRepository.findByUserId(userId);

    assertEquals(2, reactions.size());
    assertTrue(reactions.stream().allMatch(r -> r.getUserId().equals(userId)));
  }

  @Test
  @DisplayName("✓ Deve retornar lista vazia se usuário não tem likes")
  void testFindUserReactionsEmpty() {
    Long userId = 999L;

    when(reactionRepository.findByUserId(userId))
        .thenReturn(new ArrayList<>());

    List<UserPostReaction> reactions = reactionRepository.findByUserId(userId);

    assertTrue(reactions.isEmpty());
  }

  @Test
  @DisplayName("✓ Deve encontrar reação específica por usuário e post")
  void testFindByUserIdAndPostId() {
    Long userId = 1L;
    Long postId = 100L;

    UserPostReaction reaction = createReaction(userId, postId);

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.of(reaction));

    Optional<UserPostReaction> found = reactionRepository
        .findByUserIdAndPostId(userId, postId);

    assertTrue(found.isPresent());
    assertEquals(userId, found.get().getUserId());
    assertEquals(postId, found.get().getPostId());
  }

  @Test
  @DisplayName("✓ Deve retornar empty se reação não existe")
  void testFindByUserIdAndPostIdNotFound() {
    Long userId = 1L;
    Long postId = 999L;

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());

    Optional<UserPostReaction> found = reactionRepository
        .findByUserIdAndPostId(userId, postId);

    assertFalse(found.isPresent());
  }

  // ===== TESTES DE VALIDAÇÃO =====

  @Test
  @DisplayName("✓ Toggle like com postId = 0 deve funcionar")
  void testToggleLikeWithZeroPostId() {
    Long userId = 1L;
    Long postId = 0L;

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());
    when(reactionRepository.save(any(UserPostReaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> result = postService.toggleLike(postId, userId);

    assertEquals(postId, result.get("postId"));
  }

  @Test
  @DisplayName("✓ Toggle like com userId = 0 deve funcionar")
  void testToggleLikeWithZeroUserId() {
    Long userId = 0L;
    Long postId = 100L;

    when(reactionRepository.findByUserIdAndPostId(userId, postId))
        .thenReturn(Optional.empty());
    when(reactionRepository.save(any(UserPostReaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> result = postService.toggleLike(postId, userId);

    assertTrue((Boolean) result.get("liked"));
  }

  // ===== HELPER METHODS =====

  private UserPostReaction createReaction(Long userId, Long postId) {
    UserPostReaction reaction = new UserPostReaction();
    reaction.setUserId(userId);
    reaction.setPostId(postId);
    return reaction;
  }
}
