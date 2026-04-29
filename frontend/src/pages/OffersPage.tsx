import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimOffer, offers } from '../api/loyaltyApi';

export function OffersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['offers'], queryFn: offers });
  const claim = useMutation({ mutationFn: claimOffer, onSuccess: () => client.invalidateQueries({ queryKey: ['offers'] }) });

  return <section className="page">
    <div className="page-title"><p className="label">Персональные задания</p><h1>Офферы на основе истории покупок</h1></div>
    <div className="alert info">Как работает: активируйте оффер, затем купите товар указанной категории. Если категория совпадет, бонус начислится автоматически, а оффер станет USED.</div>
    {claim.isError ? <div className="alert error">Оффер уже активирован или использован.</div> : null}
    <div className="offers-grid">{query.data?.map((offer) => <article className="offer" key={offer.id}><div className="offer-top"><div className="score">match {offer.personalizationScore}</div><span className={`status ${offer.status.toLowerCase()}`}>{offer.status}</span></div><h3>{offer.title}</h3><p>{offer.description}</p><div className="personal-reason"><b>Почему вам:</b> {offer.personalizationReason}</div><div className="personal-reason"><b>Что дальше:</b> {offer.nextStep}</div><div className="offer-meta"><span>{offer.targetCategory}</span><b>+{offer.bonusPoints} баллов</b></div><small>Действует до {offer.validUntil}</small><button className="primary" disabled={claim.isPending || offer.status !== 'AVAILABLE'} onClick={() => claim.mutate(offer.id)}>{offer.status === 'AVAILABLE' ? 'Активировать задание' : offer.status === 'CLAIMED' ? 'Активировано' : 'Использовано'}</button></article>)}</div>
  </section>;
}
