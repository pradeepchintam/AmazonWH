import api from './api';
import { WmsOrder, Wave, Page } from '../types';

export const orderService = {
  getAll: (params?: any) => api.get<Page<WmsOrder>>('/orders', { params }).then(r => r.data),
  getById: (id: number) => api.get<WmsOrder>(`/orders/${id}`).then(r => r.data),
  create: (order: any) => api.post<WmsOrder>('/orders', order).then(r => r.data),
  update: (id: number, order: any) => api.put<WmsOrder>(`/orders/${id}`, order).then(r => r.data),
};

export const waveService = {
  getAll: (params?: any) => api.get<Page<Wave>>('/waves', { params }).then(r => r.data),
  getById: (id: number) => api.get<Wave>(`/waves/${id}`).then(r => r.data),
  create: (data: { dcId: number; orderIds: number[] }) => api.post<Wave>('/waves', data).then(r => r.data),
  release: (id: number) => api.post<Wave>(`/waves/${id}/release`).then(r => r.data),
};
