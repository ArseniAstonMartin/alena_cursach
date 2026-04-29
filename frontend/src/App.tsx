import { NavLink, Navigate, Route, Routes } from 'react-router-dom';
import type { ReactElement } from 'react';
import { useAuthStore } from './store/authStore';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { CatalogPage } from './pages/CatalogPage';
import { OffersPage } from './pages/OffersPage';

function Protected({ children }: { children: ReactElement }) {
  return useAuthStore.getState().customer ? children : <Navigate to="/login" replace />;
}

export default function App() {
  const { customer, logout } = useAuthStore();
  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><span className="brand-mark">LP</span><div><b>Loyalty Pro</b><small>personal rewards</small></div></div>
      <nav className="nav-links">
        <NavLink to="/">Overview</NavLink>
        <NavLink to="/catalog">Smart catalog</NavLink>
        <NavLink to="/offers">Personal offers</NavLink>
      </nav>
      <div className="sidebar-card">
        <span>Course project</span>
        <strong>Distributed information systems</strong>
        <p>Spring Boot API + React SPA + PostgreSQL + Redis</p>
      </div>
    </aside>
    <main className="content">
      <header className="topbar">
        <div><p className="eyebrow">Loyalty platform</p><h1>{customer ? `Welcome, ${customer.fullName}` : 'Personalized loyalty workspace'}</h1></div>
        {customer ? <div className="user-pill"><span>{customer.segment}</span><button className="ghost" onClick={logout}>Logout</button></div> : <NavLink className="button-link" to="/login">Sign in</NavLink>}
      </header>
      <Routes>
        <Route path="/login" element={customer ? <Navigate to="/" replace /> : <LoginPage />} />
        <Route path="/catalog" element={<Protected><CatalogPage /></Protected>} />
        <Route path="/offers" element={<Protected><OffersPage /></Protected>} />
        <Route path="/" element={<Protected><DashboardPage /></Protected>} />
      </Routes>
    </main>
  </div>;
}
