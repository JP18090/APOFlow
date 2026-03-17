import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Archive, Download, TrendingUp, Users } from 'lucide-react';
import { StatusBadge } from '@/components/StatusBadge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { archiveApo, getApos, getStudents, launchApo, queryKeys } from '@/lib/api';
import { toast } from 'sonner';

export default function SecretariaDashboard() {
  const queryClient = useQueryClient();
  const { data: apos = [] } = useQuery({ queryKey: queryKeys.apos, queryFn: getApos });
  const { data: students = [] } = useQuery({ queryKey: queryKeys.students, queryFn: getStudents });
  const archiveMutation = useMutation({
    mutationFn: archiveApo,
    onSuccess: async (_, apoId) => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.apos });
      const archived = apos.find((entry) => entry.id === apoId);
      toast.success(`"${archived?.titulo ?? 'APO'}" arquivada.`);
    },
    onError: (error) => {
      toast.error(error instanceof Error ? error.message : 'Nao foi possivel arquivar a APO.');
    },
  });
  const launchMutation = useMutation({
    mutationFn: launchApo,
    onSuccess: async (_, apoId) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.apos }),
        queryClient.invalidateQueries({ queryKey: queryKeys.students }),
      ]);
      const launched = apos.find((entry) => entry.id === apoId);
      toast.success(`Lançamento de ${launched?.titulo ?? 'APO'} no sistema acadêmico!`);
    },
    onError: (error) => {
      toast.error(error instanceof Error ? error.message : 'Nao foi possivel lancar a APO.');
    },
  });

  const aprovados = apos.filter((entry) => entry.status === 'aprovado');
  const arquivados = apos.filter((entry) => entry.status === 'arquivado' || entry.status === 'lancado');
  const alunosProximos = students.filter((entry) => entry.pontosAcumulados >= 10);

  return (
    <div className="max-w-5xl space-y-6">
      <div>
        <h1 className="text-2xl font-display font-bold">Painel da Secretaria</h1>
        <p className="font-body text-sm text-muted-foreground">Arquivamento e lançamento no sistema acadêmico</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          { label: 'Aguardando Arquivamento', value: aprovados.length, icon: Archive, color: 'text-warning' },
          { label: 'Arquivados', value: arquivados.length, icon: Download, color: 'text-success' },
          { label: 'Próximos de 12pt', value: alunosProximos.length, icon: TrendingUp, color: 'text-accent' },
          { label: 'Alunos Ativos', value: students.length, icon: Users, color: 'text-primary' },
        ].map((stat, index) => (
          <motion.div key={stat.label} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.05 }}>
            <Card className="shadow-card">
              <CardContent className="flex items-center gap-3 p-4">
                <div className={`flex h-10 w-10 items-center justify-center rounded-lg bg-secondary ${stat.color}`}><stat.icon className="h-5 w-5" /></div>
                <div><p className="font-body text-xs text-muted-foreground">{stat.label}</p><p className="text-xl font-display font-bold">{stat.value}</p></div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      <Card className="shadow-card">
        <CardHeader>
          <CardTitle className="text-lg font-display">Alunos Próximos do Lançamento (&gt;=10pt)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {alunosProximos.length === 0 ? (
            <p className="font-body text-sm text-muted-foreground">Nenhum aluno próximo do limite.</p>
          ) : (
            alunosProximos.map((aluno) => (
              <div key={aluno.id} className="flex flex-col gap-3 md:flex-row md:items-center">
                {(() => {
                  const launchableApo = arquivados.find((entry) => entry.alunoId === aluno.id && entry.status === 'arquivado');

                  return (
                    <>
                <div className="flex-1">
                  <p className="text-sm font-display font-medium">{aluno.nome}</p>
                  <p className="font-body text-xs text-muted-foreground">{aluno.pontosAcumulados}/12 pontos</p>
                </div>
                <div className="w-full md:w-32">
                  <Progress value={(aluno.pontosAcumulados / 12) * 100} className="h-2" />
                </div>
                {launchableApo && aluno.pontosAcumulados >= 12 && (
                  <Button size="sm" className="bg-gradient-accent font-display text-xs text-accent-foreground" onClick={() => launchMutation.mutate(launchableApo.id)} disabled={launchMutation.isPending}>
                    Lançar
                  </Button>
                )}
                    </>
                  );
                })()}
              </div>
            ))
          )}
        </CardContent>
      </Card>

      <Card className="shadow-card">
        <CardHeader>
          <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <CardTitle className="text-lg font-display">Fila de Arquivamento</CardTitle>
            <Button variant="outline" size="sm" onClick={() => toast.success('Pacote exportado (simulado).')}>
              <Download className="mr-1 h-3 w-3" /> Exportar Pacote
            </Button>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          <div className="divide-y">
            {aprovados.map((apo) => (
              <div key={apo.id} className="flex items-center justify-between gap-4 px-6 py-4 transition-colors hover:bg-secondary/30">
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-display font-medium">{apo.titulo}</p>
                  <p className="font-body text-xs text-muted-foreground">{apo.aluno} • {apo.pontos} pt</p>
                </div>
                <div className="flex items-center gap-2">
                  <StatusBadge status={apo.status} />
                  <Button size="sm" variant="outline" onClick={() => archiveMutation.mutate(apo.id)} disabled={archiveMutation.isPending}>
                    Arquivar
                  </Button>
                </div>
              </div>
            ))}
            {aprovados.length === 0 && <div className="p-8 text-center font-body text-muted-foreground">Nenhum item na fila.</div>}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
