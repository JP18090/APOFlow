export type Role = 'aluno' | 'orientador' | 'comissao' | 'coordenacao' | 'secretaria';

export type BadgeTone = 'default' | 'destructive' | 'secondary' | 'outline';

export type APOStatus =
  | 'rascunho'
  | 'em_avaliacao_orientador'
  | 'em_avaliacao_comissao'
  | 'em_avaliacao_coordenacao'
  | 'aprovado'
  | 'reprovado'
  | 'arquivado'
  | 'lancado';

export interface Usuario {
  id: string;
  nome: string;
  papel: Role;
  email: string;
}

export interface AlunoResumo {
  id: string;
  nome: string;
  orientadorId: string;
  pontosAcumulados: number;
}

export interface APOVote {
  membro: string;
  decisao: 'aprovar' | 'reprovar' | 'devolver';
  justificativa: string;
}

export interface APORecord {
  id: string;
  titulo: string;
  tipo: string;
  descricao: string;
  pontos: number;
  alunoId: string;
  aluno: string;
  orientadorId: string;
  status: APOStatus;
  anexos: string[];
  dataAtualizacao: string;
  votos?: APOVote[];
}

export const tiposAPO = [
  'Artigo em periódico',
  'Artigo em congresso',
  'Capítulo de livro',
  'Estágio docência',
  'Minicurso ministrado',
  'Participação em comissão',
  'Patente ou software',
];

export interface NotificationItem {
  id: string;
  titulo: string;
  tempo: string;
  lida: boolean;
}

export function getNotificationRecipient(role: Role) {
  return role;
}

export function getStatusLabel(status: APOStatus) {
  const labels: Record<APOStatus, string> = {
    rascunho: 'Rascunho',
    em_avaliacao_orientador: 'Em avaliação do orientador',
    em_avaliacao_comissao: 'Em avaliação da comissão',
    em_avaliacao_coordenacao: 'Em avaliação da coordenação',
    aprovado: 'Aprovado',
    reprovado: 'Reprovado',
    arquivado: 'Arquivado',
    lancado: 'Lançado',
  };

  return labels[status];
}

export function getStatusVariant(status: APOStatus): BadgeTone {
  if (status === 'reprovado') {
    return 'destructive';
  }

  if (status === 'aprovado') {
    return 'default';
  }

  if (status === 'arquivado' || status === 'lancado' || status === 'rascunho') {
    return 'outline';
  }

  return 'secondary';
}
