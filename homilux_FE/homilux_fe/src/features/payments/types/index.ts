// src/features/payments/types/index.ts
import type { components } from '@/types/api';

export type PaymentResponse = components['schemas']['PaymentResponse'];
export type PaymentRequest = components['schemas']['PaymentRequest'];

export type PaymentMethod = NonNullable<PaymentResponse['paymentMethod']>;