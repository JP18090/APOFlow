import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { login as loginRequest } from '@/lib/api';
import { Role, Usuario } from '@/lib/mock-data';

interface AuthContextValue {
  user: Usuario | null;
  login: (role: Role) => Promise<void>;
  logout: () => void;
  isAuthenticating: boolean;
}

const STORAGE_KEY = 'apoflow.current-user';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function getStoredUser() {
  if (typeof window === 'undefined') {
    return null;
  }

  const value = window.localStorage.getItem(STORAGE_KEY);
  return value ? (JSON.parse(value) as Usuario) : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<Usuario | null>(() => getStoredUser());
  const [isAuthenticating, setIsAuthenticating] = useState(false);

  useEffect(() => {
    if (typeof window === 'undefined') {
      return;
    }

    if (user) {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
      return;
    }

    window.localStorage.removeItem(STORAGE_KEY);
  }, [user]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      login: async (role) => {
        setIsAuthenticating(true);
        try {
          const nextUser = await loginRequest(role);
          setUser(nextUser);
        } finally {
          setIsAuthenticating(false);
        }
      },
      logout: () => setUser(null),
      isAuthenticating,
    }),
    [isAuthenticating, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
}