# Documentação da Lógica de Construção dos Testes

## Sumário
1. [Introdução](#introdução)
2. [Estrutura Geral dos Testes](#estrutura-geral-dos-testes)
3. [Testes Backend (Java)](#testes-backend-java)
4. [Testes Frontend (Node.js/TypeScript)](#testes-frontend-nodejstypescript)
5. [Estratégia de Logging](#estratégia-de-logging)
6. [Padrões e Convenções](#padrões-e-convenções)
7. [Como Executar os Testes](#como-executar-os-testes)
8. [Interpretação dos Resultados](#interpretação-dos-resultados)

---

## Introdução

Este documento explica a lógica e metodologia utilizada na construção dos testes unitários para o projeto DemoApp, que consiste em uma API Java Spring Boot e um cliente Next.js/TypeScript. 

### Objetivo dos Testes
- **Validar funcionalidades críticas** da aplicação
- **Identificar bugs** conhecidos no código
- **Documentar comportamentos esperados** e reais
- **Fornecer feedback claro** através de logs de sucesso/erro
- **Facilitar manutenção** e evolução do código

---

## Estrutura Geral dos Testes

### Princípios Aplicados

1. **Testes Independentes**: Cada teste pode ser executado isoladamente
2. **Nomenclatura Descritiva**: Nomes de testes explicam claramente o que está sendo testado
3. **Arrange-Act-Assert**: Estrutura padrão de organização de testes
4. **Mocking**: Isolamento de dependências externas
5. **Logging Detalhado**: Rastreamento de execução e resultados

### Categorias de Testes

#### Testes Positivos (✓)
Verificam se o código funciona corretamente em cenários esperados:
- Email válido é aceito
- Senha forte é aceita
- Usuário é criado com sucesso

#### Testes Negativos (✗)
Verificam se o código rejeita corretamente entradas inválidas:
- Email sem @ é rejeitado
- Senha curta é rejeitada
- Usuário duplicado não é criado

#### Testes de BUG (⚠️)
Documentam bugs conhecidos no código:
- Email com apenas "@" é aceito (deveria ser rejeitado)
- Senha com 8 caracteres é rejeitada (deveria ser aceita)
- Senha nula causa NullPointerException

---

## Testes Backend (Java)

### Tecnologias Utilizadas
- **JUnit 5**: Framework de testes
- **Mockito**: Framework de mocking
- **Java Logging**: Sistema de logs nativo

### Estrutura dos Testes Java

```java
@Test
@DisplayName("✓ Descrição clara do que está sendo testado")
void nomeDescritivo() {
    try {
        // Log de início
        logger.info("Iniciando teste: descrição do cenário");
        
        // Arrange: Preparação dos dados
        String email = "test@example.com";
        
        // Act: Execução da ação
        boolean result = userService.isEmailValid(email);
        
        // Assert: Verificação do resultado
        assertTrue(result);
        
        // Log de sucesso
        logger.info("✓ SUCESSO: Comportamento esperado confirmado");
    } catch (Exception e) {
        // Log de erro
        logger.severe("✗ ERRO: Descrição do problema - " + e.getMessage());
        throw e;
    }
}
```

### Classes de Teste Implementadas

#### 1. UserServiceTest
**Arquivo**: `api/src/test/java/com/demoapp/demo/service/UserServiceTest.java`

**Funcionalidades Testadas**:

##### Validação de Email
- ✓ Email correto (user@example.com)
- ✓ Email com múltiplos domínios (user@mail.co.uk)
- ✗ Email nulo deve ser rejeitado
- ✗ Email vazio deve ser rejeitado
- ⚠️ BUG: Email sem domínio (invalid@) é aceito

**Lógica**: A validação de email no backend apenas verifica a presença do caractere "@", o que é insuficiente. Emails como "invalid@" ou "user @example.com" são aceitos incorretamente.

##### Validação de Senha
- ✓ Senha forte (SecurePass123!)
- ✓ Senha com @ ou outros caracteres especiais
- ✗ Senha sem maiúscula deve falhar
- ✗ Senha sem minúscula deve falhar
- ✗ Senha sem número deve falhar
- ✗ Senha sem caractere especial deve falhar
- ⚠️ BUG: Senha nula causa NullPointerException

**Lógica**: A validação de senha usa regex para verificar:
- Mínimo 8 caracteres
- Pelo menos 1 letra maiúscula
- Pelo menos 1 letra minúscula
- Pelo menos 1 número
- Pelo menos 1 caractere especial (!@#$%^&*)

##### Operações de Usuário
- ✓ Criar usuário com sucesso
- ✓ Buscar usuário por email
- ✓ Retornar null quando não encontrado

**Lógica**: Usa Mockito para simular o comportamento do UserRepository sem precisar de um banco de dados real.

#### 2. PostServiceTest
**Arquivo**: `api/src/test/java/com/demoapp/demo/service/PostServiceTest.java`

**Funcionalidades Testadas**:

##### Toggle Like (Adicionar/Remover Like)
- ✓ Adicionar like em post sem reação anterior
- ✓ Remover like de post já curtido
- ✓ PostId retornado corretamente
- ✓ Múltiplos toggles alternam o estado

**Lógica**: 
1. Verifica se já existe reação do usuário para o post
2. Se não existe: cria nova reação (liked = true)
3. Se existe: remove a reação (liked = false)
4. Retorna mapa com estado atual e postId

##### Busca de Reações
- ✓ Encontrar todas as reações de um usuário
- ✓ Retornar lista vazia se não há reações
- ✓ Encontrar reação específica por userId e postId

**Lógica**: Utiliza métodos do repositório mockado para simular buscas no banco de dados.

### Estratégia de Mocking

```java
@Mock
private UserRepository userRepository;

when(userRepository.findByEmail(email))
    .thenReturn(Optional.of(mockUser));
```

**Vantagens**:
- Testes rápidos (sem acesso a BD real)
- Controle total sobre cenários
- Isolamento de dependências
- Testes determinísticos

---

## Testes Frontend (Node.js/TypeScript)

### Tecnologias Utilizadas
- **Jest**: Framework de testes
- **React Testing Library**: Testes de componentes React
- **Console.log/error**: Sistema de logs do JavaScript

### Estrutura dos Testes TypeScript

```typescript
it('✓ descrição clara do teste', async () => {
  console.log('Iniciando teste: descrição do cenário');
  try {
    // Arrange: Preparação
    const mockData = { ... };
    
    // Act: Execução
    const result = await service.method(data);
    
    // Assert: Verificação
    expect(result).toBe(expected);
    
    console.log('✓ SUCESSO: Comportamento esperado confirmado');
  } catch (error) {
    console.error('✗ ERRO: Descrição do problema', error);
    throw error;
  }
});
```

### Arquivos de Teste Implementados

#### 1. auth.test.ts
**Arquivo**: `client/src/__tests__/service/auth.test.ts`

**Funcionalidades Testadas**:

##### Serviço de Login (signIn)
- ✓ Chamar endpoint com dados corretos
- ✓ Tratar erro de credenciais inválidas
- ✓ Retornar estrutura de resposta correta (id, email, token)

**Lógica**: 
```typescript
const result = await authService.signIn({
  email: 'user@example.com',
  password: 'SecurePass123!'
});

// Verifica se chamou o endpoint correto
expect(api.post).toHaveBeenCalledWith('/auth/signin', { ... });

// Verifica estrutura da resposta
expect(result).toHaveProperty('id');
expect(result).toHaveProperty('token');
```

##### Serviço de Cadastro (signUp)
- ✓ Chamar endpoint com dados corretos
- ✓ Tratar erro de email duplicado
- ✓ Retornar estrutura correta

##### Recuperação de Senha (resetPassword)
- ✓ Chamar endpoint de recuperação
- ✓ Tratar erro de email não encontrado
- ✓ Tratar erros de rede

##### Cenários de Integração
- ✓ Fluxo completo de cadastro e login

**Lógica**: Testa sequência de operações para simular comportamento real do usuário.

#### 2. email.test.ts
**Arquivo**: `client/src/__tests__/utils/email.test.ts`

**Funcionalidades Testadas**:

##### Validação de Email (isEmailValid)
- ✓ Validar email correto
- ✓ Validar email com múltiplos domínios
- ✓ Validar email com números
- ✗ Rejeitar email sem @
- ✗ Rejeitar email sem extensão de domínio
- ✗ Rejeitar email com espaços
- ⚠️ BUG: Inconsistência frontend/backend

**Lógica**: O frontend usa regex robusto:
```typescript
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
```
Valida:
- Caracteres antes do @
- Caracteres depois do @
- Ponto e extensão de domínio
- Sem espaços

**Nota Importante**: O frontend valida corretamente, mas o backend aceita emails inválidos (apenas verifica @).

##### Mensagens de Validação (getEmailValidationMessage)
- ✓ String vazia para email válido
- ✓ Mensagem "obrigatório" para email vazio
- ✓ Mensagem "inválido" para email incorreto

#### 3. password.test.ts
**Arquivo**: `client/src/__tests__/utils/password.test.ts`

**Funcionalidades Testadas**:

##### Validação de Senha (isPasswordValid)
- ✓ Validar senha com 9+ caracteres
- ✓ Validar senha com @ ou #
- ✗ Rejeitar senha sem maiúscula
- ✗ Rejeitar senha sem minúscula
- ✗ Rejeitar senha sem número
- ✗ Rejeitar senha sem caractere especial
- ⚠️ BUG: Senha com 8 caracteres é rejeitada (erro off-by-one)

**Lógica do BUG**:
```typescript
// CÓDIGO ATUAL (ERRADO)
if (password.length <= 8) return false;  // 8 é INVÁLIDO

// DEVERIA SER
if (password.length < 8) return false;   // 8 seria VÁLIDO
```

A mensagem diz "mínimo de 8 caracteres", mas o código rejeita senhas com exatamente 8 caracteres.

##### Mensagens de Validação
- ✓ Incluir requisito de comprimento
- ✓ Incluir requisito de maiúscula
- ✓ Incluir requisito de minúscula
- ✓ Incluir requisito de número
- ✓ Incluir requisito de caractere especial
- ✓ Mostrar múltiplos requisitos faltando

#### 4. AuthContext.test.tsx
**Arquivo**: `client/src/__tests__/contexts/AuthContext.test.tsx`

**Funcionalidades Testadas**:

##### Contexto e Provider
- ✓ Fornecer estado inicial
- ✓ Lançar erro se usado fora do provider
- ✓ Atualizar estado após login
- ✓ Limpar estado após logout

**Lógica**: Testa o React Context que gerencia o estado de autenticação global da aplicação.

##### Funcionalidades de Login/Cadastro
- ✓ Definir dados do usuário no login
- ✓ Lidar com email e senha
- ✓ Definir dados no cadastro

##### Gerenciamento de Sessão
- ✓ Manter sessão após login

**Lógica**: Usa React Testing Library para simular interações do usuário:
```typescript
fireEvent.click(loginBtn);
await waitFor(() => {
  expect(screen.getByTestId('auth-status'))
    .toHaveTextContent('Logado como user@email.com');
});
```

---

## Estratégia de Logging

### Java (Backend)

```java
private static final Logger logger = Logger.getLogger(UserServiceTest.class.getName());

// Início do teste
logger.info("Iniciando teste: descrição");

// Sucesso
logger.info("✓ SUCESSO: Operação concluída");

// Aviso (para bugs conhecidos)
logger.warning("✓ BUG CONFIRMADO: Descrição do bug");

// Erro
logger.severe("✗ ERRO: Descrição - " + e.getMessage());
```

### TypeScript/JavaScript (Frontend)

```typescript
// Início do teste
console.log('Iniciando teste: descrição');

// Sucesso
console.log('✓ SUCESSO: Operação concluída');

// Aviso (para bugs)
console.warn('✓ BUG CONFIRMADO: Descrição do bug');

// Erro
console.error('✗ ERRO: Descrição', error);
```

### Benefícios do Logging

1. **Rastreamento**: Saber exatamente qual teste está executando
2. **Diagnóstico**: Identificar rapidamente onde ocorreu falha
3. **Documentação**: Logs servem como documentação viva
4. **Depuração**: Facilita encontrar problemas
5. **Transparência**: Professor pode ver todo o processo de execução

---

## Padrões e Convenções

### Nomenclatura de Testes

#### Java
```java
@Test
@DisplayName("✓ Deve validar email correto")
void testValidEmailCorrect() { ... }

@Test
@DisplayName("✗ BUG: Email sem domínio é aceito (DEVE FALHAR)")
void testEmailValidationBugNoDomain() { ... }
```

#### TypeScript
```typescript
it('✓ deve validar email correto', () => { ... });

it('✗ BUG: deve rejeitar email apenas com @', () => { ... });
```

### Símbolos Usados

- `✓` - Teste positivo (comportamento esperado)
- `✗` - Teste negativo (validação de erro)
- `⚠️` - Bug conhecido documentado

### Organização de Describes/Suites

```typescript
describe('Validação de Email - Utilitários', () => {
  describe('isEmailValid', () => {
    // Testes de validação
  });
  
  describe('getEmailValidationMessage', () => {
    // Testes de mensagens
  });
});
```

---

## Como Executar os Testes

### Backend (Java/Maven)

```bash
# Navegar para pasta da API
cd api

# Executar todos os testes
mvn test

# Executar teste específico
mvn test -Dtest=UserServiceTest

# Executar com logs detalhados
mvn test -X
```

### Frontend (Node.js/Jest)

```bash
# Navegar para pasta do cliente
cd client

# Instalar dependências (se necessário)
npm install

# Executar todos os testes
npm test

# Executar em modo watch
npm test -- --watch

# Executar arquivo específico
npm test -- email.test.ts

# Executar com cobertura
npm test -- --coverage
```

### Execução Individual

#### Java
No VS Code ou IntelliJ, clique no botão "Run Test" ao lado de cada método @Test.

#### TypeScript
```bash
# Executar arquivo específico
npm test -- src/__tests__/utils/email.test.ts
```

---

## Interpretação dos Resultados

### Saída Esperada - Teste com Sucesso

**Java**:
```
INFO: Iniciando teste: validação de email correto
INFO: ✓ SUCESSO: Email válido foi aceito corretamente
[OK] testValidEmailCorrect (12ms)
```

**TypeScript**:
```
console.log
  Iniciando teste: validação de email correto

console.log
  ✓ SUCESSO: Email válido foi aceito corretamente

✓ deve validar email correto (15ms)
```

### Saída Esperada - Teste de Bug

**Java**:
```
WARNING: Iniciando teste: verificação de BUG - email sem domínio
WARNING: ✓ BUG CONFIRMADO: Email sem domínio foi aceito incorretamente
[OK] testEmailValidationBugNoDomain (8ms)
```

**TypeScript**:
```
console.warn
  ✓ BUG CONFIRMADO: Senha com 8 caracteres rejeitada quando deveria ser válida!

✓ BUG: deve rejeitar senha com 8 caracteres (10ms)
```

### Saída Esperada - Falha de Teste

**Java**:
```
SEVERE: ✗ ERRO: Falha na validação de email correto - AssertionError
[FAIL] testValidEmailCorrect (5ms)
  Expected: true
  Actual: false
```

**TypeScript**:
```
console.error
  ✗ ERRO: Falha na validação de email correto

✕ deve validar email correto (20ms)

  Expected: true
  Received: false
```

### Análise de Cobertura

**Backend (Maven)**:
```bash
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

**Frontend (Jest)**:
```bash
npm test -- --coverage
# Relatório em: coverage/lcov-report/index.html
```

### Métricas de Sucesso

- **Taxa de Sucesso**: % de testes passando
- **Cobertura de Código**: % de linhas testadas
- **Bugs Documentados**: Número de bugs conhecidos identificados
- **Tempo de Execução**: Performance dos testes

---

## Conclusão

### Metodologia Aplicada

1. **Análise do Código**: Estudo das funcionalidades existentes
2. **Identificação de Cenários**: Casos de teste positivos e negativos
3. **Implementação de Mocks**: Isolamento de dependências
4. **Adição de Logs**: Rastreamento detalhado de execução
5. **Documentação de Bugs**: Registro de problemas conhecidos
6. **Tradução para Português**: Facilitar compreensão

### Aprendizados

1. **Importância de Validação Robusta**: Os bugs encontrados mostram que validações simples (apenas verificar "@") são insuficientes
2. **Testes como Documentação**: Testes bem escritos documentam o comportamento esperado
3. **Logging Detalhado**: Facilita depuração e compreensão
4. **Consistência Frontend/Backend**: Necessidade de validações consistentes em ambos os lados

### Próximos Passos

1. **Corrigir Bugs Identificados**:
   - Melhorar validação de email no backend
   - Corrigir erro off-by-one na validação de senha
   - Adicionar validação de null para senha

2. **Expandir Cobertura**:
   - Adicionar testes para controllers
   - Testar cenários de erro HTTP
   - Adicionar testes de integração

3. **Melhorar Qualidade**:
   - Implementar testes E2E
   - Adicionar testes de performance
   - Configurar CI/CD com execução automática

---

## Referências

### Documentação
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://site.mockito.org/)
- [Jest](https://jestjs.io/docs/getting-started)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

### Padrões
- [Arrange-Act-Assert Pattern](https://automationpanda.com/2020/07/07/arrange-act-assert-a-pattern-for-writing-good-tests/)
- [Test Driven Development (TDD)](https://martinfowler.com/bliki/TestDrivenDevelopment.html)

---

**Documento criado por**: Equipe de Desenvolvimento  
**Data**: Junho de 2026  
**Versão**: 1.0  
**Objetivo**: Documentar lógica de testes para apresentação acadêmica
