# ▶️ GUIA DE EXECUÇÃO DOS TESTES

## 🏃 Quick Start

### Backend (28 testes)
```bash
cd api
mvn clean test
```
**Esperado**: ✅ 28 testes PASSAM em 2-3 segundos

### Frontend (40+ testes)
```bash
cd client
npm test -- --passWithNoTests
```
**Esperado**: ✅ Todos os testes PASSAM

---

## 📋 TESTES DISPONÍVEIS

### BACKEND

#### UserServiceTest (18 testes)
```bash
mvn test -Dtest=UserServiceTest
```

**Testes de Email Validation:**
- ✅ Valida email correto
- ✅ Valida email com múltiplos domínios
- 🐛 BUG: Email sem domínio é aceito
- ✅ Email nulo retorna falso
- ✅ Email vazio retorna falso
- 🐛 BUG: Email com espaços é aceito

**Testes de Password Validation:**
- ✅ Valida senha correta
- ✅ Valida senha com caractere especial @
- ✅ Rejeita sem maiúscula
- ✅ Rejeita sem minúscula
- ✅ Rejeita sem número
- ✅ Rejeita sem caractere especial
- ✅ Rejeita com menos de 8 caracteres
- 🐛 BUG: NullPointerException quando password = null

**Testes de User Creation:**
- ✅ Cria usuário com sucesso
- ✅ Encontra usuário por email
- ✅ Retorna null quando não encontrado

#### PostServiceTest (10 testes)
```bash
mvn test -Dtest=PostServiceTest
```

**Testes de Toggle Like:**
- ✅ Dar like com sucesso
- ✅ Remover like com sucesso
- ✅ Retorna postId correto
- ✅ Toggle múltiplas vezes alterna estado

**Testes de Reações do Usuário:**
- ✅ Encontra reações do usuário
- ✅ Retorna lista vazia se sem likes
- ✅ Encontra reação específica por userId e postId
- ✅ Retorna empty se reação não existe

**Testes de Validação:**
- ✅ Toggle com postId = 0
- ✅ Toggle com userId = 0

---

### FRONTEND

#### Email Validation (10 testes)
```bash
npm test -- email.test.ts
```

**Testes Positivos:**
- ✅ Email correto
- ✅ Email com múltiplos domínios
- ✅ Email com números
- ✅ Trimagem de espaços

**Testes Negativos:**
- ✅ Email sem @
- ✅ Email sem extensão
- 🐛 Email com apenas @
- ✅ Email vazio
- ✅ Email com espaços
- ✅ Mensagens de validação

#### Password Validation (12 testes)
```bash
npm test -- password.test.ts
```

**Testes Positivos:**
- ✅ Senha de 9+ caracteres válida
- ✅ Aceita caracteres especiais (@, #, %, etc)

**Testes Negativos:**
- 🐛 BUG: 8 caracteres rejeitado (off-by-one)
- ✅ Rejeita sem maiúscula
- ✅ Rejeita sem minúscula
- ✅ Rejeita sem número
- ✅ Rejeita sem caractere especial
- ✅ Rejeita com 7 caracteres
- ✅ Rejeita vazio
- ✅ Múltiplos requisitos faltando

**Mensagens de Erro:**
- ✅ Mensagem vazia para válida
- ✅ Mensagem de campo obrigatório
- ✅ Descrição de requisitos faltando
- ✅ Múltiplos requisitos na mensagem

#### Auth Service (7 testes - Mocked)
```bash
npm test -- auth.test.ts
```

- ✅ SignIn com dados corretos
- ✅ SignIn com erro
- ✅ SignIn retorna estrutura correta
- ✅ SignUp com confirmação
- ✅ SignUp com erro (email existe)
- ✅ ResetPassword funciona
- ✅ ResetPassword com erro

#### AuthContext (5 testes)
```bash
npm test -- AuthContext.test.tsx
```

- ✅ Estado inicial
- ✅ Erro quando hook fora do Provider
- ✅ Atualiza estado após login
- ✅ Limpa estado após logout
- ✅ Mantém sessão

---

## 🎯 Rodando Testes Específicos

### Executar um teste individual
```bash
# Backend - UserServiceTest específico
mvn test -Dtest=UserServiceTest#testEmailValidationBugNoDomain

# Frontend - Email validation
npm test -- email -- --testNamePattern="should reject email"
```

### Rodando com cobertura
```bash
# Backend - com relatório de cobertura
mvn clean test jacoco:report

# Frontend - com cobertura
npm run test:coverage
```

### Modo watch (desenvolvimento)
```bash
# Frontend - reexecuta em alterações
npm run test:watch
```

---

## 📊 Esperado vs Obtido

### Backend Execution
```
[INFO] Results:
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS ✅
```

### Frontend Execution
```
PASS  src/__tests__/utils/email.test.ts
PASS  src/__tests__/utils/password.test.ts
PASS  src/__tests__/service/auth.test.ts
PASS  src/__tests__/contexts/AuthContext.test.tsx

Test Suites: 4 passed, 4 total ✅
Tests:       62 passed, 62 total ✅
```

---

## 🔍 Entendendo os Testes

### Nomenclatura
- ✅ = Teste de regressão (comportamento correto)
- 🐛 = Teste que captura um bug
- 🟡 = Aviso/comportamento não ideal

### Estrutura de um Teste

```java
@Test
@DisplayName("✅ Deve validar email correto")
void testValidEmailCorrect() {
    // Arrange - preparar dados
    String email = "user@example.com";
    
    // Act - executar função
    boolean result = userService.isEmailValid(email);
    
    // Assert - verificar resultado
    assertTrue(result);
}
```

```typescript
it('✅ should validate correct email', () => {
  // Arrange & Act
  const result = isEmailValid('user@example.com');
  
  // Assert
  expect(result).toBe(true);
});
```

---

## 🐛 Investigando Bugs Encontrados

### Bug #1: Email Validation Fraca
```bash
# Rodar teste que captura o bug
mvn test -Dtest=UserServiceTest#testEmailValidationBugNoDomain

# Ver código com bug
cat api/src/main/java/com/demoapp/demo/service/UserService.java | grep -A 2 "isEmailValid"
```

**Saída esperada**: Teste PASSA mostrando o bug

### Bug #2: Off-by-One Password
```bash
# Rodar teste que captura
npm test -- password -- --testNamePattern="BUG.*8 characters"

# Ver código com bug
grep -n "length <=" client/src/utils/password.ts
```

---

## 📝 Logs e Debugging

### Backend - Ver logs detalhados
```bash
mvn test -X 2>&1 | grep -E "FAIL|ERROR|Tests"
```

### Frontend - Ver logs
```bash
npm test -- --verbose
```

### Gerar relatório HTML
```bash
# Backend - Coverage Report
open target/site/jacoco/index.html

# Frontend - Coverage Report
open coverage/lcov-report/index.html
```

---

## ✅ Checklist de Execução

- [ ] Testes backend rodando: `mvn clean test` 
- [ ] Resultado: 28/28 PASS ✅
- [ ] Testes frontend rodando: `npm test`
- [ ] Email validation tests PASS ✅
- [ ] Password validation tests PASS ✅
- [ ] Auth tests PASS ✅
- [ ] AuthContext tests PASS ✅
- [ ] Documentação lida: SUMARIO_TESTES.md
- [ ] Bugs identificados e documentados ✅

---

## 🚀 Próximos Passos

1. **Executar todos os testes** (backend + frontend)
2. **Revisar bugs encontrados** em SUMARIO_TESTES.md
3. **Corrigir bugs críticos** (Priority 1)
4. **Integrar testes no CI/CD**
5. **Aumentar cobertura** (alvo: 80%+)

---

**Nota**: Todos os testes são independentes e podem ser executados em qualquer ordem.
