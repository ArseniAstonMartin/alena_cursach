import { useQuery } from '@tanstack/react-query';
import { products } from '../api/loyaltyApi';

export function CatalogPage() {
  const query = useQuery({ queryKey: ['products'], queryFn: () => products() });
  return <section className="card"><h2>Catalog</h2><div className="list">{query.data?.content.map(product => <article key={product.id}><b>{product.name}</b><span>{product.merchantName}</span><strong>{product.price}</strong></article>)}</div></section>;
}
