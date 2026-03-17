import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { motion } from 'framer-motion';
import { RotateCcw, ThumbsDown, ThumbsUp, Vote } from 'lucide-react';
import { StatusBadge } from '@/components/StatusBadge';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Textarea } from '@/components/ui/textarea';
import { useAuth } from '@/contexts/AuthContext';
import { getApos, queryKeys, voteApo } from '@/lib/api';
import { APORecord } from '@/lib/mock-data';
import { toast } from 'sonner';

export default function ComissaoDashboard() {
  const { user } = useAuth();
  const { data: apos = [] } = useQuery({ queryKey: queryKeys.apos, queryFn: getApos });
  const itens = apos.filter((entry) => entry.status === 'em_avaliacao_comissao');

  return (
    <div className="max-w-5xl space-y-6">
      <div>
        <h1 className="text-2xl font-display font-bold">Painel da Comissão</h1>
        <p className="font-body text-sm text-muted-foreground">Itens para avaliação e votação</p>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <Card className="shadow-card">
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary text-warning"><Vote className="h-5 w-5" /></div>
            <div><p className="font-body text-xs text-muted-foreground">Aguardando Voto</p><p className="text-xl font-display font-bold">{itens.length}</p></div>
          </CardContent>
        </Card>
        <Card className="shadow-card">
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary text-success"><ThumbsUp className="h-5 w-5" /></div>
            <div><p className="font-body text-xs text-muted-foreground">Aprovados (mês)</p><p className="text-xl font-display font-bold">5</p></div>
          </CardContent>
        </Card>
        <Card className="shadow-card">
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary text-destructive"><ThumbsDown className="h-5 w-5" /></div>
            <div><p className="font-body text-xs text-muted-foreground">Devolvidos</p><p className="text-xl font-display font-bold">1</p></div>
          </CardContent>
        </Card>
      </div>

      <div className="space-y-4">
        {itens.map((apo, index) => (
          <motion.div key={apo.id} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.05 }}>
            {user && <VotacaoCard apo={apo} memberName={user.nome} />}
          </motion.div>
        ))}
        {itens.length === 0 && (
          <Card className="shadow-card">
            <CardContent className="p-8 text-center font-body text-muted-foreground">Nenhum item pendente de votação.</CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}

function VotacaoCard({ apo, memberName }: { apo: APORecord; memberName: string }) {
  const [justificativa, setJustificativa] = useState('');
  const queryClient = useQueryClient();
  const voteMutation = useMutation({
    mutationFn: (decisao: 'aprovar' | 'reprovar' | 'devolver') => voteApo(apo.id, { membro: memberName, decisao, justificativa }),
    onSuccess: async (_, decisao) => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.apos });
      toast.success(`Voto "${decisao}" registrado para "${apo.titulo}".`);
      setJustificativa('');
    },
    onError: (error) => {
      toast.error(error instanceof Error ? error.message : 'Nao foi possivel registrar o voto.');
    },
  });

  const votar = (decisao: 'aprovar' | 'reprovar' | 'devolver') => {
    if (!justificativa.trim()) {
      toast.error('Adicione uma justificativa antes de registrar o voto.');
      return;
    }

    voteMutation.mutate(decisao);
  };

  return (
    <Card className="shadow-card">
      <CardHeader className="pb-3">
        <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
          <div>
            <CardTitle className="text-base font-display">{apo.titulo}</CardTitle>
            <p className="mt-1 font-body text-xs text-muted-foreground">{apo.aluno} • {apo.tipo} • {apo.pontos} pt</p>
          </div>
          <StatusBadge status={apo.status} />
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="font-body text-sm text-muted-foreground">{apo.descricao}</p>

        {apo.votos && apo.votos.length > 0 && (
          <div className="space-y-1">
            <p className="text-xs font-display font-semibold text-foreground">Votos registrados:</p>
            {apo.votos.map((voto) => (
              <div key={`${apo.id}-${voto.membro}`} className="flex items-center gap-2 font-body text-xs">
                <Badge variant={voto.decisao === 'aprovar' ? 'default' : 'destructive'} className={voto.decisao === 'aprovar' ? 'bg-success text-success-foreground' : ''}>
                  {voto.decisao}
                </Badge>
                <span className="text-muted-foreground">{voto.membro}</span>
              </div>
            ))}
          </div>
        )}

        <Textarea placeholder="Justificativa do voto..." value={justificativa} onChange={(event) => setJustificativa(event.target.value)} rows={2} />
        <div className="flex flex-wrap justify-end gap-2">
          <Button variant="outline" size="sm" onClick={() => votar('devolver')} disabled={voteMutation.isPending}>
            <RotateCcw className="mr-1 h-3 w-3" /> Devolver
          </Button>
          <Button variant="outline" size="sm" className="text-destructive" onClick={() => votar('reprovar')} disabled={voteMutation.isPending}>
            <ThumbsDown className="mr-1 h-3 w-3" /> Reprovar
          </Button>
          <Button size="sm" className="bg-gradient-accent font-display text-accent-foreground" onClick={() => votar('aprovar')} disabled={voteMutation.isPending}>
            <ThumbsUp className="mr-1 h-3 w-3" /> Aprovar
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
