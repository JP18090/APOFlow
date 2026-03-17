import { APORecord, AlunoResumo, NotificationItem, Role, Usuario } from '@/lib/mock-data';

const API_BASE = '/api';

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    const errorBody = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(errorBody?.message ?? 'Falha na comunicação com a API.');
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const queryKeys = {
  apos: ['apos'] as const,
  students: ['students'] as const,
  notifications: (recipient: string) => ['notifications', recipient] as const,
};

export function login(role: Role) {
  return request<Usuario>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ role }),
  });
}

export function getApos() {
  return request<APORecord[]>('/apos');
}

export function getStudents() {
  return request<AlunoResumo[]>('/students');
}

export function getNotifications(recipient: string) {
  return request<NotificationItem[]>(`/notifications?recipient=${encodeURIComponent(recipient)}`);
}

export function createApo(payload: {
  alunoId: string;
  titulo: string;
  tipo: string;
  descricao: string;
  pontos: number;
  anexos: string[];
}) {
  return request<APORecord>('/apos', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function approveByOrientador(apoId: string) {
  return request<APORecord>(`/apos/${apoId}/orientador/aprovar`, { method: 'POST' });
}

export function returnByOrientador(apoId: string, justificativa: string) {
  return request<APORecord>(`/apos/${apoId}/orientador/devolver`, {
    method: 'POST',
    body: JSON.stringify({ justificativa }),
  });
}

export function voteApo(apoId: string, payload: { membro: string; decisao: 'aprovar' | 'reprovar' | 'devolver'; justificativa: string }) {
  return request<APORecord>(`/apos/${apoId}/comissao/voto`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function decideApo(apoId: string, action: 'aprovar' | 'reprovar' | 'devolver', justificativa: string) {
  return request<APORecord>(`/apos/${apoId}/coordenacao/decisao?action=${encodeURIComponent(action)}`, {
    method: 'POST',
    body: JSON.stringify({ justificativa }),
  });
}

export function archiveApo(apoId: string) {
  return request<APORecord>(`/apos/${apoId}/secretaria/arquivar`, { method: 'POST' });
}

export function launchApo(apoId: string) {
  return request<APORecord>(`/apos/${apoId}/secretaria/lancar`, { method: 'POST' });
}