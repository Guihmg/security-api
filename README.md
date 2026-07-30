# Security API

API REST de autenticação desenvolvida com Java e Spring Boot.

O projeto permite:

- cadastrar usuários;
- autenticar usuários;
- gerar tokens JWT;
- proteger rotas com Bearer Token;
- consultar o usuário autenticado;
- registrar histórico de autenticação;
- gerar logs diários de erros;
- persistir dados no PostgreSQL.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Security
- OAuth2 Resource Server
- JWT
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Maven
- JUnit 5
- Mockito

## Arquitetura

O projeto está dividido em camadas:

```text
controller  -> recebe as requisições HTTP
dto         -> representa entradas e respostas da API
service     -> contém as regras de negócio
repository  -> realiza o acesso ao banco de dados
domain      -> contém as entidades do sistema
security    -> configura autenticação e JWT
exception   -> trata erros da aplicação
```

## Banco de dados

O sistema utiliza somente duas tabelas:

```text
users
auth_history
```

A tabela `users` armazena os usuários cadastrados.

A tabela `auth_history` armazena os eventos:

```text
USER_REGISTERED
LOGIN_SUCCESS
LOGIN_FAILURE
```

Senhas são armazenadas utilizando hash BCrypt.

Tokens e senhas nunca são gravados no histórico ou nos arquivos de log.

## Pré-requisitos

Antes de executar o projeto, instale:

- Java 21
- Docker Desktop
- Git

Confira o Java:

```bash
java -version
```

## Clonar o projeto

```bash
git clone https://github.com/Guihmg/security-api.git
cd security-api
```

## Configuração do JWT

Para execução local, existe um segredo padrão de desenvolvimento.

Para definir outro segredo:

```bash
export JWT_SECRET="uma-chave-secreta-com-pelo-menos-32-caracteres"
```

Em produção, o segredo deve obrigatoriamente ser fornecido por variável de ambiente.

## Executar o banco de dados

```bash
docker compose up -d
```

Confira o container:

```bash
docker compose ps
```

## Executar a aplicação

No Linux ou WSL:

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

Esta aplicação é uma API REST e não possui interface gráfica no navegador.

## Executar os testes

No Linux ou WSL:

```bash
./mvnw test
```

No Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Endpoints

| Método | Endpoint | Autenticação | Descrição |
|---|---|---|---|
| POST | `/api/users` | Pública | Cadastra um usuário |
| POST | `/api/auth/login` | Pública | Autentica e gera um JWT |
| GET | `/api/auth/me` | Bearer Token | Retorna o usuário autenticado |
| GET | `/api/auth/history` | Bearer Token | Retorna o histórico do usuário |

## Cadastrar usuário

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Guilherme Gomes",
    "email": "guilhermeservh@gmail.com",
    "password": "12345678"
  }'
```

Resposta esperada:

```http
HTTP/1.1 201
```

```json
{
  "name": "Guilherme Gomes",
  "email": "guilhermeservh@gmail.com"
}
```

## Fazer login

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "guilhermeservh@gmail.com",
    "password": "12345678"
  }'
```

Resposta:

```json
{
  "token": "eyJ...",
  "tokenType": "Bearer",
  "name": "Guilherme Gomes",
  "email": "guilhermeservh@gmail.com"
}
```

O token possui validade de uma hora.

## Guardar o token no terminal

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "guilhermeservh@gmail.com",
    "password": "12345678"
  }' | python3 -c 'import sys,json; print(json.load(sys.stdin)["token"])')
```

## Consultar o usuário autenticado

```bash
curl -s http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

Resposta:

```json
{
  "name": "Guilherme Gomes",
  "email": "guilhermeservh@gmail.com"
}
```

## Consultar o histórico

```bash
curl -s http://localhost:8080/api/auth/history \
  -H "Authorization: Bearer $TOKEN"
```

Resposta:

```json
[
  {
    "eventType": "LOGIN_SUCCESS",
    "occurredAt": "2026-07-30T21:09:42.440731Z"
  },
  {
    "eventType": "LOGIN_FAILURE",
    "occurredAt": "2026-07-30T21:00:40.276682Z"
  }
]
```

## Testar uma rota sem token

```bash
curl -i http://localhost:8080/api/auth/history
```

Resposta esperada:

```http
HTTP/1.1 401
```

## Logs

Os avisos e erros são gravados em arquivos diários:

```text
logs/security-api-errors-AAAA-MM-DD.log
```

Exemplo:

```bash
tail -n 20 logs/security-api-errors-$(date +%F).log
```

Os arquivos são mantidos durante 30 dias.

A pasta `logs/` não é enviada para o GitHub.

## Encerrar a aplicação

No terminal em que a aplicação está rodando:

```text
Ctrl+C
```

Para encerrar o PostgreSQL:

```bash
docker compose down
```

Os dados permanecem armazenados no volume Docker.

Para também remover os dados:

```bash
docker compose down -v
```

> O comando com `-v` apaga os usuários e históricos salvos.
