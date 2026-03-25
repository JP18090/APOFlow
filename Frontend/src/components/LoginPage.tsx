import { motion } from 'framer-motion';
import { GraduationCap } from 'lucide-react';
import { type FormEvent, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { MackenzieLogo } from '@/components/MackenzieLogo';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';

export default function LoginPage() {
  const { login, isAuthenticating } = useAuth();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  const handleLogin = async (event: FormEvent) => {
    event.preventDefault();
    try {
      await login(email, senha);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Nao foi possivel autenticar.');
    }
  };

  return (
    <div className="relative min-h-screen bg-gradient-hero flex items-center justify-center p-4">
      <div className="absolute right-4 top-4">
        <MackenzieLogo />
      </div>
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
            <p className="mb-4 text-center font-body text-sm text-muted-foreground">Acesse com e-mail e senha</p>
            <form className="space-y-3" onSubmit={handleLogin}>
              <div className="space-y-1.5">
                <Label className="font-display text-sm">E-mail</Label>
                <Input type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="usuario@mackenzie.com" required />
              </div>
              <div className="space-y-1.5">
                <Label className="font-display text-sm">Senha</Label>
                <Input type="password" value={senha} onChange={(event) => setSenha(event.target.value)} required />
              </div>
              <Button type="submit" className="w-full bg-gradient-accent font-display font-semibold text-accent-foreground" disabled={isAuthenticating}>
                Entrar
              </Button>
            </form>
            <div className="mt-4 rounded-lg bg-secondary/60 p-3">
              <p className="mb-1 text-xs font-display font-semibold text-foreground">Credenciais de demonstração</p>
              <p className="text-xs text-muted-foreground">aluno@mackenzie.com / JosePedro</p>
              <p className="text-xs text-muted-foreground">orientador@mackenzie.com / GustavoNeto</p>
              <p className="text-xs text-muted-foreground">comissao@mackenzie.com / GabrielLabarca</p>
              <p className="text-xs text-muted-foreground">coordenacao@mackenzie.com / VitorCosta</p>
              <p className="text-xs text-muted-foreground">secretaria@mackenzie.com / LuizBatista</p>
            </div>
            {isAuthenticating && <p className="pt-2 text-center font-body text-xs text-muted-foreground">Conectando com a API...</p>}
          </CardContent>
        </Card>

        <p className="mt-6 text-center font-body text-xs text-primary-foreground/30">Protótipo v1 - Equipe APOFlow 2026</p>
      </motion.div>
    </div>
  );
}
