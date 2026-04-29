import { create } from 'zustand';
import type { Customer } from '../types/domain';

type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
  customer: Customer | null;
  setSession: (accessToken: string, refreshToken: string, customer: Customer) => void;
  logout: () => void;
};

function readStoredSession() {
  try {
    const stored = localStorage.getItem('loyalty-session');
    return stored ? JSON.parse(stored) : { accessToken: null, refreshToken: null, customer: null };
  } catch {
    localStorage.removeItem('loyalty-session');
    return { accessToken: null, refreshToken: null, customer: null };
  }
}

export const useAuthStore = create<AuthState>((set) => ({
  ...readStoredSession(),
  setSession: (accessToken, refreshToken, customer) => {
    const session = { accessToken, refreshToken, customer };
    localStorage.setItem('loyalty-session', JSON.stringify(session));
    set(session);
  },
  logout: () => {
    localStorage.removeItem('loyalty-session');
    set({ accessToken: null, refreshToken: null, customer: null });
  }
}));
