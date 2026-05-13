# APOFlow

Sistema web para gerenciar o fluxo de Atividades Programadas Obrigatorias do PPG-CA, cobrindo submissao, avaliacao em multiplas etapas, arquivamento de evidencias, notificacoes e apoio ao lancamento de creditos no sistema academico.

## Visao Geral

O APOFlow foi pensado para reduzir o trabalho manual hoje distribuido entre aluno, orientador, comissao, coordenacao e secretaria. O projeto e organizado em duas camadas principais, frontend e backend, empacotadas em containers Docker separados orquestrados pelo Docker Compose.

Fluxo coberto no prototipo:

1. Aluno submete APO com descricao, pontos e anexos.
2. Orientador avalia e pode aprovar ou devolver com justificativa.
3. Aluno pode editar e reenviar APO devolvida, ou desistir.
4. Comissao registra votos e consolida parecer.
5. Coordenacao toma a decisao final.
6. Secretaria arquiva e realiza o lancamento quando o aluno atinge 12 pontos.

## Funcionalidades Principais

- autenticacao por e-mail e senha com verificacao em dois fatores via OTP (e-mail)
- tokens JWT stateless com validade de 24 horas
- dashboard especifico para cada ator do fluxo
- formulario de submissao de APO com opcao de salvar rascunho
- visualizacao de rascunhos e APOs enviadas pelo aluno
- pontos por atividade na tela de APOs do aluno
- notificacoes in-app e por e-mail em cada transicao de status
- troca de perfil para professor entre orientador, comissao e coordenacao

## Stack

- Frontend: React 18, TypeScript, Vite, Tailwind CSS, React Router, TanStack Query, Framer Motion, Sonner
- Backend: Java 21, Spring Boot 3, Spring Security, Spring Data MongoDB
- Banco de dados: MongoDB 7.0
- E-mail: MailerSend HTTP API
- Containerizacao: Docker multi-stage e Docker Compose
- Infraestrutura: AWS EC2 (t3.small) provisionada via Terraform

## Arquitetura do Projeto

### Organizacao por camadas

- `Frontend/`: interface React responsavel por login com OTP, dashboards, formularios e navegacao por perfil.
- `Backend/`: API REST Spring Boot responsavel pelas regras de negocio, persistencia no MongoDB, JWT e envio de e-mails.
- `Dockerfile`: build multi-stage que compila o frontend, empacota o backend e publica uma imagem final unica.
- `docker-compose.yml`: orquestra os containers da aplicacao (apoflow) e do banco (mongodb).
- `terraform/`: infraestrutura AWS como codigo — VPC, EC2, Elastic IP e Security Groups.

### Estrutura principal

```text
.
├── Frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/      # layout, sidebar, login com OTP, componentes de UI
│   │   ├── contexts/        # autenticacao JWT e estado global
│   │   ├── lib/             # cliente HTTP, tipos e utilitários
│   │   └── pages/           # dashboards e telas por fluxo de negocio
│   └── package.json
├── Backend/
│   ├── src/main/java/com/apoflow/backend/
│   │   ├── api/             # controllers, handlers e DTOs
│   │   ├── config/          # SecurityConfig, DataInitializer
│   │   ├── domain/          # entidades e enums do dominio APO
│   │   ├── repository/      # acesso ao MongoDB com Spring Data
│   │   └── service/         # regras de negocio, EmailService, NotificationService
│   └── pom.xml
├── terraform/               # infraestrutura AWS (VPC, EC2, EIP)
├── Dockerfile
├── docker-compose.yml
└── README.md
```

### Arquitetura de execucao

1. O estagio `frontend-build` do Dockerfile instala dependencias do Vite e gera o build estatico do React.
2. O estagio `backend-build` compila o Spring Boot e copia o conteudo gerado do frontend para `src/main/resources/static`.
3. A imagem final sobe apenas o `jar` do backend, que serve tanto a API quanto os arquivos estaticos do frontend.
4. O MongoDB roda em container separado com volume persistente.

### Fluxo de runtime

- Navegador acessa `http://localhost:8080`
- Spring Boot entrega a interface React ja buildada
- O frontend chama rotas REST em `/api/...` com token JWT no header `Authorization: Bearer`
- O backend processa autenticacao (login, OTP, JWT), APOs, notificacoes e workflow dos perfis
- Os dados sao persistidos no MongoDB

## Variaveis de Ambiente

| Variavel | Descricao | Padrao |
|---|---|---|
| `MONGODB_URI` | URI de conexao com o MongoDB | `mongodb://localhost:27017/apoflow` |
| `JWT_SECRET` | Chave secreta para assinar tokens JWT (min. 32 chars) | valor de desenvolvimento |
| `MAILERSEND_TOKEN` | Token de API do MailerSend para envio de e-mails | — |
| `MAILERSEND_FROM` | Endereco de origem dos e-mails enviados | trial do MailerSend |
| `EMAIL_ENABLED` | Habilita envio real de e-mails (`true`/`false`) | `false` |
| `SEED_ALUNO_PASSWORD` | Senha do usuario aluno de demonstracao | — |
| `SEED_ORIENTADOR_PASSWORD` | Senha do usuario orientador de demonstracao | — |
| `SEED_COMISSAO_PASSWORD` | Senha do usuario comissao de demonstracao | — |
| `SEED_COORDENACAO_PASSWORD` | Senha do usuario coordenacao de demonstracao | — |
| `SEED_SECRETARIA_PASSWORD` | Senha do usuario secretaria de demonstracao | — |

Crie um arquivo `.env` na raiz do projeto com essas variaveis antes de executar.

## Como Executar com Docker

Requisitos:

- Docker
- Docker Compose

### Subir a aplicacao

```bash
docker compose up --build
```

Esse comando:

- constroi o frontend React
- empacota o backend Spring Boot
- sobe o container MongoDB com volume persistente
- publica a aplicacao em `http://localhost:8080`

### Executar em segundo plano

```bash
docker compose up --build -d
```

### Parar a aplicacao

```bash
docker compose down
```

### Reconstruir apos alteracoes

```bash
docker compose up --build
```

### Ver logs da aplicacao

```bash
docker compose logs -f
```

### Remover containers, rede, volumes e artefatos

```bash
docker compose down --volumes --remove-orphans
```

## Como Funciona o Dockerfile

O arquivo [Dockerfile](Dockerfile) usa tres estagios:

- `frontend-build`: gera os arquivos estaticos do React com Vite
- `backend-build`: compila o backend com Maven e incorpora o frontend buildado
- `runtime`: sobe uma imagem enxuta com Java 21 JRE e o `jar` final

Isso evita instalar Node e Maven na imagem final e reduz o tamanho do artefato de producao.

## Publicacao e Portas

- Aplicacao web: `http://localhost:8080`
- API REST: `http://localhost:8080/api`
- MongoDB: porta `27017` (acessivel apenas entre containers)

## Deploy na AWS

A infraestrutura e gerenciada por Terraform na pasta `terraform/`. Sao criados: VPC, subnet publica, Internet Gateway, Security Group (portas 22, 80, 443, 8080), Elastic IP e instancia EC2 Ubuntu 22.04 (t3.small).

### Pre-requisitos

- Terraform >= 1.8
- AWS CLI configurado ou variaveis `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`
- Par de chaves EC2 criado na AWS com o nome `apoflow-key`

### Provisionar

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars  # preencha os valores
terraform init
terraform apply
```

Apos o apply, a EC2 executa automaticamente o `user_data.sh`, que instala Docker, clona o repositorio e sobe o `docker compose`. A aplicacao estara disponivel em `http://<EC2_IP>:8080` apos cerca de 5 minutos.

### Destruir infraestrutura

```bash
terraform destroy
```

## Acesso ao Prototipo

Login em duas etapas:

1. Informe e-mail e senha.
2. Um codigo OTP de 6 digitos e enviado para o e-mail cadastrado. Informe o codigo para concluir o login.

Os usuarios de demonstracao sao criados no startup com as senhas definidas pelas variaveis de ambiente `SEED_*`.

O perfil de professor pode alternar entre orientador, comissao e coordenacao no menu lateral.

## Casos de Uso Cobertos

- aluno salva rascunho
- aluno envia APO
- orientador aprova ou devolve
- aluno edita ou desiste apos devolucao
- comissao registra votos
- coordenacao toma decisao final
- secretaria arquiva e lanca quando o total atinge 12 pontos

## Comunicacao da API

Principais rotas implementadas:

- `POST /api/auth/login`
- `POST /api/auth/verify-otp`
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
