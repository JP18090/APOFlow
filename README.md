# APOFlow

Sistema web para gerenciar o fluxo de Atividades Programadas Obrigatorias do PPG-CA, cobrindo submissao, avaliacao em multiplas etapas, arquivamento de evidencias, notificacoes e apoio ao lancamento de creditos no sistema academico.

## Visao Geral

O APOFlow foi pensado para reduzir o trabalho manual hoje distribuido entre aluno, orientador, comissao, coordenacao e secretaria. O projeto foi organizado em duas camadas principais, frontend e backend, empacotadas em uma unica imagem Docker para simplificar a execucao.

Fluxo coberto no prototipo:

1. Aluno submete APO com descricao, pontos e anexos.
2. Orientador avalia e pode aprovar ou devolver com justificativa.
3. Aluno pode editar e reenviar APO devolvida, ou desistir.
4. Comissao registra votos e consolida parecer.
5. Coordenacao toma a decisao final.
6. Secretaria arquiva e realiza o lancamento quando o aluno atinge 12 pontos.

## Funcionalidades Principais

- autenticacao por e-mail e senha
- dashboard especifico para cada ator do fluxo
- formulario de submissao de APO com opcao de salvar rascunho
- visualizacao de rascunhos e APOs enviadas pelo aluno
- pontos por atividade na tela de APOs do aluno
- notificacoes in-app simuladas
- troca de perfil para professor entre orientador, comissao e coordenacao

## Stack

- Frontend: React 18, TypeScript, Vite, Tailwind CSS, React Router, TanStack Query, Framer Motion, Sonner
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA, H2
- Containerizacao: Docker multi-stage e docker-compose

## Arquitetura do Projeto

### Organizacao por camadas

- `Frontend/`: interface React responsável por login, dashboards, formulários e navegação por perfil.
- `Backend/`: API REST Spring Boot responsável pelas regras de negócio, persistência em memória e carga inicial dos dados.
- `Dockerfile`: build multi-stage que compila o frontend, empacota o backend e publica uma imagem final única.
- `docker-compose.yml`: orquestra a execução da aplicação via Docker.

### Estrutura principal

```text
.
├── Frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/      # layout, sidebar, login, componentes de UI
│   │   ├── contexts/        # autenticação e estado global simples
│   │   ├── lib/             # cliente HTTP, tipos e utilitários
│   │   └── pages/           # dashboards e telas por fluxo de negócio
│   └── package.json
├── Backend/
│   ├── src/main/java/com/apoflow/backend/
│   │   ├── api/             # controllers, handlers e DTOs
│   │   ├── config/          # carga inicial e configuração
│   │   ├── domain/          # entidades e enums do domínio APO
│   │   ├── repository/      # acesso aos dados com Spring Data JPA
│   │   └── service/         # regras de negócio do workflow
│   └── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

### Arquitetura de execução

1. O estágio `frontend-build` do Dockerfile instala dependências do Vite e gera o build estático do React.
2. O estágio `backend-build` compila o Spring Boot e copia o conteúdo gerado do frontend para `src/main/resources/static`.
3. A imagem final sobe apenas o `jar` do backend, que passa a servir tanto a API quanto os arquivos estáticos do frontend.
4. O banco H2 roda em memória dentro da própria aplicação, com carga inicial feita no startup.

### Fluxo de runtime

- Navegador acessa `http://localhost:8080`
- Spring Boot entrega a interface React já buildada
- O frontend chama rotas REST em `/api/...`
- O backend processa autenticação, APOs, notificações e workflow dos perfis
- Os dados vivem em H2 em memória para fins de demonstração

## Como Executar com Docker

Requisitos:

- Docker
- Docker Compose

### Subir a aplicação

```bash
docker compose up --build
```

Esse comando:

- constrói o frontend React
- empacota o backend Spring Boot
- gera a imagem final da aplicação
- publica a aplicação em `http://localhost:8080`

### Executar em segundo plano

```bash
docker compose up --build -d
```

### Parar a aplicação

```bash
docker compose down
```

### Reconstruir após alterações

```bash
docker compose up --build
```

### Ver logs da aplicação

```bash
docker compose logs -f
```

### Remover containers, rede e artefatos do compose

```bash
docker compose down --volumes --remove-orphans
```

## Como Funciona o Dockerfile

O arquivo [Dockerfile](/workspaces/APOFlow/Dockerfile) usa três estágios:

- `frontend-build`: gera os arquivos estáticos do React
- `backend-build`: compila o backend com Maven e incorpora o frontend buildado
- `runtime`: sobe uma imagem enxuta com Java 21 JRE e o `jar` final

Isso evita instalar Node e Maven na imagem final e reduz o tamanho do artefato de produção.

## Publicação e Portas

- Aplicação web: `http://localhost:8080`
- API REST: `http://localhost:8080/api`
- Console H2: não está exposto separadamente no Docker para uso externo no README, pois o foco é a aplicação integrada

## Operação de Desenvolvimento com Docker

Fluxo recomendado:

```bash
docker compose up --build
```

Após subir:

- abra `http://localhost:8080`
- faça login com um dos perfis disponíveis
- teste os fluxos de submissão, devolução, comissão, coordenação e secretaria

Quando alterar código e quiser refletir a mudança na imagem final, rode novamente:

```bash
docker compose up --build
```

## Acesso ao Prototipo

Login por e-mail e senha:

- `aluno@mackenzie.com` / `JosePedro`
- `orientador@mackenzie.com` / `GustavoNeto`
- `comissao@mackenzie.com` / `GabrielLabarca`
- `coordenacao@mackenzie.com` / `VitorCosta`
- `secretaria@mackenzie.com` / `LuizBatista`

Obs.: o perfil de professor pode alternar entre orientador, comissao e coordenacao no menu lateral.

## Casos de Uso Cobertos

- aluno salva rascunho
- aluno envia APO
- orientador aprova ou devolve
- aluno edita ou desiste após devolução
- comissão registra votos
- coordenação toma decisão final
- secretaria arquiva e lança quando o total atinge 12 pontos

## Comunicacao da API

Principais rotas implementadas:

- `POST /api/auth/login`
- `GET /api/students`
- `GET /api/apos`
- `POST /api/apos`
- `POST /api/apos/rascunho`
- `PUT /api/apos/{id}/aluno/reenviar`
- `POST /api/apos/{id}/aluno/desistir`
- `POST /api/apos/{id}/orientador/aprovar`
- `POST /api/apos/{id}/orientador/devolver`
- `POST /api/apos/{id}/comissao/voto`
- `POST /api/apos/{id}/coordenacao/decisao?action=aprovar|reprovar|devolver`
- `POST /api/apos/{id}/secretaria/arquivar`
- `POST /api/apos/{id}/secretaria/lancar`
- `GET /api/notifications?recipient=aluno|orientador|comissao|coordenacao|secretaria`

## Documento de Casos de Uso

- [Frontend/docs/use-cases.md](Frontend/docs/use-cases.md)
