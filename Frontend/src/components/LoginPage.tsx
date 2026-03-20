import { motion } from 'framer-motion';
import { Archive, GraduationCap, Shield, UserCheck, Users } from 'lucide-react';
import type { ReactNode } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { useAuth } from '@/contexts/AuthContext';
import { Role } from '@/lib/mock-data';
import { toast } from 'sonner';

const roles: { papel: Role; label: string; desc: string; icon: ReactNode }[] = [
  { papel: 'aluno', label: 'Aluno', desc: 'Submeter e acompanhar APOs', icon: <GraduationCap className="h-6 w-6" /> },
  { papel: 'orientador', label: 'Orientador', desc: 'Avaliar submissões dos orientados', icon: <UserCheck className="h-6 w-6" /> },
  { papel: 'comissao', label: 'Comissão', desc: 'Votar e avaliar APOs', icon: <Users className="h-6 w-6" /> },
  { papel: 'coordenacao', label: 'Coordenação', desc: 'Decisão final e assinaturas', icon: <Shield className="h-6 w-6" /> },
  { papel: 'secretaria', label: 'Secretaria', desc: 'Arquivar e lançar no sistema', icon: <Archive className="h-6 w-6" /> },
];

export default function LoginPage() {
  const { login, isAuthenticating } = useAuth();

  const handleLogin = async (role: Role) => {
    try {
      await login(role);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Nao foi possivel autenticar.');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-hero flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-lg"
      >
        <div className="mb-8 text-center">
          <motion.div
            initial={{ scale: 0.8 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: 'spring' }}
            className="mb-4 inline-flex items-center gap-3"
          >
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-accent shadow-glow">
              <GraduationCap className="h-7 w-7 text-accent-foreground" />
            </div>
            <h1 className="text-3xl font-display font-bold text-primary-foreground">APOFlow</h1>
          </motion.div>
          <p className="font-body text-sm text-primary-foreground/60">
            Sistema de Gestão de Atividades Programadas Obrigatórias
          </p>
          <p className="mt-1 font-body text-xs text-primary-foreground/40">
            PPG-CA - Programa de Pós-Graduação em Computação Aplicada
          </p>
        </div>

        <Card className="border-0 shadow-elevated">
          <CardContent className="p-6">
            <p className="mb-4 text-center font-body text-sm text-muted-foreground">
              Selecione um papel para acessar o protótipo
            </p>
            <div className="space-y-2">
              {roles.map((role, index) => (
                <motion.div
                  key={role.papel}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.3 + index * 0.08 }}
                >
                  <Button
                    variant="ghost"
                    className={
                      `h-auto w-full justify-start gap-3 px-4 py-3 role-selectable` +
                      (isAuthenticating ? ' opacity-60' : '')
                    }
                    onMouseEnter={e => e.currentTarget.classList.add('role-selected')}
                    onMouseLeave={e => e.currentTarget.classList.remove('role-selected')}
                    onClick={() => void handleLogin(role.papel)}
                    disabled={isAuthenticating}
                  >
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-white text-accent">
                      {role.icon}
                    </div>
                    <div className="text-left">
                      <div className="font-display font-semibold">{role.label}</div>
                      <div className="font-body text-xs opacity-90">{role.desc}</div>
                    </div>
                  </Button>
                </motion.div>
              ))}
            </div>
            {isAuthenticating && <p className="pt-2 text-center font-body text-xs text-muted-foreground">Conectando com a API...</p>}
          </CardContent>
        </Card>

        <p className="mt-6 text-center font-body text-xs text-primary-foreground/30">Protótipo v1 - Equipe APOFlow 2026</p>
      </motion.div>
    </div>
  );
}
