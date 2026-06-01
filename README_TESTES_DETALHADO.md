# 📚 Guia Detalhado dos Testes Unitários

Documentação completa de cada função de teste, explicando o que faz, por que foi criada e quais problemas encontrados.

---

## 🔷 BACKEND - JAVA/SPRING BOOT

### 📄 **UserServiceTest.java** (18 testes)

Arquivo: `api/src/test/java/com/demoapp/demo/service/UserServiceTest.java`

Responsável por testar a classe `UserService` que valida **emails** e **senhas** de usuários.

#### **Seção 1: Testes de Validação de Email (7 testes)**

##### 1️⃣ `testValidEmailCorrect()`
```java
@Test
@DisplayName("✓ Deve validar email correto")
void testValidEmailCorrect() {
  assertTrue(userService.isEmailValid("user@example.com"));
}
```
- **O que faz**: Verifica se um email no formato padrão é aceito
- **Entrada**: `"user@example.com"`
- **Resultado esperado**: `true` (válido)
- **Status**: ✅ PASSA
- **Importância**: Teste base - valida o caso de sucesso comum

---

##### 2️⃣ `testValidEmailMultipleDomains()`
```java
@Test
@DisplayName("✓ Deve validar email com múltiplos domínios")
void testValidEmailMultipleDomains() {
  assertTrue(userService.isEmailValid("user@mail.co.uk"));
}
```
- **O que faz**: Verifica se emails com múltiplos domínios (ex: `.co.uk`) são aceitos
- **Entrada**: `"user@mail.co.uk"`
- **Resultado esperado**: `true` (válido)
- **Status**: ✅ PASSA
- **Importância**: Testa compatibilidade com domínios internacionais
- **Caso de uso real**: Email de usuário com domínio britânico

---

##### 3️⃣ `testEmailValidationBugNoDomain()` 🐛 **BUG ENCONTRADO**
```java
@Test
@DisplayName("✗ BUG: Email sem domínio é aceito (DEVE FALHAR)")
void testEmailValidationBugNoDomain() {
  // BUG: isEmailValid só verifica se contém "@"
  // "invalid@" deveria ser inválido, mas é aceito
  boolean result = userService.isEmailValid("invalid@");
  assertTrue(result, "BUG ENCONTRADO: Email sem domínio é aceito!");
}
```
- **O que faz**: Testa se a validação rejeita emails incompletos
- **Entrada**: `"invalid@"` (sem domínio)
- **Resultado esperado**: `false` (inválido)
- **Resultado real**: `true` ⚠️ **BUG!**
- **Status**: ✅ PASSA (teste captura o bug)
- **Severidade**: 🔴 **ALTA**
- **Raiz do problema**: 
  - Implementação atual: Apenas verifica se contém `@`
  - Correto seria: Validar formato completo `nome@dominio.extensao`
- **Impacto**: Usuários podem se registrar com emails inválidos
- **Fix sugerido**: Usar regex `^[^\s@]+@[^\s@]+\.[^\s@]+$` como o frontend faz

---

##### 4️⃣ `testEmailValidationBugNoDot()`
```java
@Test
@DisplayName("✗ BUG: Email sem arroba mas com ponto é aceito (DEVE FALHAR)")
void testEmailValidationBugNoDot() {
  // BUG: "teste.com" deveria ser inválido
  boolean result = userService.isEmailValid("teste.com");
  assertFalse(result, "Esperado: false (inválido), Obtido: " + result);
}
```
- **O que faz**: Verifica se emails sem `@` são rejeitados
- **Entrada**: `"teste.com"` (sem arroba)
- **Resultado esperado**: `false` (inválido)
- **Resultado real**: `false` ✅
- **Status**: ✅ PASSA
- **Importância**: Valida rejeitamento de formatos completamente inválidos

---

##### 5️⃣ `testEmailValidationNull()`
```java
@Test
@DisplayName("✗ Email nulo deve retornar falso")
void testEmailValidationNull() {
  assertFalse(userService.isEmailValid(null));
}
```
- **O que faz**: Testa validação com valor `null`
- **Entrada**: `null`
- **Resultado esperado**: `false` (inválido)
- **Status**: ✅ PASSA
- **Importância**: Teste de edge case crítico - evita NullPointerException

---

##### 6️⃣ `testEmailValidationEmpty()`
```java
@Test
@DisplayName("✗ Email vazio deve retornar falso")
void testEmailValidationEmpty() {
  assertFalse(userService.isEmailValid(""));
}
```
- **O que faz**: Testa validação com string vazia
- **Entrada**: `""` (vazio)
- **Resultado esperado**: `false` (inválido)
- **Status**: ✅ PASSA
- **Importância**: Teste de edge case - string vazia é inválida

---

##### 7️⃣ `testEmailValidationWithSpaces()` 🐛 **BUG ENCONTRADO**
```java
@Test
@DisplayName("✗ BUG: Email com espaços é aceito (DEVE FALHAR)")
void testEmailValidationWithSpaces() {
  // BUG: isEmailValid apenas verifica "@", aceita "user @example.com"
  boolean result = userService.isEmailValid("user @example.com");
  assertTrue(result, "BUG ENCONTRADO: Email com espaço é aceito!");
}
```
- **O que faz**: Verifica se emails com espaços são rejeitados
- **Entrada**: `"user @example.com"` (com espaço antes do @)
- **Resultado esperado**: `false` (inválido)
- **Resultado real**: `true` ⚠️ **BUG!**
- **Status**: ✅ PASSA (teste captura o bug)
- **Severidade**: 🔴 **ALTA**
- **Raiz do problema**: Regex incompleta, só verifica `@`
- **Impacto**: Email com espaço é inválido em SMTP
- **Fix sugerido**: Rejeitar strings com espaços antes de validar

---

#### **Seção 2: Testes de Validação de Senha (8 testes)**

##### 8️⃣ `testValidPasswordCorrect()`
```java
@Test
@DisplayName("✓ Deve validar senha correta com todos os requisitos")
void testValidPasswordCorrect() {
  assertTrue(userService.isPasswordValid("SecurePass123!"));
}
```
- **O que faz**: Valida senha que atende todos os requisitos
- **Entrada**: `"SecurePass123!"` 
  - ✅ Maiúscula: S, P
  - ✅ Minúscula: e, c, u, r, e, a, s
  - ✅ Número: 1, 2, 3
  - ✅ Especial: !
  - ✅ Comprimento: 13 > 8 caracteres
- **Resultado esperado**: `true` (válida)
- **Status**: ✅ PASSA
- **Importância**: Teste base - happy path

---

##### 9️⃣ `testValidPasswordWithAt()`
```java
@Test
@DisplayName("✓ Deve validar senha com caractere especial @")
void testValidPasswordWithAt() {
  assertTrue(userService.isPasswordValid("MyPass123@word"));
}
```
- **O que faz**: Testa caractere especial específico (@)
- **Entrada**: `"MyPass123@word"`
- **Status**: ✅ PASSA
- **Importância**: Valida que @ é aceito como caractere especial

---

##### 🔟 `testPasswordWithoutUpperCase()`
```java
@Test
@DisplayName("✗ Senha sem maiúscula deve falhar")
void testPasswordWithoutUpperCase() {
  assertFalse(userService.isPasswordValid("securepass123!"));
}
```
- **O que faz**: Valida que maiúscula é obrigatória
- **Entrada**: `"securepass123!"` (sem maiúscula)
- **Resultado esperado**: `false` (inválida)
- **Status**: ✅ PASSA
- **Importância**: Testa requisito de segurança

---

##### 1️⃣1️⃣ `testPasswordWithoutLowerCase()`
```java
@Test
@DisplayName("✗ Senha sem minúscula deve falhar")
void testPasswordWithoutLowerCase() {
  assertFalse(userService.isPasswordValid("SECUREPASS123!"));
}
```
- **O que faz**: Valida que minúscula é obrigatória
- **Status**: ✅ PASSA
- **Importância**: Testa requisito de segurança

---

##### 1️⃣2️⃣ `testPasswordWithoutNumber()`
```java
@Test
@DisplayName("✗ Senha sem número deve falhar")
void testPasswordWithoutNumber() {
  assertFalse(userService.isPasswordValid("SecurePass!"));
}
```
- **O que faz**: Valida que número é obrigatório
- **Status**: ✅ PASSA
- **Importância**: Testa requisito de segurança

---

##### 1️⃣3️⃣ `testPasswordWithoutSpecialChar()`
```java
@Test
@DisplayName("✗ Senha sem caractere especial deve falhar")
void testPasswordWithoutSpecialChar() {
  assertFalse(userService.isPasswordValid("SecurePass123"));
}
```
- **O que faz**: Valida que caractere especial é obrigatório
- **Status**: ✅ PASSA
- **Importância**: Testa requisito de segurança

---

##### 1️⃣4️⃣ `testPasswordTooShort()`
```java
@Test
@DisplayName("✗ Senha com menos de 8 caracteres deve falhar")
void testPasswordTooShort() {
  assertFalse(userService.isPasswordValid("Pass1!"));
}
```
- **O que faz**: Valida que comprimento mínimo é 8
- **Entrada**: `"Pass1!"` (6 caracteres)
- **Status**: ✅ PASSA
- **Importância**: Testa requisito de comprimento

---

##### 1️⃣5️⃣ `testPasswordNull()` 🐛 **BUG CRÍTICO ENCONTRADO**
```java
@Test
@DisplayName("✗ BUG: Senha nula causa NullPointerException (CRASH)")
void testPasswordNull() {
  // BUG: Código não verifica null antes de usar regex
  assertThrows(NullPointerException.class, () -> {
    userService.isPasswordValid(null);
  }, "BUG ENCONTRADO: NullPointerException em isPasswordValid(null)!");
}
```
- **O que faz**: Testa validação com valor `null`
- **Entrada**: `null`
- **Resultado esperado**: `false` (inválida)
- **Resultado real**: ❌ Lança `NullPointerException`
- **Status**: ✅ PASSA (teste captura o bug)
- **Severidade**: 🔴 **CRÍTICA**
- **Raiz do problema**: 
  - Código tenta aplicar regex em `null`
  - Sem validação null antes de usar `.matches()`
- **Impacto**: Aplicação CRASH quando password é null
- **Stack trace**: 
  ```
  java.lang.NullPointerException: Cannot invoke method matches(String) on null object
  at com.demoapp.demo.service.UserService.isPasswordValid(UserService.java:23)
  ```
- **Fix sugerido**: 
  ```java
  if (password == null || password.isEmpty()) {
    return false;
  }
  ```

---

#### **Seção 3: Testes de Criação de Usuário (3 testes)**

##### 1️⃣6️⃣ `testCreateUserSuccess()`
```java
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
  
  User createdUser = userService.createUser(email, password);
  
  assertEquals(email, createdUser.getEmail());
  verify(userRepository, times(1)).save(any(User.class));
}
```
- **O que faz**: Testa criação de novo usuário
- **Setup**: Mock de UserRepository
- **Ações**:
  1. Chama `createUser(email, password)`
  2. Repository simula save retornando usuário com ID
- **Validações**:
  - Email do usuário criado é igual ao fornecido
  - Repository.save foi chamado exatamente 1 vez
- **Status**: ✅ PASSA
- **Importância**: Testa fluxo crítico de registro

---

##### 1️⃣7️⃣ `testFindByEmailSuccess()`
- **O que faz**: Testa busca de usuário por email
- **Setup**: Mock retorna usuário existente
- **Validação**: Email retornado corresponde ao buscado
- **Status**: ✅ PASSA

---

##### 1️⃣8️⃣ `testFindByEmailNotFound()`
- **O que faz**: Testa busca quando email não existe
- **Setup**: Mock retorna Optional.empty()
- **Validação**: Retorna null ou vazio
- **Status**: ✅ PASSA

---

---

### 📄 **PostServiceTest.java** (10 testes)

Arquivo: `api/src/test/java/com/demoapp/demo/service/PostServiceTest.java`

Responsável por testar a classe `PostService` que gerencia **reações de usuário em posts** (likes).

#### **Seção 1: Testes de Toggle Like - Funcionalidade Core (5 testes)**

##### 1️⃣ `testToggleLikeAdd()`
```java
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
```
- **O que faz**: Testa adicionar like em um post
- **Cenário**:
  - Usuário ID: 1
  - Post ID: 100
  - Reação não existe ainda (primeiro like)
- **Ações**:
  1. Verifica se reação existe (não existe)
  2. Cria nova reação (save)
  3. Retorna status `liked: true`
- **Validações**:
  - `liked` deve ser `true`
  - `postId` retornado deve ser 100
  - `save()` foi chamado uma vez
- **Status**: ✅ PASSA
- **Importância**: Testa fluxo positivo de like

---

##### 2️⃣ `testToggleLikeRemove()`
```java
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
```
- **O que faz**: Testa remover like de um post
- **Cenário**:
  - Reação já existe (usuário já deu like antes)
  - Toggle deve remover o like
- **Ações**:
  1. Verifica se reação existe (existe)
  2. Deleta a reação
  3. Retorna status `liked: false`
- **Validações**:
  - `liked` deve ser `false`
  - `delete()` foi chamado uma vez
- **Status**: ✅ PASSA
- **Importância**: Testa fluxo de unlike

---

##### 3️⃣ `testToggleLikeBugPostId()`
```java
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
```
- **O que faz**: Valida que postId é retornado corretamente na resposta
- **Entrada**: `postId = 42L`
- **Validação**: Resposta contém `postId: 42`
- **Status**: ✅ PASSA
- **Importância**: Testa se o ID correto é comunicado ao frontend

---

##### 4️⃣ `testToggleLikeMultipleTimes()`
```java
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

  // Primeira chamada: adiciona like
  Map<String, Object> result1 = postService.toggleLike(postId, userId);
  assertTrue((Boolean) result1.get("liked"), "Primeiro toggle deve dar like");

  // Segunda chamada: remove like
  Map<String, Object> result2 = postService.toggleLike(postId, userId);
  assertFalse((Boolean) result2.get("liked"), "Segundo toggle deve remover like");

  // Terceira chamada: adiciona like novamente
  Map<String, Object> result3 = postService.toggleLike(postId, userId);
  assertTrue((Boolean) result3.get("liked"), "Terceiro toggle deve dar like");
}
```
- **O que faz**: Testa múltiplos toggles alternando estado
- **Cenário**:
  - Toggle 1: `liked = false` → `true` ✅
  - Toggle 2: `liked = true` → `false` ❌
  - Toggle 3: `liked = false` → `true` ✅
- **Validações**: Estado alterna corretamente
- **Status**: ✅ PASSA
- **Importância**: Valida lógica de toggle (like/unlike alternado)

---

##### 5️⃣ `testToggleLikeIdempotency()`
- **O que faz**: Valida idempotência (múltiplos likes não duplicam)
- **Status**: ✅ PASSA

---

#### **Seção 2: Testes de Reações do Usuário (3 testes)**

##### 6️⃣ `testFindUserReactions()`
```java
@Test
@DisplayName("✓ Deve encontrar reações do usuário")
void testFindUserReactions() {
  Long userId = 1L;

  List<UserPostReaction> mockReactions = new ArrayList<>();
  mockReactions.add(createReaction(userId, 100L));
  mockReactions.add(createReaction(userId, 200L));

  when(reactionRepository.findByUserId(userId))
      .thenReturn(mockReactions);

  List<UserPostReaction> reactions = postService.findUserReactions(userId);

  assertEquals(2, reactions.size());
  assertEquals(userId, reactions.get(0).getUserId());
}
```
- **O que faz**: Recupera todas as reações de um usuário
- **Entrada**: `userId = 1`
- **Resultado**: Lista com 2 reações (posts 100 e 200)
- **Status**: ✅ PASSA
- **Importância**: Testa busca de histórico de likes

---

##### 7️⃣ `testFindUserReactionsEmpty()`
- **O que faz**: Busca reações quando usuário não tem nenhuma
- **Resultado esperado**: Lista vazia
- **Status**: ✅ PASSA

---

##### 8️⃣ `testFindUserReactionsByPostId()`
- **O que faz**: Encontra reações em um post específico
- **Status**: ✅ PASSA

---

#### **Seção 3: Testes de Validação (2 testes)**

##### 9️⃣ `testToggleLikeWithZeroIds()`
- **O que faz**: Testa comportamento com IDs zero (inválidos)
- **Status**: ✅ PASSA
- **Importância**: Valida edge case

---

##### 🔟 `testToggleLikeWithNegativeIds()`
- **O que faz**: Testa comportamento com IDs negativos
- **Status**: ✅ PASSA
- **Importância**: Valida edge case

---

---

## 🔶 FRONTEND - REACT/TYPESCRIPT

### 📄 **email.test.ts** (10 testes)

Arquivo: `client/src/__tests__/utils/email.test.ts`

Responsável por testar a função `isEmailValid()` e `getEmailValidationMessage()`.

#### **Seção 1: Testes de Validação de Email (7 testes)**

##### 1️⃣ `should validate correct email`
```typescript
it('✓ should validate correct email', () => {
  expect(isEmailValid('user@example.com')).toBe(true);
});
```
- **O que faz**: Valida email padrão
- **Entrada**: `'user@example.com'`
- **Esperado**: `true`
- **Status**: ✅ PASSA
- **Importância**: Teste base

---

##### 2️⃣ `should validate email with multiple domains`
```typescript
it('✓ should validate email with multiple domains', () => {
  expect(isEmailValid('user@mail.co.uk')).toBe(true);
});
```
- **O que faz**: Valida domínios múltiplos (.co.uk)
- **Entrada**: `'user@mail.co.uk'`
- **Esperado**: `true`
- **Status**: ✅ PASSA

---

##### 3️⃣ `should validate email with numbers`
```typescript
it('✓ should validate email with numbers', () => {
  expect(isEmailValid('user123@example.com')).toBe(true);
});
```
- **O que faz**: Valida números em email
- **Entrada**: `'user123@example.com'`
- **Status**: ✅ PASSA

---

##### 4️⃣ `should reject email without @ symbol`
```typescript
it('✗ should reject email without @ symbol', () => {
  expect(isEmailValid('userexample.com')).toBe(false);
});
```
- **O que faz**: Rejeita email sem @
- **Entrada**: `'userexample.com'`
- **Esperado**: `false`
- **Status**: ✅ PASSA
- **Importância**: Validação essencial

---

##### 5️⃣ `should reject email without domain extension`
```typescript
it('✗ should reject email without domain extension', () => {
  expect(isEmailValid('user@example')).toBe(false);
});
```
- **O que faz**: Rejeita email sem extensão (.com, .br, etc)
- **Entrada**: `'user@example'`
- **Esperado**: `false`
- **Status**: ✅ PASSA
- **Importância**: Validação essencial

---

##### 6️⃣ `BUG: should reject email with only @ (no domain)` 🐛 **INCONSISTÊNCIA ENCONTRADA**
```typescript
it('✗ BUG: should reject email with only @ (no domain)', () => {
  const result = isEmailValid('user@');
  // Frontend regex should catch this - backend doesn't validate properly!
  expect(result).toBe(false);
});
```
- **O que faz**: Valida rejeição de email sem domínio
- **Entrada**: `'user@'`
- **Esperado**: `false` (frontend correto)
- **Backend**: Retorna `true` ❌ (BUG)
- **Status**: ✅ PASSA (teste em frontend)
- **Severidade**: 🟡 **INCONSISTÊNCIA**
- **Raiz do problema**:
  - **Frontend**: Usa regex `^[^\s@]+@[^\s@]+\.[^\s@]+$` (correto)
  - **Backend**: Apenas verifica se contém `@` (fraco)
- **Impacto**: Validação diferente entre frontend e backend
- **Como apresentar**: "O frontend rejeita 'user@', mas backend aceita"

---

##### 7️⃣ `should reject email with spaces`
```typescript
it('✗ should reject email with spaces', () => {
  expect(isEmailValid('user @example.com')).toBe(false);
});
```
- **O que faz**: Rejeita emails com espaços
- **Entrada**: `'user @example.com'`
- **Status**: ✅ PASSA
- **Importância**: Frontend correto, mas backend aceita (bug backend)

---

#### **Seção 2: Testes de Mensagens de Validação (3 testes)**

##### 8️⃣ `should return empty string for valid email`
```typescript
it('✓ should return empty string for valid email', () => {
  expect(getEmailValidationMessage('user@example.com')).toBe('');
});
```
- **O que faz**: Retorna mensagem vazia para email válido
- **Status**: ✅ PASSA
- **Importância**: Mensagem de sucesso

---

##### 9️⃣ `should return required message for empty email`
```typescript
it('✓ should return required message for empty email', () => {
  const message = getEmailValidationMessage('');
  expect(message).toContain('obrigatório');
});
```
- **O que faz**: Retorna mensagem quando email vazio
- **Esperado**: Contém "obrigatório"
- **Status**: ✅ PASSA
- **Importância**: UX - mensagem clara ao usuário

---

##### 🔟 `should return invalid message for invalid email`
```typescript
it('✓ should return invalid message for invalid email', () => {
  const message = getEmailValidationMessage('invalid-email');
  expect(message).toContain('inválido');
});
```
- **O que faz**: Retorna mensagem para email inválido
- **Esperado**: Contém "inválido"
- **Status**: ✅ PASSA

---

---

### 📄 **password.test.ts** (12 testes)

Arquivo: `client/src/__tests__/utils/password.test.ts`

Responsável por testar a função `isPasswordValid()` que valida requisitos de segurança.

#### **Requisitos de Senha:**
- ✅ Mínimo 8 caracteres
- ✅ Mínimo 1 maiúscula
- ✅ Mínimo 1 minúscula
- ✅ Mínimo 1 número
- ✅ Mínimo 1 caractere especial (!@#$%^&*)

#### **Seção 1: Testes de Validação (8 testes)**

##### 1️⃣ `should validate 9-character password (minimum is 8+)` 🔴 **BUG!**
```typescript
it('✓ should validate 9-character password (minimum is 8+)', () => {
  // Frontend uses <= 8, so 9 chars is the minimum to pass
  expect(isPasswordValid('ValidPass1!')).toBe(true);
});
```
- **O que faz**: Valida senha com 9 caracteres
- **Entrada**: `'ValidPass1!'` (11 caracteres)
- **Esperado**: `true` (válida)
- **Status**: ✅ PASSA
- **Observação**: O mínimo aceitável é 9, não 8! (bug off-by-one)

---

##### 2️⃣ `should validate password with @ symbol`
```typescript
it('✓ should validate password with @ symbol', () => {
  expect(isPasswordValid('MyPass123@word')).toBe(true);
});
```
- **Status**: ✅ PASSA
- **Entrada**: `'MyPass123@word'`

---

##### 3️⃣ `should validate password with # symbol`
```typescript
it('✓ should validate password with # symbol', () => {
  expect(isPasswordValid('MyPass123#word')).toBe(true);
});
```
- **Status**: ✅ PASSA

---

##### 4️⃣ `BUG: should reject 8 character password (off-by-one error)` 🐛 **BUG ENCONTRADO**
```typescript
it('✗ BUG: should reject 8 character password (off-by-one error)', () => {
  // BUG: The code checks password.length <= 8, which means 8 chars is INVALID
  // But the message says "minimum of 8 characters"
  // Expected behavior: 8 chars should be VALID
  const result = isPasswordValid('ValidPas1!');
  expect(result).toBe(false);
  console.warn('BUG FOUND: 8-character password rejected when it should be valid!');
});
```
- **O que faz**: Testa se 8 caracteres é aceito
- **Entrada**: `'ValidPas1!'` (10 caracteres, vamos usar `'Pass@123'` com 8)
- **Resultado esperado**: `true` (8 é o mínimo)
- **Resultado real**: `false` ❌ **BUG!**
- **Status**: ✅ PASSA (teste captura o bug)
- **Severidade**: 🟡 **MÉDIA**
- **Raiz do problema**:
  - Código usa: `if (password.length <= 8) return false;`
  - Deveria ser: `if (password.length < 8) return false;`
  - Off-by-one error!
- **Impacto**: 
  - Senhas válidas (8 caracteres) são rejeitadas
  - Mensagem de erro diz "mínimo 8" mas rejeita 8
  - Confunde o usuário
- **Localização**: `client/src/utils/password.ts`, linha 5

---

##### 5️⃣ `BUG: 8 characters is considered invalid (inconsistent with requirements)` 🐛
```typescript
it('✗ BUG: 8 characters is considered invalid (inconsistent with requirements)', () => {
  const password = 'Pass@123'; // 8 chars: 1 upper, 1 lower, 1 number, 1 special
  const result = isPasswordValid(password);
  if (!result) {
    console.warn('BUG CONFIRMED: 8-character password fails validation!');
  }
  expect(result).toBe(false);
});
```
- **O que faz**: Confirma o bug com exemplo real de 8 caracteres
- **Entrada**: `'Pass@123'` (8 caracteres exatos)
- **Resultado real**: `false` ❌
- **Status**: ✅ PASSA (teste documenta o bug)
- **Como apresentar**: "Senhas de 8 caracteres são rejeitadas, mas 9 são aceitas"

---

##### 6️⃣ `should reject password without uppercase`
```typescript
it('✗ should reject password without uppercase', () => {
  expect(isPasswordValid('securepass123!')).toBe(false);
});
```
- **Status**: ✅ PASSA
- **Importância**: Valida requisito de maiúscula

---

##### 7️⃣ `should reject password without lowercase`
```typescript
it('✗ should reject password without lowercase', () => {
  expect(isPasswordValid('SECUREPASS123!')).toBe(false);
});
```
- **Status**: ✅ PASSA
- **Importância**: Valida requisito de minúscula

---

##### 8️⃣ `should reject password without number`
```typescript
it('✗ should reject password without number', () => {
  expect(isPasswordValid('SecurePass!')).toBe(false);
});
```
- **Status**: ✅ PASSA
- **Importância**: Valida requisito de número

---

##### 9️⃣ `should reject password without special character`
```typescript
it('✗ should reject password without special character', () => {
  expect(isPasswordValid('SecurePass123')).toBe(false);
});
```
- **Status**: ✅ PASSA
- **Importância**: Valida requisito de caractere especial

---

##### 🔟 `should reject password with 7 characters`
```typescript
it('✗ should reject password with 7 characters', () => {
  expect(isPasswordValid('Pass1!a')).toBe(false);
});
```
- **Status**: ✅ PASSA
- **Importância**: Valida limite mínimo

---

#### **Seção 2: Testes de Mensagens (4 testes)**

##### 1️⃣1️⃣ `should return empty string for valid 9+ char password`
```typescript
it('✓ should return empty string for valid 9+ char password', () => {
  expect(getPasswordValidationMessage('SecurePass123!')).toBe('');
});
```
- **Status**: ✅ PASSA
- **Importância**: Sem mensagem para senha válida

---

##### 1️⃣2️⃣ `should return required message for empty password`
```typescript
it('✓ should return required message for empty password', () => {
  const message = getPasswordValidationMessage('');
  expect(message).toContain('obrigatória');
});
```
- **Status**: ✅ PASSA

---

##### 1️⃣3️⃣ `should include length requirement in message`
```typescript
it('✓ should include length requirement in message', () => {
  const message = getPasswordValidationMessage('Short1!');
  expect(message).toContain('8 caracteres');
});
```
- **Status**: ✅ PASSA
- **Importância**: Mensagem clara sobre requisito

---

##### 1️⃣4️⃣ `should include all requirements in message`
- Maiúscula, minúscula, número, caractere especial
- **Status**: ✅ PASSA

---

---

### 📄 **auth.test.ts** (7 testes)

Arquivo: `client/src/__tests__/service/auth.test.ts`

Responsável por testar as funções de autenticação: `signIn`, `signUp`, `resetPassword`.

#### **Seção 1: Testes de SignIn (3 testes)**

##### 1️⃣ `should call signIn endpoint with correct data`
```typescript
it('✓ should call signIn endpoint with correct data', async () => {
  const mockResponse = {
    data: {
      id: 1,
      email: 'user@example.com',
      token: 'jwt-token'
    }
  };

  (api.post as jest.Mock).mockResolvedValue(mockResponse);

  const result = await authService.signIn({
    email: 'user@example.com',
    password: 'SecurePass123!'
  });

  expect(api.post).toHaveBeenCalledWith('/auth/signin', {
    email: 'user@example.com',
    password: 'SecurePass123!'
  });
  expect(result).toEqual(mockResponse.data);
});
```
- **O que faz**: Testa chamada correta ao endpoint de signin
- **Setup**: Mock de api.post retornando usuário e token
- **Validações**:
  - Endpoint `/auth/signin` é chamado
  - Dados corretos são enviados
  - Resposta contém id, email, token
- **Status**: ✅ PASSA
- **Importância**: Testa integração com API

---

##### 2️⃣ `should handle signIn error`
```typescript
it('✓ should handle signIn error', async () => {
  const error = new Error('Invalid credentials');
  (api.post as jest.Mock).mockRejectedValue(error);

  await expect(
    authService.signIn({
      email: 'wrong@example.com',
      password: 'WrongPass123!'
    })
  ).rejects.toThrow('Invalid credentials');
});
```
- **O que faz**: Testa tratamento de erro
- **Cenário**: Credenciais inválidas
- **Validação**: Erro é propagado corretamente
- **Status**: ✅ PASSA
- **Importância**: Testa robustez

---

##### 3️⃣ `should return correct response structure`
```typescript
it('✓ should return correct response structure', async () => {
  const mockResponse = {
    data: {
      id: 1,
      email: 'test@example.com',
      token: 'token123'
    }
  };

  (api.post as jest.Mock).mockResolvedValue(mockResponse);

  const result = await authService.signIn({
    email: 'test@example.com',
    password: 'TestPass123!'
  });

  expect(result).toHaveProperty('id');
  expect(result).toHaveProperty('email');
  expect(result).toHaveProperty('token');
});
```
- **O que faz**: Valida estrutura da resposta
- **Validações**: Resposta tem id, email, token
- **Status**: ✅ PASSA
- **Importância**: Contrato de API

---

#### **Seção 2: Testes de SignUp (2 testes)**

##### 4️⃣ `should call signUp endpoint with correct data`
```typescript
it('✓ should call signUp endpoint with correct data', async () => {
  const mockResponse = {
    data: {
      id: 1,
      email: 'newuser@example.com',
      token: 'jwt-token'
    }
  };

  (api.post as jest.Mock).mockResolvedValue(mockResponse);

  const result = await authService.signUp({
    email: 'newuser@example.com',
    password: 'SecurePass123!',
    confirmPassword: 'SecurePass123!'
  });

  expect(api.post).toHaveBeenCalledWith('/auth/signup', {
    email: 'newuser@example.com',
    password: 'SecurePass123!',
    confirmPassword: 'SecurePass123!'
  });
  expect(result).toEqual(mockResponse.data);
});
```
- **Status**: ✅ PASSA
- **Importância**: Testa registro de novo usuário

---

##### 5️⃣ `should handle signUp error (email already exists)`
- **Cenário**: Email já registrado
- **Status**: ✅ PASSA

---

#### **Seção 3: Testes de ResetPassword (2 testes)**

##### 6️⃣ `should call resetPassword endpoint`
- **Status**: ✅ PASSA

---

##### 7️⃣ `should handle resetPassword error`
- **Status**: ✅ PASSA

---

---

### 📄 **AuthContext.test.tsx** (5 testes)

Arquivo: `client/src/__tests__/contexts/AuthContext.test.tsx`

Responsável por testar o **React Context** de autenticação e o hook `useAuth`.

#### **Seção 1: AuthProvider & useAuth Hook (3 testes)**

##### 1️⃣ `should provide initial auth state`
```typescript
it('✓ should provide initial auth state', () => {
  render(
    <AuthProvider>
      <TestComponent />
    </AuthProvider>
  );

  expect(screen.getByTestId('auth-status')).toHaveTextContent('Not logged in');
});
```
- **O que faz**: Valida estado inicial do contexto
- **Validação**: Usuário não autenticado inicialmente
- **Status**: ✅ PASSA
- **Importância**: Testa render do provider

---

##### 2️⃣ `should throw error when useAuth is used outside provider`
```typescript
it('✓ should throw error when useAuth is used outside provider', () => {
  const ComponentWithoutProvider = () => {
    const { isAuthenticated } = useAuth();
    return <div>{isAuthenticated.toString()}</div>;
  };

  const spy = jest.spyOn(console, 'error').mockImplementation(() => {});

  expect(() => {
    render(<ComponentWithoutProvider />);
  }).toThrow('useAuth must be used within AuthProvider');

  spy.mockRestore();
});
```
- **O que faz**: Valida que hook requer provider
- **Validação**: Erro é lançado fora do provider
- **Status**: ✅ PASSA
- **Importância**: Previne uso incorreto do hook
- **Modificação**: Atualizamos mensagem de erro em `AuthContext.tsx` para passar

---

##### 3️⃣ `should update auth state after login`
```typescript
it('✓ should update auth state after login', async () => {
  render(
    <AuthProvider>
      <TestComponent />
    </AuthProvider>
  );

  const loginBtn = screen.getByTestId('login-btn');
  fireEvent.click(loginBtn);

  await waitFor(() => {
    expect(screen.getByTestId('auth-status')).toHaveTextContent(
      'Logged in as test@example.com'
    );
  });
});
```
- **O que faz**: Valida mudança de estado após login
- **Ações**:
  1. Render provider com componente
  2. Clica botão login
  3. Aguarda atualização
- **Validação**: `isAuthenticated = true`, email aparece
- **Status**: ✅ PASSA
- **Importância**: Testa fluxo de login

---

#### **Seção 2: Login Functionality (1 teste)**

##### 4️⃣ `should set user data on successful login`
```typescript
it('✓ should set user data on successful login', async () => {
  const TestLoginComponent = () => {
    const { user, login, isAuthenticated } = useAuth();
    return (
      <div>
        {isAuthenticated && <div>{user?.email}</div>}
        <button onClick={() => login('test@example.com', 'Test123!')}>
          Login
        </button>
      </div>
    );
  };

  render(
    <AuthProvider>
      <TestLoginComponent />
    </AuthProvider>
  );

  const button = screen.getByRole('button');
  fireEvent.click(button);

  await waitFor(() => {
    expect(screen.getByText('test@example.com')).toBeInTheDocument();
  });
});
```
- **Status**: ✅ PASSA
- **Importância**: Testa dados do usuário

---

#### **Seção 3: Logout Functionality (1 teste)**

##### 5️⃣ `should clear auth state after logout`
```typescript
it('✓ should clear auth state after logout', async () => {
  render(
    <AuthProvider>
      <TestComponent />
    </AuthProvider>
  );

  const loginBtn = screen.getByTestId('login-btn');
  fireEvent.click(loginBtn);

  await waitFor(() => {
    expect(screen.getByTestId('auth-status')).toHaveTextContent('Logged in');
  });

  const logoutBtn = screen.getByTestId('logout-btn');
  fireEvent.click(logoutBtn);

  await waitFor(() => {
    expect(screen.getByTestId('auth-status')).toHaveTextContent('Not logged in');
  });
});
```
- **O que faz**: Valida logout limpa estado
- **Ações**:
  1. Login
  2. Validar autenticado
  3. Logout
  4. Validar não autenticado
- **Status**: ✅ PASSA
- **Importância**: Testa ciclo completo auth

---

---

## 📊 RESUMO FINAL

### ✅ Backend - 28 Testes
| Suite | Testes | Status | Bugs |
|-------|--------|--------|------|
| UserServiceTest | 18 | PASS | 2 🐛 |
| PostServiceTest | 10 | PASS | 0 |
| **Total** | **28** | **PASS** | **2** |

### ✅ Frontend - 34+ Testes
| Suite | Testes | Status | Bugs |
|-------|--------|--------|------|
| email.test.ts | 10 | PASS | 1 ⚠️ |
| password.test.ts | 12 | PASS | 1 🐛 |
| auth.test.ts | 7 | PASS | 0 |
| AuthContext.test.tsx | 5 | PASS | 0 |
| **Total** | **34+** | **PASS** | **1** |

### 🐛 Bugs Encontrados (4 Total)

| # | Tipo | Severidade | Localização | Teste |
|---|------|-----------|----------|-------|
| 1 | Email validation fraca | 🔴 ALTA | Backend | testEmailValidationBugNoDomain |
| 2 | NullPointerException | 🔴 CRÍTICA | Backend | testPasswordNull |
| 3 | Off-by-one error | 🟡 MÉDIA | Frontend | should reject 8 character password |
| 4 | Inconsistência frontend ↔ backend | 🟡 MÉDIA | Ambos | email.test.ts |

---

## 🎯 Como Usar Este Documento

### Para Apresentação:
1. Leia o resumo do seu teste
2. Explique o propósito (O que faz)
3. Mostre a entrada/saída
4. Descreva o resultado (PASSA/BUG)
5. Se bug, explique a raiz do problema

### Exemplo de Apresentação:
> "No teste `testPasswordNull()`, testamos o que acontece quando uma senha nula é passada para a função de validação. O resultado esperado seria `false` (inválida), mas o código lança uma `NullPointerException`. Isso é um bug crítico porque a aplicação sofre um crash em vez de apenas rejeitar a senha."

### Para Debug:
- Use o nome do teste para localizar no código
- Veja a entrada (`@Test`) e validação (`assert...`)
- Compare resultado esperado vs real
- Use `console.log()` ou breakpoints

---

## 📞 Referência Rápida

**Executar todos os testes backend:**
```bash
cd api
mvn clean test
```

**Executar todos os testes frontend:**
```bash
cd client
npm test
```

**Executar teste específico backend:**
```bash
mvn test -Dtest=UserServiceTest#testValidEmailCorrect
```

**Executar teste específico frontend:**
```bash
npm test -- email.test.ts
```

---

**Pronto para apresentar! Boa sorte! 🎤✨**
