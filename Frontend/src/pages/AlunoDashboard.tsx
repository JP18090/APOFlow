import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { CheckCircle2, Clock, FileText, Paperclip, Plus, TrendingUp } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { StatusBadge } from '@/components/StatusBadge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useAuth } from '@/contexts/AuthContext';
import { getApos, getStudents, queryKeys } from '@/lib/api';

export default function AlunoDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { data: students = [] } = useQuery({ queryKey: queryKeys.students, queryFn: getStudents });
  const { data: apos = [] } = useQuery({ queryKey: queryKeys.apos, queryFn: getApos });

  const aluno = students.find((entry) => entry.id === user?.id);
  const minhasAPOs = apos.filter((entry) => entry.alunoId === user?.id);

  const stats = [
    { label: 'APOs Submetidas', value: minhasAPOs.length, icon: FileText, color: 'text-primary' },
    {
      label: 'Pendentes',
      value: minhasAPOs.filter((entry) => !['aprovado', 'reprovado', 'arquivado', 'lancado', 'rascunho'].includes(entry.status)).length,
      icon: Clock,
      color: 'text-warning',
    },
    {
      label: 'Aprovadas',
      value: minhasAPOs.filter((entry) => ['aprovado', 'arquivado', 'lancado'].includes(entry.status)).length,
      icon: CheckCircle2,
      color: 'text-success',
    },
    { label: 'Pontos Acumulados', value: `${aluno?.pontosAcumulados ?? 0}/12`, icon: TrendingUp, color: 'text-accent' },
  ];

  return (
    <div className="max-w-5xl space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-display font-bold text-foreground">Meu Painel</h1>
          <p className="font-body text-sm text-muted-foreground">Acompanhe suas APOs e créditos</p>
        </div>
        <Button onClick={() => navigate('/nova-apo')} className="bg-gradient-accent font-display font-semibold text-accent-foreground">
          <Plus className="mr-2 h-4 w-4" /> Nova APO
        </Button>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat, index) => (
          <motion.div key={stat.label} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.05 }}>
            <Card className="shadow-card">
              <CardContent className="flex items-center gap-3 p-4">
                <div className={`flex h-10 w-10 items-center justify-center rounded-lg bg-secondary ${stat.color}`}>
                  <stat.icon className="h-5 w-5" />
                </div>
                <div>
                  <p className="font-body text-xs text-muted-foreground">{stat.label}</p>
                  <p className="text-xl font-display font-bold">{stat.value}</p>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      <Card className="shadow-card">
        <CardContent className="p-4">
          <div className="mb-2 flex items-center justify-between">
            <span className="text-sm font-display font-semibold">Progresso para Lançamento</span>
            <span className="font-body text-sm text-muted-foreground">{aluno?.pontosAcumulados ?? 0} de 12 pontos</span>
          </div>
          <div className="h-3 overflow-hidden rounded-full bg-secondary">
            <motion.div
              className="h-full rounded-full bg-gradient-accent"
              initial={{ width: 0 }}
              animate={{ width: `${Math.min((((aluno?.pontosAcumulados ?? 0) / 12) * 100), 100)}%` }}
              transition={{ duration: 0.8, ease: 'easeOut' }}
            />
          </div>
        </CardContent>
      </Card>

      <Card className="shadow-card">
        <CardHeader className="pb-3">
          <CardTitle className="text-lg font-display">APOs Recentes</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <div className="divide-y">
            {minhasAPOs.map((apo) => (
              <div key={apo.id} className="flex items-center justify-between gap-4 px-6 py-4 transition-colors hover:bg-secondary/50">
                <div className="flex min-w-0 items-center gap-3">
                  <FileText className="h-4 w-4 shrink-0 text-muted-foreground" />
                  <div className="min-w-0">
                    <p className="truncate text-sm font-display font-medium">{apo.titulo}</p>
                    <p className="font-body text-xs text-muted-foreground">{apo.tipo} • {apo.pontos} pt</p>
                  </div>
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  {apo.anexos.length > 0 && (
                    <span className="flex items-center gap-1 text-xs text-muted-foreground">
                      <Paperclip className="h-3 w-3" /> {apo.anexos.length}
                    </span>
                  )}
                  <StatusBadge status={apo.status} />
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
