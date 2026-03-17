import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Toaster as Sonner } from '@/components/ui/sonner';
import { Toaster } from '@/components/ui/toaster';
import { TooltipProvider } from '@/components/ui/tooltip';
import { AuthProvider } from '@/contexts/AuthContext';
import DashboardRouter from '@/pages/DashboardRouter';
import NovaAPORoute from '@/pages/NovaAPORoute';
import MinhasAPOs from '@/pages/MinhasAPOs';
import Pendencias from '@/pages/Pendencias';
import Votacao from '@/pages/Votacao';
import AvaliacaoFinal from '@/pages/AvaliacaoFinal';
import Lancamento from '@/pages/Lancamento';
import NotificacoesRoute from '@/pages/NotificacoesRoute';
import NotFound from '@/pages/NotFound';

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <AuthProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<DashboardRouter />} />
            <Route path="/nova-apo" element={<NovaAPORoute />} />
            <Route path="/minhas-apos" element={<MinhasAPOs />} />
            <Route path="/pendencias" element={<Pendencias />} />
            <Route path="/votacao" element={<Votacao />} />
            <Route path="/avaliacao-final" element={<AvaliacaoFinal />} />
            <Route path="/lancamento" element={<Lancamento />} />
            <Route path="/notificacoes" element={<NotificacoesRoute />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
