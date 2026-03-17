# APOFlow

Sistema web para gerenciar o fluxo de Atividades Programadas Obrigatorias do PPG-CA, cobrindo submissao, avaliacao em multiplas etapas, arquivamento de evidencias, notificacoes e apoio ao lancamento de creditos no sistema academico.

## Visao Geral

O APOFlow foi pensado para reduzir o trabalho manual hoje distribuido entre aluno, orientador, comissao, coordenacao e secretaria. O repositorio agora esta dividido em frontend e backend, com comunicacao real via API REST.

Fluxo coberto no prototipo:

1. Aluno submete APO com descricao, pontos e anexos.
2. Orientador avalia e pode aprovar ou devolver com justificativa.
3. Comissao registra votos e consolida parecer.
4. Coordenacao toma a decisao final e aciona a assinatura eletronica.
5. Secretaria arquiva e realiza o lancamento quando o aluno atinge 12 pontos.

## Objetivo

Desenvolver uma aplicacao web que suporte o workflow de submissao e aprovacao das APOs do programa PPG-CA, permitindo:

- cadastro de alunos e professores
- submissao de atividades com evidencias
- fluxo de avaliacao orientador -> comissao -> coordenacao
- arquivamento e lancamento no sistema academico apenas apos 12 pontos
- dashboards e alertas de pendencias e prazos

## Membros do Grupo

- Jose Pedro Bitetti
- Gustavo Netto de Carvalho
- Gabriel Labarca Del Bianco
- Vitor Costa Lemos
- Luiz Batista dos Santos
- Rodrygo Rogerio Vasconcellos

## Escopo

Inclui:

- interface web para aluno, orientador, comissao, coordenacao e secretaria
- sistema de papeis e permissoes
- upload e controle basico de documentos comprobatórios no prototipo
- trilha do workflow com decisoes e justificativas
- relatorios de creditos acumulados e pendencias
- exportacao simulada para lancamento academico

Fora do escopo na v1:

- integracao completa com sistemas externos da universidade
- OCR ou validacao automatica de documentos

## Funcionalidades Principais

- autenticacao por papel para navegar pelo prototipo
- dashboard especifico para cada ator do fluxo
- formulario de submissao de APO
- historico de APOs submetidas pelo aluno
- notificacoes in-app simuladas
- tela de avaliacao do orientador com justificativa
- painel de votacao da comissao
- decisao final pela coordenacao com consolidacao dos votos
- fila de arquivamento e lancamento pela secretaria

## Stack

- Frontend: React 18, TypeScript, Vite, Tailwind CSS, React Router, TanStack Query, Framer Motion, Sonner
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA, H2
- Containerizacao: Docker multi-stage e docker-compose

## Estrutura do Projeto

```text
.
├── Backend/
│   ├── pom.xml
│   └── src/
├── Frontend/
│   ├── docs/
│   │   └── use-cases.md
│   ├── src/
│   └── package.json
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Como Executar Localmente

Requisitos:

- Node.js 20 ou superior
- npm 10 ou superior
- Java 21
- Maven 3.9+

### Backend

```bash
cd Backend
mvn spring-boot:run
```

Servicos disponiveis:

- API: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/h2-console`

### Frontend

Instalacao e execucao:

```bash
cd Frontend
npm install
npm run dev
```

O Vite faz proxy de `/api` para `http://localhost:8080`.

### Builds

```bash
cd Frontend && npm run build
cd Backend && mvn test
```

## Executar com Docker

```bash
docker compose up --build
```

Aplicacao disponibilizada em:

- `http://localhost:8080`

## Papeis Disponiveis no Prototipo

Na tela inicial, o login e feito por selecao de papel para facilitar a navegacao durante a demonstracao.

- Aluno
- Orientador
- Comissao
- Coordenacao
- Secretaria

## Documento de Casos de Uso

Os casos de uso detalhados, incluindo diagramas PlantUML por ator e por processo, estao em:

- [Frontend/docs/use-cases.md](Frontend/docs/use-cases.md)

## Comunicacao da API

Principais rotas implementadas:

- `POST /api/auth/login`
- `GET /api/students`
- `GET /api/apos`
- `POST /api/apos`
- `POST /api/apos/{id}/orientador/aprovar`
- `POST /api/apos/{id}/orientador/devolver`
- `POST /api/apos/{id}/comissao/voto`
- `POST /api/apos/{id}/coordenacao/decisao?action=aprovar|reprovar|devolver`
- `POST /api/apos/{id}/secretaria/arquivar`
- `POST /api/apos/{id}/secretaria/lancar`
- `GET /api/notifications?recipient=aluno|orientador|comissao|coordenacao|secretaria`

## Observacoes

- O frontend agora consome dados reais do backend em vez de mocks locais.
- O backend usa H2 em memoria com carga inicial para demonstracao.
- O Dockerfile raiz gera uma imagem unica com frontend buildado e backend Spring Boot servindo a aplicacao.

