import axios from 'axios';
import { useAuthStore } from '../store/authStore';

export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL ?? '/api/v1' });

api.interceptors.request.use((config) => {
  const url = config.url ?? '';
  const token = useAuthStore.getState().accessToken;
  if (token && !url.startsWith('/auth/')) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url ?? '';
    if ((status === 401 || status === 403) && !url.startsWith('/auth/')) {
      useAuthStore.getState().logout();
    }
    return Promise.reject(error);
  }
);
