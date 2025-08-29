// src/lib/utils/constants.ts
import type { components } from '@/types/api';

type BookingStatus = NonNullable<components['schemas']['BookingResponse']['status']>;
type PaymentMethod = NonNullable<components['schemas']['PaymentResponse']['paymentMethod']>;
type LocationType = NonNullable<components['schemas']['BookingResponse']['locationType']>;
type ServiceType = NonNullable<components['schemas']['RentalServiceResponse']['type']>;

export const BOOKING_STATUS: Record<BookingStatus, { label: string; color: string }> = {
  PENDING: { label: 'Pending', color: 'bg-yellow-500' },
  CONFIRMED: { label: 'Confirmed', color: 'bg-blue-500' },
  COMPLETED: { label: 'Completed', color: 'bg-green-500' },
  CANCELLED: { label: 'Cancelled', color: 'bg-red-500' },
};

export const PAYMENT_METHOD: Record<PaymentMethod, string> = {
  CASH: 'Cash',
  BANK_TRANSFER: 'Bank Transfer',
  THIRD_PARTY_GATEWAY: 'VNPay',
};

export const LOCATION_TYPE: Record<LocationType, string> = {
  RESTAURANT: 'Restaurant',
  EXTERNAL: 'External Location',
};

export const SERVICE_TYPE: Record<ServiceType, string> = {
  PACKAGE: 'Package',
  EQUIPMENT: 'Equipment',
  PERSONNEL: 'Personnel',
  OTHER: 'Other',
};