import { api } from './client';
import type { Balance, Certificate, LoyaltyProgram, Offer, Page, Product, Purchase, PurchaseProfile, Recommendation, Redemption, RewardTransaction, TokenResponse } from '../types/domain';

export type RegisterPayload = { fullName: string; email: string; password: string };

export const login = async (email: string, password: string) => (await api.post<TokenResponse>('/auth/login', { email, password })).data;
export const registerCustomer = async (payload: RegisterPayload) => (await api.post<TokenResponse>('/auth/register', payload)).data;
export const products = async (q = '') => (await api.get<Page<Product>>('/catalog/products', { params: { q, size: 20, sort: 'price,asc' } })).data;
export const balance = async () => (await api.get<Balance>('/loyalty/balance')).data;
export const purchaseProfile = async () => (await api.get<PurchaseProfile>('/loyalty/purchase-profile')).data;
export const program = async () => (await api.get<LoyaltyProgram>('/loyalty/program')).data;
export const offers = async () => (await api.get<Offer[]>('/loyalty/offers')).data;
export const claimOffer = async (id: number) => (await api.post<Offer>(`/loyalty/offers/${id}/claim`)).data;
export const redeemReward = async (points: number, rewardName: string, targetCategory?: string) => (await api.post<Redemption>('/loyalty/redemptions', { points, rewardName, targetCategory })).data;
export const certificates = async () => (await api.get<Certificate[]>('/loyalty/certificates')).data;
export const createPurchase = async (merchantId: number, productId: number, certificateId?: number | null) => (await api.post<Purchase>('/purchases', { merchantId, certificateId, items: [{ productId, quantity: 1 }] })).data;
export const purchases = async () => (await api.get<Page<Purchase>>('/purchases', { params: { size: 10, sort: 'purchasedAt,desc' } })).data;
export const transactions = async () => (await api.get<Page<RewardTransaction>>('/loyalty/transactions', { params: { size: 8, sort: 'createdAt,desc' } })).data;
export const recommendations = async () => (await api.get<Recommendation[]>('/loyalty/recommendations')).data;
