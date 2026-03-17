import { useQuery } from '@tanstack/react-query';
import { FileText, Paperclip } from 'lucide-react';
import AppLayout from '@/components/AppLayout';
import LoginPage from '@/components/LoginPage';
import { StatusBadge } from '@/components/StatusBadge';
import { Card, CardContent } from '@/components/ui/card';
import { useAuth } from '@/contexts/AuthContext';
import { getApos, queryKeys } from '@/lib/api';

export default function MinhasAPOs() {
  const { user } = useAuth();
  const { data: allApos = [] } = useQuery({ queryKey: queryKeys.apos, queryFn: getApos, enabled: !!user });

  if (!user) {
    return <LoginPage />;
  }

  const apos = allApos.filter((entry) => entry.alunoId === user.id);

  return (
    <AppLayout>
      <div className="max-w-5xl space-y-6">
        <div>
          <h1 className="text-2xl font-display font-bold">Minhas APOs</h1>
          <p className="font-body text-sm text-muted-foreground">Todas as suas atividades submetidas</p>
        </div>
        <Card className="shadow-card">
          <CardContent className="p-0">
            <div className="divide-y">
              {apos.map((apo) => (
                <div key={apo.id} className="flex items-center justify-between gap-4 px-6 py-4 transition-colors hover:bg-secondary/30">
                  <div className="flex min-w-0 items-center gap-3">
                    <FileText className="h-4 w-4 shrink-0 text-muted-foreground" />
                    <div className="min-w-0">
                      <p className="truncate text-sm font-display font-medium">{apo.titulo}</p>
                      <p className="font-body text-xs text-muted-foreground">{apo.tipo} • {apo.pontos} pt • {apo.dataAtualizacao}</p>
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
              {apos.length === 0 && <div className="p-8 text-center font-body text-muted-foreground">Nenhuma APO encontrada.</div>}
            </div>
          </CardContent>
        </Card>
      </div>
    </AppLayout>
  );
}
