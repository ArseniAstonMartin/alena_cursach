import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { createPurchase, products } from '../api/loyaltyApi';

export function CatalogPage() {
  const [search, setSearch] = useState('');
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['products', search], queryFn: () => products(search) });
  const buy = useMutation({ mutationFn: ({ merchantId, productId }: { merchantId: number; productId: number }) => createPurchase(merchantId, productId), onSuccess: () => { client.invalidateQueries({ queryKey: ['balance'] }); client.invalidateQueries({ queryKey: ['purchases'] }); } });
  return <section className="page-stack">
    <div className="section-title"><div><p className="eyebrow">Smart catalog</p><h2>Каталог партнеров</h2></div><input className="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Фильтр по названию или категории" /></div>
    <div className="product-grid">{query.data?.content.map(product => <article className="product-card" key={product.id}><div className="product-art">{product.category.slice(0, 2)}</div><div><span>{product.category}</span><h3>{product.name}</h3><p>{product.merchantName}</p></div><div className="product-footer"><strong>{Number(product.price).toFixed(2)} ₽</strong><button disabled={buy.isPending} onClick={() => buy.mutate({ merchantId: product.merchantId, productId: product.id })}>{buy.isPending ? '...' : 'Купить'}</button></div></article>)}</div>
    {buy.isSuccess ? <p className="success-box">Покупка создана, баллы начислены.</p> : null}{buy.isError ? <p className="error-box">Не удалось создать покупку.</p> : null}
  </section>;
}
