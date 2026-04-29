import { create } from 'zustand';
import type { Customer } from '../types/domain';

type AuthState = { accessToken: string | null; refreshToken: string | null; customer: Customer | null; setSession: (accessToken: string, refreshToken: string, customer: Customer) => void; logout: () => void };
const stored = localStorage.getItem('loyalty-session');
const initial = stored ? JSON.parse(stored) : { accessToken: null, refreshToken: null, customer: null };
export const useAuthStore = create<AuthState>((set) => ({ ...initial, setSession: (accessToken, refreshToken, customer) => { const session = { accessToken, refreshToken, customer }; localStorage.setItem('loyalty-session', JSON.stringify(session)); set(session); }, logout: () => { localStorage.removeItem('loyalty-session'); set({ accessToken: null, refreshToken: null, customer: null }); } }));
