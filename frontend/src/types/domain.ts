export type Customer = { id: number; fullName: string; email: string; segment: string; roles: string[]; createdAt: string };
export type TokenResponse = { accessToken: string; refreshToken: string; customer: Customer };
export type Page<T> = { content: T[]; totalElements: number; totalPages: number; number: number; size: number };
export type Product = { id: number; merchantId: number; merchantName: string; name: string; category: string; price: number };
export type Offer = { id: number; title: string; description: string; targetCategory: string; bonusPoints: number; validUntil: string; personalizationScore: number };
export type Balance = { customerId: number; pointsBalance: number; segment: string };
export type Purchase = { id: number; merchantName: string; totalAmount: number; purchasedAt: string };
export type Recommendation = { title: string; description: string; source: string };
