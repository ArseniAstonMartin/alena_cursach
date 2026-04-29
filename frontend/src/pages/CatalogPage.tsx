import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { createPurchase, products, program } from '../api/loyaltyApi';
import { useAuthStore } from '../store/authStore';

export function CatalogPage() {
  const [search, setSearch] = useState('');
  const customer = useAuthStore((state) => state.customer);
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['products', search], queryFn: () => products(search) });
  const programQuery = useQuery({ queryKey: ['program'], queryFn: program });
  const buy = useMutation({ mutationFn: ({ merchantId, productId }: { merchantId: number; productId: number }) => createPurchase(merchantId, productId), onSuccess: () => { client.invalidateQueries({ queryKey: ['balance'] }); client.invalidateQueries({ queryKey: ['purchases'] }); client.invalidateQueries({ queryKey: ['transactions'] }); } });
  const cashbackFor = (category: string) => programQuery.data?.rewardRules.find((rule) => rule.category === category)?.cashbackPercent ?? 5;

  return <section className="page">
    <div className="page-title with-control"><div><p className="label">Каталог</p><h1>Покупки с понятным кэшбэком</h1></div><input className="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Поиск по названию или категории" /></div>
    {!customer ? <div className="alert info">Каталог можно смотреть без входа. Чтобы купить товар и получить баллы, <NavLink to="/login">войдите или зарегистрируйтесь</NavLink>.</div> : null}
    {buy.isSuccess ? <div className="alert success">Покупка создана. Баллы начислены по категории, сегменту и активированным офферам.</div> : null}
    {buy.isError ? <div className="alert error">Не удалось создать покупку. Попробуйте войти заново.</div> : null}
    <div className="catalog-grid">{query.data?.content.map((product) => <article className="product" key={product.id}><div className="product-icon">{product.category[0]}</div><div className="product-body"><span>{product.category} · {Number(cashbackFor(product.category)).toFixed(0)}% cashback</span><h3>{product.name}</h3><p>{product.merchantName}</p></div><div className="earn-preview">≈ {Math.round(Number(product.price) * Number(cashbackFor(product.category)) / 100)} базовых баллов</div><div className="product-bottom"><b>{Number(product.price).toFixed(2)} ₽</b>{customer ? <button className="primary small" disabled={buy.isPending} onClick={() => buy.mutate({ merchantId: product.merchantId, productId: product.id })}>Купить</button> : <NavLink className="secondary-link small" to="/login">Войти</NavLink>}</div></article>)}</div>
  </section>;
}
