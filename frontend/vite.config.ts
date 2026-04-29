import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    allowedHosts: [
      'f4bbe14a7fb9cf598328-pod-25exqh64dfb5bj3rzavzesv7s4-5173.us7.cursorvm.com',
      '.cursorvm.com'
    ],
    proxy: { '/api': 'http://localhost:8080' }
  }
});
