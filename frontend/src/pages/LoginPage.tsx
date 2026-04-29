import { AxiosError } from 'axios';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { login, registerCustomer } from '../api/loyaltyApi';
import { useAuthStore } from '../store/authStore';

const loginSchema = z.object({ email: z.string().email(), password: z.string().min(6) });
const registerSchema = loginSchema.extend({ fullName: z.string().min(3) });
type LoginForm = z.infer<typeof loginSchema>;
type RegisterForm = z.infer<typeof registerSchema>;

function errorMessage(error: unknown) {
  if (error instanceof AxiosError) return error.response?.data?.detail ?? error.message;
  return 'Request failed. Check that backend is running on port 8080.';
}

export function LoginPage() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [serverError, setServerError] = useState('');
  const setSession = useAuthStore((state) => state.setSession);
  const loginForm = useForm<LoginForm>({ defaultValues: { email: 'alice@example.com', password: 'password123' } });
  const registerForm = useForm<RegisterForm>({ defaultValues: { fullName: '', email: '', password: '' } });

  const submitLogin = loginForm.handleSubmit(async (values) => {
    setServerError('');
    const parsed = loginSchema.safeParse(values);
    if (!parsed.success) return setServerError('Введите корректный email и пароль от 6 символов.');
    try {
      const session = await login(parsed.data.email, parsed.data.password);
      setSession(session.accessToken, session.refreshToken, session.customer);
    } catch (error) { setServerError(errorMessage(error)); }
  });

  const submitRegister = registerForm.handleSubmit(async (values) => {
    setServerError('');
    const parsed = registerSchema.safeParse(values);
    if (!parsed.success) return setServerError('Заполните имя, корректный email и пароль от 6 символов.');
    try {
      const session = await registerCustomer(parsed.data);
      setSession(session.accessToken, session.refreshToken, session.customer);
    } catch (error) { setServerError(errorMessage(error)); }
  });

  return <section className="auth-layout">
    <div className="hero-panel">
      <p className="eyebrow">Personalized loyalty</p>
      <h2>Покупки превращаются в баллы, сегменты и умные предложения</h2>
      <p>Демо-пользователь уже создан: <b>alice@example.com</b> / <b>password123</b>. Можно также зарегистрировать нового клиента.</p>
      <div className="hero-stats"><span><b>12</b> DB tables</span><span><b>JWT</b> auth</span><span><b>REST</b> API</span></div>
    </div>
    <div className="auth-card">
      <div className="tabs"><button className={mode === 'login' ? 'active' : ''} onClick={() => { setMode('login'); setServerError(''); }}>Войти</button><button className={mode === 'register' ? 'active' : ''} onClick={() => { setMode('register'); setServerError(''); }}>Зарегистрироваться</button></div>
      {mode === 'login' ? <form onSubmit={submitLogin} className="form-stack">
        <label>Email<input placeholder="alice@example.com" {...loginForm.register('email')} /></label>
        <label>Password<input placeholder="password123" type="password" {...loginForm.register('password')} /></label>
        <button className="primary" disabled={loginForm.formState.isSubmitting}>{loginForm.formState.isSubmitting ? 'Входим...' : 'Войти в платформу'}</button>
      </form> : <form onSubmit={submitRegister} className="form-stack">
        <label>Full name<input placeholder="Анна Иванова" {...registerForm.register('fullName')} /></label>
        <label>Email<input placeholder="anna@example.com" {...registerForm.register('email')} /></label>
        <label>Password<input type="password" placeholder="минимум 6 символов" {...registerForm.register('password')} /></label>
        <button className="primary" disabled={registerForm.formState.isSubmitting}>{registerForm.formState.isSubmitting ? 'Создаем...' : 'Создать аккаунт'}</button>
      </form>}
      {serverError ? <p className="error-box">{serverError}</p> : null}
      <p className="hint">Если вход не проходит, проверьте что backend запущен на 8080 и frontend открыт на 5173.</p>
    </div>
  </section>;
}
