import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimOffer, offers } from '../api/loyaltyApi';

export function OffersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['offers'], queryFn: offers });
  const claim = useMutation({ mutationFn: claimOffer, onSuccess: () => client.invalidateQueries({ queryKey: ['offers'] }) });

  return <section className="page">
    <div className="page-title"><p className="label">Персонализация</p><h1>Ваши предложения</h1></div>
    {claim.isError ? <div className="alert error">Предложение уже активировано или временно недоступно.</div> : null}
    <div className="offers-grid">{query.data?.map((offer) => <article className="offer" key={offer.id}><div className="score">match {offer.personalizationScore}</div><h3>{offer.title}</h3><p>{offer.description}</p><div className="personal-reason">{offer.personalizationReason}</div><div className="offer-meta"><span>{offer.targetCategory}</span><b>+{offer.bonusPoints} баллов</b></div><small>Действует до {offer.validUntil}</small><button className="primary" disabled={claim.isPending} onClick={() => claim.mutate(offer.id)}>Активировать</button></article>)}</div>
  </section>;
}
