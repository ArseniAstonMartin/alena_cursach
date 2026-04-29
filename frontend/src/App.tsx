import { NavLink, Navigate, Route, Routes } from 'react-router-dom';
import type { ReactElement } from 'react';
import { useAuthStore } from './store/authStore';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { CatalogPage } from './pages/CatalogPage';
import { OffersPage } from './pages/OffersPage';

function RequireAuth({ children }: { children: ReactElement }) {
  const customer = useAuthStore((state) => state.customer);
  return customer ? children : <Navigate to="/login" replace />;
}

export default function App() {
  const { customer, logout } = useAuthStore();
  return <div className="layout">
    <header className="header">
      <NavLink to="/" className="logo"><span>LP</span><div><b>Loyalty Platform</b><small>умные предложения и баллы</small></div></NavLink>
      <nav className="menu">
        <NavLink to="/">Главная</NavLink>
        <NavLink to="/catalog">Каталог</NavLink>
        <NavLink to="/offers">Предложения</NavLink>
      </nav>
      <div className="header-actions">
        {customer ? <><span className="profile-chip">{customer.fullName} · {customer.segment}</span><button className="secondary" onClick={logout}>Выйти</button></> : <NavLink className="primary-link" to="/login">Войти</NavLink>}
      </div>
    </header>
    <main className="main">
      <Routes>
        <Route path="/login" element={customer ? <Navigate to="/" replace /> : <LoginPage />} />
        <Route path="/catalog" element={<CatalogPage />} />
        <Route path="/offers" element={<RequireAuth><OffersPage /></RequireAuth>} />
        <Route path="/" element={customer ? <DashboardPage /> : <LandingPage />} />
      </Routes>
    </main>
  </div>;
}

function LandingPage() {
  return <section className="landing">
    <div className="landing-text">
      <p className="label">Курсовой проект</p>
      <h1>Платформа лояльности с персональными предложениями</h1>
      <p>Клиент видит баланс баллов, историю покупок, каталог партнеров и предложения, подобранные по сегменту и истории покупок.</p>
      <div className="actions"><NavLink className="primary-link" to="/login">Войти или зарегистрироваться</NavLink><NavLink className="secondary-link" to="/catalog">Посмотреть каталог</NavLink></div>
    </div>
    <div className="landing-card">
      <h3>Демо-вход</h3>
      <p><b>Email:</b> alice@example.com</p>
      <p><b>Пароль:</b> password123</p>
      <div className="feature-list"><span>JWT + refresh tokens</span><span>PostgreSQL + Flyway</span><span>React + Zustand</span><span>REST API v1</span></div>
    </div>
  </section>;
}
