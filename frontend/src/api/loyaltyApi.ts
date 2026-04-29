import { api } from './client';
import type { Balance, Offer, Page, Product, Purchase, Recommendation, TokenResponse } from '../types/domain';

export type RegisterPayload = { fullName: string; email: string; password: string };

export const login = async (email: string, password: string) => (await api.post<TokenResponse>('/auth/login', { email, password })).data;
export const registerCustomer = async (payload: RegisterPayload) => (await api.post<TokenResponse>('/auth/register', payload)).data;
export const products = async (q = '') => (await api.get<Page<Product>>('/catalog/products', { params: { q, size: 20, sort: 'price,asc' } })).data;
export const balance = async () => (await api.get<Balance>('/loyalty/balance')).data;
export const offers = async () => (await api.get<Offer[]>('/loyalty/offers')).data;
export const claimOffer = async (id: number) => (await api.post<Offer>(`/loyalty/offers/${id}/claim`)).data;
export const createPurchase = async (merchantId: number, productId: number) => (await api.post<Purchase>('/purchases', { merchantId, items: [{ productId, quantity: 1 }] })).data;
export const purchases = async () => (await api.get<Page<Purchase>>('/purchases', { params: { size: 10, sort: 'purchasedAt,desc' } })).data;
export const recommendations = async () => (await api.get<Recommendation[]>('/loyalty/recommendations')).data;
