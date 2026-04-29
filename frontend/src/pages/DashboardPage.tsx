import { useQuery } from '@tanstack/react-query';
import { NavLink } from 'react-router-dom';
import { balance, offers, purchases, recommendations } from '../api/loyaltyApi';

export function DashboardPage() {
  const balanceQuery = useQuery({ queryKey: ['balance'], queryFn: balance });
  const purchasesQuery = useQuery({ queryKey: ['purchases'], queryFn: purchases });
  const recQuery = useQuery({ queryKey: ['recommendations'], queryFn: recommendations });
  const offersQuery = useQuery({ queryKey: ['offers'], queryFn: offers });

  return <section className="page">
    <div className="page-title"><p className="label">Личный кабинет</p><h1>Обзор программы лояльности</h1></div>
    <div className="stats">
      <div className="stat-card"><span>Баллы</span><b>{balanceQuery.data?.pointsBalance ?? '...'}</b><p>доступный баланс</p></div>
      <div className="stat-card"><span>Сегмент</span><b>{balanceQuery.data?.segment ?? '...'}</b><p>по истории покупок</p></div>
      <div className="stat-card"><span>Офферы</span><b>{offersQuery.data?.length ?? '...'}</b><p>активных предложений</p></div>
    </div>
    <div className="two-columns">
      <div className="card"><div className="card-head"><h2>История покупок</h2><NavLink to="/catalog">Купить товар</NavLink></div>{purchasesQuery.data?.content.length ? purchasesQuery.data.content.map((purchase) => <div className="row" key={purchase.id}><div><b>{purchase.merchantName}</b><p>{new Date(purchase.purchasedAt).toLocaleString()}</p></div><strong>{Number(purchase.totalAmount).toFixed(2)} ₽</strong></div>) : <p className="muted">Пока покупок нет. Перейдите в каталог и создайте первую покупку.</p>}</div>
      <div className="card"><div className="card-head"><h2>Рекомендации</h2><span>2 REST API</span></div>{recQuery.data?.map((item) => <div className="note" key={item.title}><b>{item.title}</b><p>{item.description}</p><small>{item.source}</small></div>)}</div>
    </div>
  </section>;
}
