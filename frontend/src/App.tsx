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
      <NavLink to="/" className="logo" aria-label="Loyalty logo"><span className="logo-mark"><svg viewBox="0 0 48 48" role="img"><path d="M24 4 42 14v20L24 44 6 34V14L24 4Z"/><path d="M16 24h16M24 16v16"/></svg></span></NavLink>
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
      <h3>Персональная программа</h3>
      <p>Сервис анализирует историю покупок, рейтинг клиента и прогресс заданий.</p>
      <div className="feature-list"><span>Копите баллы за покупки</span><span>Обменивайте баллы на сертификаты</span><span>Храните сертификаты в кошельке</span><span>Применяйте скидку в каталоге</span></div>
    </div>
  </section>;
}
