import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { balance, certificates, offers, program, purchaseProfile, purchases, recommendations, redeemReward, transactions } from '../api/loyaltyApi';

export function DashboardPage() {
  const [redeemPoints, setRedeemPoints] = useState(100);
  const client = useQueryClient();
  const balanceQuery = useQuery({ queryKey: ['balance'], queryFn: balance });
  const purchasesQuery = useQuery({ queryKey: ['purchases'], queryFn: purchases });
  const recQuery = useQuery({ queryKey: ['recommendations'], queryFn: recommendations });
  const offersQuery = useQuery({ queryKey: ['offers'], queryFn: offers });
  const profileQuery = useQuery({ queryKey: ['purchase-profile'], queryFn: purchaseProfile });
  const programQuery = useQuery({ queryKey: ['program'], queryFn: program });
  const txQuery = useQuery({ queryKey: ['transactions'], queryFn: transactions });
  const certificatesQuery = useQuery({ queryKey: ['certificates'], queryFn: certificates });
  const redeem = useMutation({ mutationFn: () => redeemReward(redeemPoints, 'Strategic category certificate'), onSuccess: () => { client.invalidateQueries({ queryKey: ['balance'] }); client.invalidateQueries({ queryKey: ['transactions'] }); client.invalidateQueries({ queryKey: ['certificates'] }); } });
  const maxRedeem = Math.max(50, Math.min(200, balanceQuery.data?.pointsBalance ?? 50));
  const activeCertificates = certificatesQuery.data?.filter((certificate) => certificate.status === 'ACTIVE') ?? [];

  return <section className="page">
    <div className="page-title"><p className="label">Личный кабинет</p><h1>Баллы, сертификаты и покупки</h1></div>
    <div className="stats">
      <div className="stat-card"><span>Баллы</span><b>{balanceQuery.data?.pointsBalance ?? '...'}</b><p>можно обменять на сертификат</p></div>
      <div className="stat-card"><span>Активные сертификаты</span><b>{activeCertificates.length}</b><p>доступны для применения в каталоге</p></div>
      <div className="stat-card"><span>Офферы</span><b>{offersQuery.data?.length ?? '...'}</b><p>активируйте перед покупкой</p></div>
    </div>

    <div className="two-columns">
      <div className="card redeem-card"><div className="card-head"><h2>Обменять баллы</h2><span>10 баллов = 1 ₽</span></div><p className="muted">Баллы превращаются в сертификат для категории, которую вы чаще всего покупаете. Так скидка тоже основана на истории покупок.</p><input type="range" min="50" max={maxRedeem} step="10" value={Math.min(redeemPoints, maxRedeem)} onChange={(event) => setRedeemPoints(Number(event.target.value))} /><div className="redeem-summary"><b>{Math.min(redeemPoints, maxRedeem)} баллов</b><span>стратегический сертификат на {(Math.min(redeemPoints, maxRedeem) / 10).toFixed(0)} ₽</span></div><button className="primary" disabled={redeem.isPending || (balanceQuery.data?.pointsBalance ?? 0) < 50} onClick={() => redeem.mutate()}>{redeem.isPending ? 'Оформляем...' : 'Создать сертификат'}</button>{redeem.data ? <div className="alert success">Сертификат {redeem.data.confirmationCode} для {redeem.data.targetCategory} создан. {redeem.data.personalizationReason}</div> : null}{redeem.isError ? <div className="alert error">Недостаточно баллов для списания.</div> : null}</div>
      <div className="card"><div className="card-head"><h2>Кошелек сертификатов</h2><NavLink to="/catalog">Применить</NavLink></div>{certificatesQuery.data?.length ? certificatesQuery.data.map((certificate) => <div className="certificate" key={certificate.id}><div><b>{certificate.confirmationCode}</b><p>{certificate.rewardName}</p><small>Категория: {certificate.targetCategory} · до {new Date(certificate.expiresAt).toLocaleDateString()}</small></div><div><strong>{Number(certificate.discountAmount).toFixed(0)} ₽</strong><span className={`status ${certificate.status.toLowerCase()}`}>{certificate.status}</span></div></div>) : <p className="muted">Сертификатов пока нет. Обменяйте баллы, и они появятся здесь.</p>}</div>
    </div>

    <div className="two-columns">
      <div className="card"><div className="card-head"><h2>История покупок</h2><NavLink to="/catalog">Купить товар</NavLink></div>{purchasesQuery.data?.content.length ? purchasesQuery.data.content.map((purchase) => <div className="purchase-row" key={purchase.id}><div><b>{purchase.merchantName}</b><p>{new Date(purchase.purchasedAt).toLocaleString()}</p>{purchase.certificateCode ? <small>Сертификат {purchase.certificateCode}: -{Number(purchase.discountAmount).toFixed(0)} ₽</small> : null}{purchase.rewardExplanation ? <small>{purchase.rewardExplanation}</small> : null}</div><div><strong>{Number(purchase.totalAmount).toFixed(2)} ₽</strong><span>+{purchase.pointsEarned} баллов</span></div></div>) : <p className="muted">Покупок пока нет. Перейдите в каталог и создайте первую покупку.</p>}</div>
      <div className="card"><div className="card-head"><h2>Движение баллов</h2><span>ledger</span></div>{txQuery.data?.content.length ? txQuery.data.content.map((tx) => <div className="row" key={tx.id}><div><b>{tx.reason}</b><p>{new Date(tx.createdAt).toLocaleString()}</p></div><strong className={tx.points > 0 ? 'positive' : 'negative'}>{tx.points > 0 ? '+' : ''}{tx.points}</strong></div>) : <p className="muted">Операций пока нет.</p>}</div>
    </div>

    <div className="two-columns"><div className="card"><div className="card-head"><h2>Отчет по истории покупок</h2><span>персонализация</span></div><div className="profile-summary"><b>{profileQuery.data?.buyerRating ?? 0}</b><span>рейтинг покупателя · {profileQuery.data?.buyerRatingLabel} · {profileQuery.data?.purchaseCount ?? 0} покупок · средний чек {Number(profileQuery.data?.averageCheck ?? 0).toFixed(0)} ₽</span></div>{profileQuery.data?.lastPurchaseAt ? <p className="muted">Последняя покупка: {new Date(profileQuery.data.lastPurchaseAt).toLocaleString()}</p> : null}{profileQuery.data?.favoriteCategories.length ? profileQuery.data.favoriteCategories.map((category) => <div className="rule-row" key={category.category}><b>{category.category}</b><span>{Number(category.spent).toFixed(0)} ₽ · {Number(category.sharePercent).toFixed(0)}%</span><p>{category.purchaseCount} покупок · средний чек {Number(category.averageCheck).toFixed(0)} ₽{category.lastPurchaseAt ? ` · последняя ${new Date(category.lastPurchaseAt).toLocaleDateString()}` : ''}</p></div>) : <p className="muted">После первых покупок здесь появятся любимые категории, и офферы станут точнее.</p>}</div><div className="card"><div className="card-head"><h2>Как начисляются баллы</h2><span>правила</span></div>{programQuery.data?.rewardRules.map((rule) => <div className="rule-row" key={rule.category}><b>{rule.category}</b><span>{Number(rule.cashbackPercent).toFixed(0)}%</span><p>{rule.description}</p></div>)}<p className="muted">{programQuery.data?.redemptionRule}</p></div></div>
    <div className="card"><div className="card-head"><h2>Идеи для следующих покупок</h2><span>recommendations</span></div>{recQuery.data?.map((item) => <div className="note" key={item.title}><b>{item.title}</b><p>{item.description}</p></div>)}</div>
  </section>;
}
