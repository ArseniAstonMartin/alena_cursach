import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { adjustAdminUserPoints, adminCategories, adminMerchants, adminProducts, adminStats, adminUsers, createAdminCategory, createAdminProduct, deleteAdminCategory, deleteAdminProduct, updateAdminCategory, updateAdminProduct, updateAdminUserSegment } from '../api/loyaltyApi';
import type { AdminCategory, AdminProduct, CategoryPayload, ProductPayload } from '../types/domain';

const emptyProduct: ProductPayload = { merchantId: 1, name: '', category: 'GROCERY', price: 0 };
const emptyCategory: CategoryPayload = { code: '', displayName: '', description: '', cashbackPercent: 5, strategicPriority: 50, missionMultiplier: 1, active: true };
const segments = ['NEWCOMER', 'REGULAR', 'PREMIUM', 'AT_RISK'];

export function AdminPage() {
  const client = useQueryClient();
  const [form, setForm] = useState<ProductPayload>(emptyProduct);
  const [categoryForm, setCategoryForm] = useState<CategoryPayload>(emptyCategory);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editingCategory, setEditingCategory] = useState<string | null>(null);
  const [pointsDraft, setPointsDraft] = useState<Record<number, number>>({});
  const stats = useQuery({ queryKey: ['admin-stats'], queryFn: adminStats });
  const users = useQuery({ queryKey: ['admin-users'], queryFn: adminUsers });
  const merchants = useQuery({ queryKey: ['admin-merchants'], queryFn: adminMerchants });
  const products = useQuery({ queryKey: ['admin-products'], queryFn: adminProducts });
  const categories = useQuery({ queryKey: ['admin-categories'], queryFn: adminCategories });

  const refreshAdmin = () => {
    client.invalidateQueries({ queryKey: ['admin-stats'] });
    client.invalidateQueries({ queryKey: ['admin-products'] });
    client.invalidateQueries({ queryKey: ['admin-users'] });
    client.invalidateQueries({ queryKey: ['admin-categories'] });
    client.invalidateQueries({ queryKey: ['products'] });
    client.invalidateQueries({ queryKey: ['program'] });
  };

  const saveProduct = useMutation({ mutationFn: () => editingId ? updateAdminProduct(editingId, form) : createAdminProduct(form), onSuccess: () => { setForm(emptyProduct); setEditingId(null); refreshAdmin(); } });
  const removeProduct = useMutation({ mutationFn: deleteAdminProduct, onSuccess: refreshAdmin });
  const saveCategory = useMutation({ mutationFn: () => editingCategory ? updateAdminCategory(editingCategory, categoryForm) : createAdminCategory(categoryForm), onSuccess: () => { setCategoryForm(emptyCategory); setEditingCategory(null); refreshAdmin(); } });
  const removeCategory = useMutation({ mutationFn: deleteAdminCategory, onSuccess: refreshAdmin });
  const segmentMutation = useMutation({ mutationFn: ({ id, segment }: { id: number; segment: string }) => updateAdminUserSegment(id, segment), onSuccess: refreshAdmin });
  const pointsMutation = useMutation({ mutationFn: ({ id, points }: { id: number; points: number }) => adjustAdminUserPoints(id, points, 'Admin manual adjustment'), onSuccess: refreshAdmin });

  const activeCategories = categories.data?.filter((category) => category.active) ?? [];
  const editProduct = (product: AdminProduct) => { setEditingId(product.id); setForm({ merchantId: product.merchantId, name: product.name, category: product.category, price: Number(product.price) }); };
  const editCategory = (category: AdminCategory) => { setEditingCategory(category.code); setCategoryForm({ code: category.code, displayName: category.displayName, description: category.description, cashbackPercent: Number(category.cashbackPercent), strategicPriority: category.strategicPriority, missionMultiplier: Number(category.missionMultiplier), active: category.active }); };

  return <section className="page admin-page">
    <div className="page-title"><p className="label">Админ-панель</p><h1>Управление платформой</h1></div>
    <div className="stats admin-stats"><div className="stat-card"><span>Пользователи</span><b>{stats.data?.customers ?? '...'}</b><p>зарегистрировано</p></div><div className="stat-card"><span>Товары</span><b>{stats.data?.products ?? '...'}</b><p>в каталоге</p></div><div className="stat-card"><span>Выручка</span><b>{Number(stats.data?.revenue ?? 0).toFixed(0)} ₽</b><p>{stats.data?.purchases ?? 0} покупок</p></div></div>

    <div className="two-columns admin-columns">
      <div className="card"><div className="card-head"><h2>{editingCategory ? 'Редактировать категорию' : 'Создать категорию'}</h2><span>category strategy</span></div><form className="form" onSubmit={(event) => { event.preventDefault(); saveCategory.mutate(); }}><label>Код<input disabled={Boolean(editingCategory)} value={categoryForm.code} onChange={(event) => setCategoryForm({ ...categoryForm, code: event.target.value.toUpperCase() })} placeholder="ELECTRONICS" /></label><label>Название<input value={categoryForm.displayName} onChange={(event) => setCategoryForm({ ...categoryForm, displayName: event.target.value })} /></label><label>Описание<input value={categoryForm.description} onChange={(event) => setCategoryForm({ ...categoryForm, description: event.target.value })} /></label><div className="form-grid"><label>Кэшбэк %<input type="number" step="0.1" value={categoryForm.cashbackPercent} onChange={(event) => setCategoryForm({ ...categoryForm, cashbackPercent: Number(event.target.value) })} /></label><label>Приоритет<input type="number" value={categoryForm.strategicPriority} onChange={(event) => setCategoryForm({ ...categoryForm, strategicPriority: Number(event.target.value) })} /></label><label>Множитель миссий<input type="number" step="0.05" value={categoryForm.missionMultiplier} onChange={(event) => setCategoryForm({ ...categoryForm, missionMultiplier: Number(event.target.value) })} /></label></div><label className="checkbox"><input type="checkbox" checked={categoryForm.active} onChange={(event) => setCategoryForm({ ...categoryForm, active: event.target.checked })} /> Активна</label><div className="admin-actions"><button className="primary" disabled={saveCategory.isPending}>{editingCategory ? 'Сохранить' : 'Создать'}</button>{editingCategory ? <button type="button" className="secondary" onClick={() => { setEditingCategory(null); setCategoryForm(emptyCategory); }}>Отмена</button> : null}</div></form></div>
      <div className="card"><div className="card-head"><h2>{editingId ? 'Редактировать товар' : 'Добавить товар'}</h2><span>product in category</span></div><form className="form" onSubmit={(event) => { event.preventDefault(); saveProduct.mutate(); }}><label>Мерчант<select value={form.merchantId} onChange={(event) => setForm({ ...form, merchantId: Number(event.target.value) })}>{merchants.data?.map((merchant) => <option key={merchant.id} value={merchant.id}>{merchant.name} · {merchant.category}</option>)}</select></label><label>Название<input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} /></label><label>Категория<select value={form.category} onChange={(event) => setForm({ ...form, category: event.target.value })}>{activeCategories.map((category) => <option key={category.code} value={category.code}>{category.displayName} · {category.code}</option>)}</select></label><label>Цена<input type="number" min="0" step="0.01" value={form.price} onChange={(event) => setForm({ ...form, price: Number(event.target.value) })} /></label><div className="admin-actions"><button className="primary" disabled={saveProduct.isPending}>{editingId ? 'Сохранить' : 'Добавить'}</button>{editingId ? <button type="button" className="secondary" onClick={() => { setEditingId(null); setForm(emptyProduct); }}>Отмена</button> : null}</div></form></div>
    </div>

    <div className="card"><div className="card-head"><h2>Категории</h2><span>{categories.data?.length ?? 0} categories</span></div><div className="admin-table">{categories.data?.map((category) => <div className="admin-row" key={category.code}><div><b>{category.displayName} · {category.code}</b><p>{category.description}</p></div><strong>{Number(category.cashbackPercent).toFixed(1)}%</strong><span>priority {category.strategicPriority} · x{Number(category.missionMultiplier).toFixed(2)} · {category.active ? 'active' : 'disabled'}</span><div className="admin-actions"><button className="secondary small" onClick={() => editCategory(category)}>Изменить</button><button className="secondary small danger" onClick={() => removeCategory.mutate(category.code)}>Удалить</button></div></div>)}</div>{removeCategory.isError ? <div className="alert error">Нельзя удалить категорию, пока в ней есть товары.</div> : null}</div>

    <div className="card"><div className="card-head"><h2>Товары</h2><span>{products.data?.length ?? 0} items</span></div><div className="admin-table">{products.data?.map((product) => <div className="admin-row" key={product.id}><div><b>{product.name}</b><p>{product.merchantName} · {product.category}</p></div><strong>{Number(product.price).toFixed(2)} ₽</strong><div className="admin-actions"><button className="secondary small" onClick={() => editProduct(product)}>Изменить</button><button className="secondary small danger" onClick={() => removeProduct.mutate(product.id)}>Удалить</button></div></div>)}</div>{removeProduct.isError ? <div className="alert error">Нельзя удалить товар, который уже есть в покупках.</div> : null}</div>
    <div className="card"><div className="card-head"><h2>Пользователи</h2><span>customer operations</span></div><div className="admin-table">{users.data?.map((user) => <div className="admin-row user-admin-row" key={user.id}><div><b>{user.fullName}</b><p>{user.email} · {user.purchasesCount} покупок · {Number(user.totalSpent).toFixed(0)} ₽</p></div><strong>{user.pointsBalance} баллов</strong><select value={user.segment} onChange={(event) => segmentMutation.mutate({ id: user.id, segment: event.target.value })}>{segments.map((segment) => <option key={segment} value={segment}>{segment}</option>)}</select><div className="points-tools"><input type="number" placeholder="+/- баллы" value={pointsDraft[user.id] ?? ''} onChange={(event) => setPointsDraft({ ...pointsDraft, [user.id]: Number(event.target.value) })} /><button className="secondary small" onClick={() => pointsMutation.mutate({ id: user.id, points: pointsDraft[user.id] ?? 0 })}>Применить</button></div></div>)}</div></div>
  </section>;
}
