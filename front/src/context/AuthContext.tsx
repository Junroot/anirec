import { createContext, useState, useEffect, type ReactNode } from 'react';
import type { User } from '@/types/user';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => void;
  signup: (email: string, username: string, password: string) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem('anirec_user');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem('anirec_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('anirec_user');
    }
  }, [user]);

  const login = (_email: string, _password: string) => {
    const mockUser: User = {
      id: 'user-1',
      email: _email,
      username: _email.split('@')[0],
      createdAt: new Date().toISOString(),
    };
    setUser(mockUser);
  };

  const signup = (email: string, username: string, _password: string) => {
    const mockUser: User = {
      id: 'user-' + Date.now(),
      email,
      username,
      createdAt: new Date().toISOString(),
    };
    setUser(mockUser);
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
