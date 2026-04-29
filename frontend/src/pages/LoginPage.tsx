import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { login } from '../api/loyaltyApi';
import { useAuthStore } from '../store/authStore';

const schema = z.object({ email: z.string().email(), password: z.string().min(6) });
type Form = z.infer<typeof schema>;
export function LoginPage() {
  const { register, handleSubmit, setError, formState } = useForm<Form>({ defaultValues: { email: 'alice@example.com', password: 'password123' } });
  const setSession = useAuthStore((state) => state.setSession);
  const onSubmit = handleSubmit(async (values) => { const parsed = schema.safeParse(values); if (!parsed.success) return setError('email', { message: 'Check email and password' }); try { const session = await login(parsed.data.email, parsed.data.password); setSession(session.accessToken, session.refreshToken, session.customer); } catch { setError('email', { message: 'Login failed' }); } });
  return <section className="card auth"><h2>Sign in</h2><form onSubmit={onSubmit}><input placeholder="Email" {...register('email')} /><input placeholder="Password" type="password" {...register('password')} /><button disabled={formState.isSubmitting}>Login</button>{formState.errors.email ? <p className="error">{formState.errors.email.message}</p> : null}</form></section>;
}
