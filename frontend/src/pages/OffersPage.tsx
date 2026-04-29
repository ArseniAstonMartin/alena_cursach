import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimOffer, offers } from '../api/loyaltyApi';

export function OffersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['offers'], queryFn: offers });
  const claim = useMutation({ mutationFn: claimOffer, onSuccess: () => client.invalidateQueries({ queryKey: ['offers'] }) });
  return <section className="card"><h2>Personalized offers</h2><div className="list">{query.data?.map(offer => <article key={offer.id}><b>{offer.title}</b><p>{offer.description}</p><span>Score: {offer.personalizationScore} | Bonus: {offer.bonusPoints}</span><button onClick={() => claim.mutate(offer.id)}>Claim</button></article>)}</div></section>;
}
