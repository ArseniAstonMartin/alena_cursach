import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { certificates, createPurchase, products, program } from '../api/loyaltyApi';
import { useAuthStore } from '../store/authStore';

export function CatalogPage() {
  const [search, setSearch] = useState('');
  const [selectedCertificateId, setSelectedCertificateId] = useState<number | null>(null);
  const customer = useAuthStore((state) => state.customer);
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['products', search], queryFn: () => products(search) });
  const programQuery = useQuery({ queryKey: ['program'], queryFn: program });
  const certificatesQuery = useQuery({ queryKey: ['certificates'], queryFn: certificates, enabled: Boolean(customer) });
  const activeCertificates = useMemo(() => certificatesQuery.data?.filter((certificate) => certificate.status === 'ACTIVE') ?? [], [certificatesQuery.data]);
  const selectedCertificate = activeCertificates.find((certificate) => certificate.id === selectedCertificateId) ?? null;
  const buy = useMutation({ mutationFn: ({ merchantId, productId }: { merchantId: number; productId: number }) => createPurchase(merchantId, productId, selectedCertificateId), onSuccess: () => { setSelectedCertificateId(null); client.invalidateQueries({ queryKey: ['balance'] }); client.invalidateQueries({ queryKey: ['purchases'] }); client.invalidateQueries({ queryKey: ['transactions'] }); client.invalidateQueries({ queryKey: ['certificates'] }); } });
  const cashbackFor = (category: string) => programQuery.data?.rewardRules.find((rule) => rule.category === category)?.cashbackPercent ?? 5;

  return <section className="page">
    <div className="page-title with-control"><div><p className="label">Каталог</p><h1>Выберите товар и примените сертификат</h1></div><input className="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Поиск по названию или категории" /></div>
    {!customer ? <div className="alert info">Каталог можно смотреть без входа. Чтобы купить товар и получить баллы, <NavLink to="/login">войдите или зарегистрируйтесь</NavLink>.</div> : null}
    {customer ? <div className="card certificate-picker"><div><h2>Сертификат к покупке</h2><p className="muted">Выберите активный сертификат из кошелька. После покупки он станет USED.</p></div><select value={selectedCertificateId ?? ''} onChange={(event) => setSelectedCertificateId(event.target.value ? Number(event.target.value) : null)}><option value="">Без сертификата</option>{activeCertificates.map((certificate) => <option key={certificate.id} value={certificate.id}>{certificate.confirmationCode} · -{Number(certificate.discountAmount).toFixed(0)} ₽</option>)}</select></div> : null}
    {buy.data ? <div className="alert success">Покупка успешна: сумма {Number(buy.data.grossAmount).toFixed(0)} ₽, скидка {Number(buy.data.discountAmount).toFixed(0)} ₽, итог {Number(buy.data.totalAmount).toFixed(0)} ₽. Начисление появится в истории.</div> : null}
    {buy.isError ? <div className="alert error">Не удалось создать покупку. Сертификат мог быть уже использован или истек.</div> : null}
    <div className="catalog-grid">{query.data?.content.map((product) => { const discount = selectedCertificate ? Math.min(Number(product.price), Number(selectedCertificate.discountAmount)) : 0; const total = Math.max(0, Number(product.price) - discount); return <article className="product" key={product.id}><div className="product-icon">{product.category[0]}</div><div className="product-body"><span>{product.category} · {Number(cashbackFor(product.category)).toFixed(0)}% cashback</span><h3>{product.name}</h3><p>{product.merchantName}</p></div><div className="earn-preview">≈ {Math.round(total * Number(cashbackFor(product.category)) / 100)} базовых баллов после скидки</div>{discount > 0 ? <div className="price-breakdown"><span>Цена {Number(product.price).toFixed(0)} ₽</span><span>Сертификат -{discount.toFixed(0)} ₽</span></div> : null}<div className="product-bottom"><b>{total.toFixed(2)} ₽</b>{customer ? <button className="primary small" disabled={buy.isPending} onClick={() => buy.mutate({ merchantId: product.merchantId, productId: product.id })}>Купить</button> : <NavLink className="secondary-link small" to="/login">Войти</NavLink>}</div></article>; })}</div>
  </section>;
}
