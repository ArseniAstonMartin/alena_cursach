import { useQuery } from '@tanstack/react-query';
import { balance, offers, purchases, recommendations } from '../api/loyaltyApi';

export function DashboardPage() {
  const balanceQuery = useQuery({ queryKey: ['balance'], queryFn: balance });
  const purchasesQuery = useQuery({ queryKey: ['purchases'], queryFn: purchases });
  const recQuery = useQuery({ queryKey: ['recommendations'], queryFn: recommendations });
  const offersQuery = useQuery({ queryKey: ['offers-preview'], queryFn: offers });
  const points = balanceQuery.data?.pointsBalance ?? 0;
  return <section className="page-stack">
    <div className="kpi-grid">
      <article className="kpi-card gradient"><span>Баланс</span><strong>{balanceQuery.isLoading ? '...' : points}</strong><p>баллов доступно для списания</p></article>
      <article className="kpi-card"><span>Сегмент</span><strong>{balanceQuery.data?.segment ?? '...'}</strong><p>пересчитывается по истории покупок</p></article>
      <article className="kpi-card"><span>Офферы</span><strong>{offersQuery.data?.length ?? '...'}</strong><p>персональных предложений активно</p></article>
    </div>
    <div className="dashboard-grid">
      <article className="panel"><div className="panel-head"><h2>Последние покупки</h2><span>history</span></div>{purchasesQuery.data?.content.length ? purchasesQuery.data.content.map(p => <div className="timeline-row" key={p.id}><span></span><div><b>{p.merchantName}</b><p>{new Date(p.purchasedAt).toLocaleString()} · {Number(p.totalAmount).toFixed(2)} ₽</p></div></div>) : <p className="empty">Покупок пока нет. Откройте каталог и купите товар.</p>}</article>
      <article className="panel"><div className="panel-head"><h2>Интеграции и рекомендации</h2><span>external APIs</span></div>{recQuery.data?.map(r => <div className="recommendation" key={r.title}><b>{r.title}</b><p>{r.description}</p><small>{r.source}</small></div>)}</article>
    </div>
  </section>;
}
