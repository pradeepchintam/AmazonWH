import api from './api';
import { Inventory, CycleCount, Page } from '../types';

export const inventoryService = {
  search: (params?: any) => api.get<Page<Inventory>>('/inventory', { params }).then(r => r.data),
  adjust: (id: number, data: { qty: number; reason: string }) => api.post(`/inventory/${id}/adjust`, data).then(r => r.data),
  changeStatus: (id: number, data: { newStatus: string }) => api.post(`/inventory/${id}/status`, data).then(r => r.data),
  transfer: (id: number, data: { toLocationId: number; qty: number }) => api.post(`/inventory/${id}/transfer`, data).then(r => r.data),
};

export const cycleCountService = {
  getAll: (params?: any) => api.get<Page<CycleCount>>('/cycle-counts', { params }).then(r => r.data),
  create: (data: any) => api.post<CycleCount>('/cycle-counts', data).then(r => r.data),
  record: (id: number, data: { countedQty: number; notes?: string }) => api.post<CycleCount>(`/cycle-counts/${id}/record`, data).then(r => r.data),
  approve: (id: number) => api.post<CycleCount>(`/cycle-counts/${id}/approve`).then(r => r.data),
};
