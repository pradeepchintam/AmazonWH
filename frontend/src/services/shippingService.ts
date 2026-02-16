import api from './api';
import { Shipment, Page } from '../types';

export const shippingService = {
  getAll: (params?: any) => api.get<Page<Shipment>>('/shipments', { params }).then(r => r.data),
  getById: (id: number) => api.get<Shipment>(`/shipments/${id}`).then(r => r.data),
  create: (data: any) => api.post<Shipment>('/shipments', data).then(r => r.data),
  addOrder: (id: number, orderId: number) => api.post(`/shipments/${id}/orders/${orderId}`).then(r => r.data),
  ship: (id: number) => api.post<Shipment>(`/shipments/${id}/ship`).then(r => r.data),
};
