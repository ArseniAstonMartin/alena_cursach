import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { balance, offers, program, purchases, recommendations, redeemReward, transactions } from '../api/loyaltyApi';

export function DashboardPage() {
  const [redeemPoints, setRedeemPoints] = useState(100);
  const client = useQueryClient();
  const balanceQuery = useQuery({ queryKey: ['balance'], queryFn: balance });
  const purchasesQuery = useQuery({ queryKey: ['purchases'], queryFn: purchases });
  const recQuery = useQuery({ queryKey: ['recommendations'], queryFn: recommendations });
  const offersQuery = useQuery({ queryKey: ['offers'], queryFn: offers });
  const programQuery = useQuery({ queryKey: ['program'], queryFn: program });
  const txQuery = useQuery({ queryKey: ['transactions'], queryFn: transactions });
  const redeem = useMutation({ mutationFn: () => redeemReward(redeemPoints, 'Shopping discount certificate'), onSuccess: () => { client.invalidateQueries({ queryKey: ['balance'] }); client.invalidateQueries({ queryKey: ['transactions'] }); } });
  const maxRedeem = Math.max(50, balanceQuery.data?.pointsBalance ?? 50);

  return <section className="page">
    <div className="page-title"><p className="label">Личный кабинет</p><h1>Баллы, покупки и выгода</h1></div>
    <div className="stats">
      <div className="stat-card"><span>Баллы</span><b>{balanceQuery.data?.pointsBalance ?? '...'}</b><p>можно списать на сертификат</p></div>
      <div className="stat-card"><span>Сегмент</span><b>{balanceQuery.data?.segment ?? '...'}</b><p>дает множитель к начислению</p></div>
      <div className="stat-card"><span>Офферы</span><b>{offersQuery.data?.length ?? '...'}</b><p>активируйте перед покупкой</p></div>
    </div>

    <div className="two-columns">
      <div className="card redeem-card"><div className="card-head"><h2>Списать баллы</h2><span>10 баллов = 1 ₽</span></div><p className="muted">Выберите сумму списания. После подтверждения создается сертификат с кодом.</p><input type="range" min="50" max={maxRedeem} step="10" value={Math.min(redeemPoints, maxRedeem)} onChange={(event) => setRedeemPoints(Number(event.target.value))} /><div className="redeem-summary"><b>{Math.min(redeemPoints, maxRedeem)} баллов</b><span>скидка {(Math.min(redeemPoints, maxRedeem) / 10).toFixed(0)} ₽</span></div><button className="primary" disabled={redeem.isPending || (balanceQuery.data?.pointsBalance ?? 0) < 50} onClick={() => redeem.mutate()}>{redeem.isPending ? 'Оформляем...' : 'Получить сертификат'}</button>{redeem.data ? <div className="alert success">Сертификат {redeem.data.confirmationCode} на {Number(redeem.data.discountAmount).toFixed(0)} ₽ создан.</div> : null}{redeem.isError ? <div className="alert error">Недостаточно баллов для списания.</div> : null}</div>
      <div className="card"><div className="card-head"><h2>Как начисляются баллы</h2><span>правила</span></div>{programQuery.data?.rewardRules.map((rule) => <div className="rule-row" key={rule.category}><b>{rule.category}</b><span>{Number(rule.cashbackPercent).toFixed(0)}%</span><p>{rule.description}</p></div>)}<p className="muted">{programQuery.data?.redemptionRule}</p></div>
    </div>

    <div className="two-columns">
      <div className="card"><div className="card-head"><h2>История покупок</h2><NavLink to="/catalog">Купить товар</NavLink></div>{purchasesQuery.data?.content.length ? purchasesQuery.data.content.map((purchase) => <div className="row" key={purchase.id}><div><b>{purchase.merchantName}</b><p>{new Date(purchase.purchasedAt).toLocaleString()}</p></div><strong>{Number(purchase.totalAmount).toFixed(2)} ₽</strong></div>) : <p className="muted">Покупок пока нет. Перейдите в каталог и создайте первую покупку.</p>}</div>
      <div className="card"><div className="card-head"><h2>Движение баллов</h2><span>ledger</span></div>{txQuery.data?.content.length ? txQuery.data.content.map((tx) => <div className="row" key={tx.id}><div><b>{tx.reason}</b><p>{new Date(tx.createdAt).toLocaleString()}</p></div><strong className={tx.points > 0 ? 'positive' : 'negative'}>{tx.points > 0 ? '+' : ''}{tx.points}</strong></div>) : <p className="muted">Операций пока нет.</p>}</div>
    </div>

    <div className="card"><div className="card-head"><h2>Идеи для следующих покупок</h2><span>recommendations</span></div>{recQuery.data?.map((item) => <div className="note" key={item.title}><b>{item.title}</b><p>{item.description}</p></div>)}</div>
  </section>;
}
