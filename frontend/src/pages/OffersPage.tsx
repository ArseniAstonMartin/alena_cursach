import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimOffer, offers } from '../api/loyaltyApi';

export function OffersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['offers'], queryFn: offers });
  const claim = useMutation({ mutationFn: claimOffer, onSuccess: () => client.invalidateQueries({ queryKey: ['offers'] }) });

  return <section className="page">
    <div className="page-title"><p className="label">Персональные задания</p><h1>Офферы как цели, а не подарки</h1></div>
    <div className="alert info">Система считает рейтинг покупателя и историю покупок. Задание разблокируется, когда рейтинг достаточный; бонус начисляется только после выполнения условия.</div>
    {claim.isError ? <div className="alert error">Задание уже активировано, использовано или пока недоступно.</div> : null}
    <div className="offers-grid">{query.data?.map((offer) => {
      const locked = offer.status === 'AVAILABLE' && offer.personalizationScore < offer.requiredRating;
      return <article className="offer mission" key={offer.id}>
        <div className="offer-top"><div className="score">rating {offer.personalizationScore}</div><span className={`status ${locked ? 'expired' : offer.status.toLowerCase()}`}>{locked ? 'LOCKED' : offer.status}</span></div>
        <h3>{offer.title}</h3>
        <p>{offer.description}</p>
        <div className="mission-box"><b>{offer.missionTitle}</b><span>{offer.conditionSummary}</span><small>{offer.nextStep}</small></div>
        <div className="progress-head"><span>Прогресс</span><b>{offer.progressPercent}%</b></div>
        <div className="progress"><i style={{ width: `${Math.min(100, Math.max(0, offer.progressPercent))}%` }} /></div>
        <div className="mission-metrics"><span>{offer.metricLabel}: {Number(offer.currentValue).toFixed(0)}</span><span>Цель: {Number(offer.targetValue).toFixed(0)}</span><span>Сложность: {offer.difficulty}</span></div>
        <div className="personal-reason"><b>Почему вам:</b> {offer.personalizationReason}</div>
        <div className="personal-reason"><b>Польза бизнесу:</b> {offer.businessGoal}</div>
        <div className="offer-meta"><span>{offer.targetCategory}</span><b>+{offer.bonusPoints} баллов</b></div>
        <small>Действует до {offer.validUntil} · нужно рейтинга {offer.requiredRating}</small>
        <button className="primary" disabled={claim.isPending || offer.status !== 'AVAILABLE' || locked} onClick={() => claim.mutate(offer.id)}>{locked ? 'Недостаточно рейтинга' : offer.status === 'AVAILABLE' ? 'Принять задание' : offer.status === 'CLAIMED' ? 'Задание принято' : 'Выполнено'}</button>
      </article>;
    })}</div>
  </section>;
}
