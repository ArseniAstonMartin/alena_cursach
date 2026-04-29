import { Route, Routes, NavLink } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { CatalogPage } from './pages/CatalogPage';
import { OffersPage } from './pages/OffersPage';

export default function App() {
  const { customer, logout } = useAuthStore();
  return <main className="shell"><header className="topbar"><h1>Loyalty Platform</h1><nav><NavLink to="/">Dashboard</NavLink><NavLink to="/catalog">Catalog</NavLink><NavLink to="/offers">Offers</NavLink></nav>{customer ? <button onClick={logout}>Logout {customer.fullName}</button> : null}</header><Routes><Route path="/login" element={<LoginPage />} /><Route path="/catalog" element={<CatalogPage />} /><Route path="/offers" element={<OffersPage />} /><Route path="/" element={customer ? <DashboardPage /> : <LoginPage />} /></Routes></main>;
}
