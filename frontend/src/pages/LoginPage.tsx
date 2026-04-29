import { AxiosError } from 'axios';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { login, registerCustomer } from '../api/loyaltyApi';
import { useAuthStore } from '../store/authStore';

const loginSchema = z.object({ email: z.string().email(), password: z.string().min(4) });
const registerSchema = z.object({ fullName: z.string().min(3), email: z.string().email(), password: z.string().min(6) });
type LoginForm = z.infer<typeof loginSchema>;
type RegisterForm = z.infer<typeof registerSchema>;

function getError(error: unknown) {
  if (error instanceof AxiosError) return error.response?.data?.detail ?? 'Сервер отклонил запрос. Проверьте данные.';
  return 'Не удалось выполнить запрос. Проверьте backend на порту 8080.';
}

export function LoginPage() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [message, setMessage] = useState('');
  const [theme, setTheme] = useState(() => localStorage.getItem('theme') ?? 'light');
  useEffect(() => { document.documentElement.dataset.theme = theme; localStorage.setItem('theme', theme); }, [theme]);
  const setSession = useAuthStore((state) => state.setSession);
  const loginForm = useForm<LoginForm>({ defaultValues: { email: 'alice@example.com', password: 'password123' } });
  const registerForm = useForm<RegisterForm>({ defaultValues: { fullName: '', email: '', password: '' } });

  async function applySession(action: () => Promise<Awaited<ReturnType<typeof login>>>) {
    setMessage('');
    try {
      const session = await action();
      setSession(session.accessToken, session.refreshToken, session.customer);
    } catch (error) {
      setMessage(getError(error));
    }
  }

  const onLogin = loginForm.handleSubmit((values) => {
    const parsed = loginSchema.safeParse(values);
    if (!parsed.success) return setMessage('Введите корректный email и пароль не короче 4 символов.');
    return applySession(() => login(parsed.data.email, parsed.data.password));
  });

  const onRegister = registerForm.handleSubmit((values) => {
    const parsed = registerSchema.safeParse(values);
    if (!parsed.success) return setMessage('Заполните имя, email и пароль не короче 6 символов.');
    return applySession(() => registerCustomer(parsed.data));
  });

  return <section className="auth-page">
    <div className="auth-info">
      <p className="label">Авторизация</p>
      <h1>Войдите или создайте клиента</h1>
      <p>После входа откроются баланс баллов, история покупок, персональные предложения и возможность покупать товары из каталога.</p>
      <div className="theme-box"><b>Тема интерфейса</b><p>Переключите оформление под себя.</p><button type="button" className="secondary" onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}>{theme === 'dark' ? 'Включить светлую тему' : 'Включить темную тему'}</button></div>
    </div>
    <div className="card auth-form">
      <div className="tabs">
        <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => { setMode('login'); setMessage(''); }}>Вход</button>
        <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => { setMode('register'); setMessage(''); }}>Регистрация</button>
      </div>
      {mode === 'login' ? <form onSubmit={onLogin} className="form">
        <label>Email<input {...loginForm.register('email')} /></label>
        <label>Пароль<input type="password" {...loginForm.register('password')} /></label>
        <button className="primary" disabled={loginForm.formState.isSubmitting}>{loginForm.formState.isSubmitting ? 'Входим...' : 'Войти'}</button>
      </form> : <form onSubmit={onRegister} className="form">
        <label>Имя<input placeholder="Иван Иванов" {...registerForm.register('fullName')} /></label>
        <label>Email<input placeholder="ivan@example.com" {...registerForm.register('email')} /></label>
        <label>Пароль<input type="password" placeholder="минимум 6 символов" {...registerForm.register('password')} /></label>
        <button className="primary" disabled={registerForm.formState.isSubmitting}>{registerForm.formState.isSubmitting ? 'Создаем...' : 'Зарегистрироваться'}</button>
      </form>}
      {message ? <div className="alert error">{message}</div> : null}
    </div>
  </section>;
}
