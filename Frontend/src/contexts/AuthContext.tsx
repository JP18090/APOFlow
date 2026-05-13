import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { login as loginRequest, verifyOtp as verifyOtpRequest, clearToken } from '@/lib/api';
import { Role, Usuario } from '@/lib/mock-data';

interface AuthContextValue {
  user: Usuario | null;
  pendingOtpEmail: string | null;
  login: (email: string, senha: string) => Promise<void>;
  verifyOtp: (code: string) => Promise<void>;
  switchProfessorRole: (role: Role) => void;
  logout: () => void;
  isAuthenticating: boolean;
}

const STORAGE_KEY = 'apoflow.current-user';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function getStoredUser() {
  if (typeof window === 'undefined') return null;
  const value = window.localStorage.getItem(STORAGE_KEY);
  return value ? (JSON.parse(value) as Usuario) : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<Usuario | null>(() => getStoredUser());
  const [pendingOtpEmail, setPendingOtpEmail] = useState<string | null>(null);
  const [isAuthenticating, setIsAuthenticating] = useState(false);

  useEffect(() => {
    if (typeof window === 'undefined') return;
    if (user) {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    } else {
      window.localStorage.removeItem(STORAGE_KEY);
    }
  }, [user]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      pendingOtpEmail,
      login: async (email, senha) => {
        setIsAuthenticating(true);
        try {
          const res = await loginRequest(email, senha);
          // mensagem === 'OTP_REQUIRED' means 2FA code was sent
          if (res.mensagem === 'OTP_REQUIRED') {
            setPendingOtpEmail(res.email);
          } else if (res.token) {
            setUser({ id: res.userId, nome: res.nome, email: res.email, papel: res.papel });
          }
        } finally {
          setIsAuthenticating(false);
        }
      },
      verifyOtp: async (code) => {
        if (!pendingOtpEmail) throw new Error('Nenhum login pendente.');
        setIsAuthenticating(true);
        try {
          const res = await verifyOtpRequest(pendingOtpEmail, code);
          setPendingOtpEmail(null);
          setUser({ id: res.userId, nome: res.nome, email: res.email, papel: res.papel });
        } finally {
          setIsAuthenticating(false);
        }
      },
      switchProfessorRole: (role) => {
        if (!user) return;
        if (!['orientador', 'comissao', 'coordenacao'].includes(user.papel)) return;
        if (!['orientador', 'comissao', 'coordenacao'].includes(role)) return;
        setUser({ ...user, papel: role });
      },
      logout: () => {
        clearToken();
        setPendingOtpEmail(null);
        setUser(null);
      },
      isAuthenticating,
    }),
    [isAuthenticating, user, pendingOtpEmail]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
}