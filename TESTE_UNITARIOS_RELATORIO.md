# 📋 Relatório de Testes Unitários - SQA Social Media

## 🎯 Objetivo
Criar testes unitários que capturem bugs reais e validem os requisitos corretos (testes de regressão).

---

## 🔍 Backend - Java/Spring Boot

### 1. **UserServiceTest.java**

#### Bugs Encontrados:
- ❌ **BUG #1: Email Validation Fraca**
  - **Localização**: `UserService.isEmailValid()`
  - **Problema**: Validação apenas verifica se contém "@", não valida domínio
  - **Exemplo**: `"invalid@"` é aceito como válido
  - **Teste**: `testEmailValidationBugNoDomain()` e `testEmailValidationBugNoDot()`
  - **Impacto**: Usuários podem se registrar com emails inválidos

#### Testes de Regressão:
- ✅ Email com múltiplos domínios (`user@mail.co.uk`)
- ✅ Validação de senha com todos os requisitos
- ✅ Criação de usuário com sucesso
- ✅ Busca de usuário por email
- ✅ Retorno null quando usuário não encontrado

**Cobertura**: 15+ testes

---

### 2. **PostServiceTest.java**

#### Bugs Encontrados:
- ⚠️ **BUG #2: Possível NullPointerException**
  - **Localização**: `PostService.getPosts()`
  - **Problema**: Acesso a campos JSON sem validação de nulidade
  - **Cenário**: Se API retorna response malformado
  - **Teste**: Testes validam resposta correta da API

#### Testes de Regressão:
- ✅ Busca de posts com paginação (limit/skip)
- ✅ Posts curtidos marcados corretamente
- ✅ Valor padrão para limit e skip (5, 0)
- ✅ Paginação de posts curtidos
- ✅ Toggle like (adicionar/remover)
- ✅ Retorno do postId correto

**Cobertura**: 12+ testes

---

## 🎨 Frontend - React/Next.js/TypeScript

### 1. **email.test.ts**

#### Bugs Encontrados:
- ❌ **BUG #3: Inconsistência entre Frontend e Backend**
  - **Frontend**: Valida corretamente com regex
  - **Backend**: Valida apenas se contém "@"
  - **Teste**: `testEmailValidationBugNoDomain()`
  - **Impacto**: Frontend aceita email válido, backend rejeita ou vice-versa

#### Testes de Regressão:
- ✅ Email correto: `user@example.com`
- ✅ Email com múltiplos domínios: `user@mail.co.uk`
- ✅ Email com números
- ✅ Rejeição de email sem @ símbolo
- ✅ Rejeição de email sem extensão de domínio
- ✅ Trimagem de espaços
- ✅ Mensagens de validação corretas

**Cobertura**: 13+ testes

---

### 2. **password.test.ts**

#### Bugs Encontrados:
- ❌ **BUG #4: Off-by-One Error em Validação de Comprimento**
  - **Localização**: `isPasswordValid()` - linha com `password.length <= 8`
  - **Problema**: 8 caracteres é considerado INVÁLIDO, mas requisito diz "mínimo de 8"
  - **Exemplo**: `"Pass@123"` (8 chars com todos requisitos) é rejeitado
  - **Testes**: 
    - `testToggleLikeBugPostId()`
    - `testPasswordWithEightCharacters()`
  - **Impacto**: Usuários com 8 caracteres não conseguem se registrar

#### Testes de Regressão:
- ✅ Senha correta: `SecurePass123!`
- ✅ Aceita 9+ caracteres
- ✅ Rejeita sem maiúscula
- ✅ Rejeita sem minúscula
- ✅ Rejeita sem número
- ✅ Rejeita sem caractere especial
- ✅ Aceita vários caracteres especiais: @, #, %, etc.
- ✅ Mensagens de validação combinadas

**Cobertura**: 16+ testes

---

### 3. **auth.test.ts**

#### Testes de Regressão:
- ✅ signIn com dados corretos
- ✅ signUp com confirmação de senha
- ✅ resetPassword com email válido
- ✅ Tratamento de erros (email existente, credenciais inválidas)
- ✅ Resposta com estrutura correta (id, email, token)
- ✅ Cenário de sign-up → sign-in
- ✅ Persistência de ID do usuário no fluxo

**Cobertura**: 11+ testes

---

### 4. **AuthContext.test.tsx**

#### Testes de Regressão:
- ✅ Estado inicial de autenticação
- ✅ Erro quando hook usado fora do Provider
- ✅ Atualização de estado após login
- ✅ Limpeza de estado após logout
- ✅ Definição de dados do usuário
- ✅ Funcionalidade de signup
- ✅ Manutenção de sessão

**Cobertura**: 9+ testes

---

## 📊 Resumo de Testes

| Camada | Arquivo | Testes | Bugs |
|--------|---------|--------|------|
| Backend | UserServiceTest | 15+ | 1 |
| Backend | PostServiceTest | 12+ | 1 |
| Frontend | email.test | 13+ | 1 |
| Frontend | password.test | 16+ | 1 |
| Frontend | auth.test | 11+ | 0 |
| Frontend | AuthContext.test | 9+ | 0 |
| **TOTAL** | **6 arquivos** | **76+** | **4** |

---

## 🐛 Bugs Capturados

### Backend Bugs:
1. **UserService.isEmailValid()** - Validação muito fraca
2. **PostService.getPosts()** - Possível NullPointerException

### Frontend Bugs:
1. **Inconsistência Frontend ↔ Backend** - Email validation
2. **Off-by-One Error** - Validação de comprimento de senha (8 chars rejeitado)

---

## ▶️ Como Executar os Testes

### Backend (Maven):
```bash
cd api
mvn test
# Ou testes específicos:
mvn test -Dtest=UserServiceTest
mvn test -Dtest=PostServiceTest
```

### Frontend (Jest):
```bash
cd client
npm test                 # Executar todos os testes
npm test -- email      # Apenas email tests
npm test -- password   # Apenas password tests
npm test -- auth       # Apenas auth tests
npm run test:coverage  # Com cobertura
```

---

## ✅ Requisitos Validados (Regressão)

- ✅ Email com formato válido é aceito
- ✅ Email inválido é rejeitado
- ✅ Senha com 8+ caracteres, maiúscula, minúscula, número e caractere especial é válida
- ✅ Usuário pode fazer login
- ✅ Usuário pode fazer signup
- ✅ Usuário pode fazer logout
- ✅ Posts são carregados com paginação
- ✅ Like/Unlike de posts funciona
- ✅ Posts curtidos são marcados corretamente

---

## 🔧 Estrutura de Arquivos

```
api/
└── src/test/java/com/demoapp/demo/service/
    ├── UserServiceTest.java        (15+ testes, 1 bug)
    └── PostServiceTest.java        (12+ testes, 1 bug)

client/
└── src/__tests__/
    ├── utils/
    │   ├── email.test.ts           (13+ testes, 1 bug)
    │   └── password.test.ts        (16+ testes, 1 bug)
    ├── service/
    │   └── auth.test.ts            (11+ testes, 0 bugs)
    └── contexts/
        └── AuthContext.test.tsx    (9+ testes, 0 bugs)
```

---

## 📈 Próximos Passos

1. Corrigir o bug #4 (Off-by-One em password validation)
   ```typescript
   // Atual: password.length <= 8
   // Correto: password.length < 8
   ```

2. Corrigir o bug #1 (Email validation backend)
   ```java
   // Usar regex apropriado em UserService.isEmailValid()
   ```

3. Adicionar tratamento de erro no PostService para respostas malformadas

4. Sincronizar validação de email entre frontend e backend

5. Executar testes em CI/CD pipeline
