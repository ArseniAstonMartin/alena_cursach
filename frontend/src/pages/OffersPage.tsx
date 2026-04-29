import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimOffer, offers } from '../api/loyaltyApi';

export function OffersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['offers'], queryFn: offers });
  const claim = useMutation({ mutationFn: claimOffer, onSuccess: () => client.invalidateQueries({ queryKey: ['offers'] }) });
  return <section className="page-stack">
    <div className="section-title"><div><p className="eyebrow">Personalization engine</p><h2>Персональные предложения</h2></div><span className="badge">Strategy scoring</span></div>
    <div className="offer-grid">{query.data?.map(offer => <article className="offer-card" key={offer.id}><div className="score">{offer.personalizationScore}</div><span>{offer.targetCategory}</span><h3>{offer.title}</h3><p>{offer.description}</p><div className="offer-footer"><b>+{offer.bonusPoints} баллов</b><small>до {offer.validUntil}</small></div><button className="wide" disabled={claim.isPending} onClick={() => claim.mutate(offer.id)}>Активировать</button></article>)}</div>
    {claim.isError ? <p className="error-box">Предложение уже активировано или недоступно.</p> : null}
  </section>;
}
