import AppLayout from '@/components/AppLayout';
import LoginPage from '@/components/LoginPage';
import { useAuth } from '@/contexts/AuthContext';
import SecretariaDashboard from '@/pages/SecretariaDashboard';

export default function Lancamento() {
  const { user } = useAuth();

  if (!user) {
    return <LoginPage />;
  }

  return (
    <AppLayout>
      <SecretariaDashboard />
    </AppLayout>
  );
}
