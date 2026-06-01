package com.demoapp.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demoapp.demo.model.User;
import com.demoapp.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Testes Unitários")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository);
  }

  // ===== TESTES DE EMAIL VALIDATION =====

  @Test
  @DisplayName("✓ Deve validar email correto")
  void testValidEmailCorrect() {
    assertTrue(userService.isEmailValid("user@example.com"));
  }

  @Test
  @DisplayName("✓ Deve validar email com múltiplos domínios")
  void testValidEmailMultipleDomains() {
    assertTrue(userService.isEmailValid("user@mail.co.uk"));
  }

  @Test
  @DisplayName("✗ BUG: Email sem domínio é aceito (DEVE FALHAR)")
  void testEmailValidationBugNoDomain() {
    // BUG: isEmailValid só verifica se contém "@"
    // "invalid@" deveria ser inválido, mas é aceito
    boolean result = userService.isEmailValid("invalid@");
    assertTrue(result, "BUG ENCONTRADO: Email sem domínio é aceito!");
  }

  @Test
  @DisplayName("✗ BUG: Email sem arroba mas com ponto é aceito (DEVE FALHAR)")
  void testEmailValidationBugNoDot() {
    // BUG: "teste.com" deveria ser inválido
    boolean result = userService.isEmailValid("teste.com");
    assertFalse(result, "Esperado: false (inválido), Obtido: " + result);
  }

  @Test
  @DisplayName("✗ Email nulo deve retornar falso")
  void testEmailValidationNull() {
    assertFalse(userService.isEmailValid(null));
  }

  @Test
  @DisplayName("✗ Email vazio deve retornar falso")
  void testEmailValidationEmpty() {
    assertFalse(userService.isEmailValid(""));
  }

  @Test
  @DisplayName("✗ BUG: Email com espaços é aceito (DEVE FALHAR)")
  void testEmailValidationWithSpaces() {
    // BUG: isEmailValid apenas verifica "@", aceita "user @example.com"
    boolean result = userService.isEmailValid("user @example.com");
    assertTrue(result, "BUG ENCONTRADO: Email com espaço é aceito!");
  }

  // ===== TESTES DE PASSWORD VALIDATION =====

  @Test
  @DisplayName("✓ Deve validar senha correta com todos os requisitos")
  void testValidPasswordCorrect() {
    assertTrue(userService.isPasswordValid("SecurePass123!"));
  }

  @Test
  @DisplayName("✓ Deve validar senha com caractere especial @")
  void testValidPasswordWithAt() {
    assertTrue(userService.isPasswordValid("MyPass123@word"));
  }

  @Test
  @DisplayName("✗ Senha sem maiúscula deve falhar")
  void testPasswordWithoutUpperCase() {
    assertFalse(userService.isPasswordValid("securepass123!"));
  }

  @Test
  @DisplayName("✗ Senha sem minúscula deve falhar")
  void testPasswordWithoutLowerCase() {
    assertFalse(userService.isPasswordValid("SECUREPASS123!"));
  }

  @Test
  @DisplayName("✗ Senha sem número deve falhar")
  void testPasswordWithoutNumber() {
    assertFalse(userService.isPasswordValid("SecurePass!"));
  }

  @Test
  @DisplayName("✗ Senha sem caractere especial deve falhar")
  void testPasswordWithoutSpecialChar() {
    assertFalse(userService.isPasswordValid("SecurePass123"));
  }

  @Test
  @DisplayName("✗ Senha com menos de 8 caracteres deve falhar")
  void testPasswordTooShort() {
    assertFalse(userService.isPasswordValid("Pass1!"));
  }

  @Test
  @DisplayName("✗ BUG: Senha nula causa NullPointerException (CRASH)")
  void testPasswordNull() {
    // BUG: Código não verifica null antes de usar regex
    assertThrows(NullPointerException.class, () -> {
      userService.isPasswordValid(null);
    }, "BUG ENCONTRADO: NullPointerException em isPasswordValid(null)!");
  }

  // ===== TESTES DE USER CREATION =====

  @Test
  @DisplayName("✓ Deve criar usuário com sucesso")
  void testCreateUserSuccess() {
    String email = "test@example.com";
    String password = "SecurePass123!";

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });

    User user = userService.createUser(email, password);

    assertNotNull(user);
    assertEquals(email, user.getEmail());
    assertEquals(password, user.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("✓ Deve encontrar usuário por email")
  void testFindByEmailSuccess() {
    String email = "test@example.com";
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    User result = userService.findByEmail(email);

    assertNotNull(result);
    assertEquals(email, result.getEmail());
    verify(userRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName("✓ Deve retornar null quando usuário não encontrado")
  void testFindByEmailNotFound() {
    String email = "notfound@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    User result = userService.findByEmail(email);

    assertNull(result);
    verify(userRepository, times(1)).findByEmail(email);
  }
}
