import { useQuery } from '@tanstack/react-query';
import { balance, purchases, recommendations } from '../api/loyaltyApi';

export function DashboardPage() {
  const balanceQuery = useQuery({ queryKey: ['balance'], queryFn: balance });
  const purchasesQuery = useQuery({ queryKey: ['purchases'], queryFn: purchases });
  const recQuery = useQuery({ queryKey: ['recommendations'], queryFn: recommendations });
  return <section className="grid"><article className="card"><h2>Balance</h2><strong>{balanceQuery.data?.pointsBalance ?? '...'}</strong><p>Segment: {balanceQuery.data?.segment}</p></article><article className="card"><h2>Recent purchases</h2>{purchasesQuery.data?.content.map(p => <p key={p.id}>{p.merchantName}: {p.totalAmount}</p>)}</article><article className="card"><h2>Recommendations</h2>{recQuery.data?.map(r => <p key={r.title}><b>{r.title}</b><br />{r.description}</p>)}</article></section>;
}
