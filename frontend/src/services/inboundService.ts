import api from './api';
import { PurchaseOrder, Asn, ReceiveRequest, Page } from '../types';

export const poService = {
  getAll: (params?: any) => api.get<Page<PurchaseOrder>>('/purchase-orders', { params }).then(r => r.data),
  getById: (id: number) => api.get<PurchaseOrder>(`/purchase-orders/${id}`).then(r => r.data),
  create: (po: any) => api.post<PurchaseOrder>('/purchase-orders', po).then(r => r.data),
  update: (id: number, po: any) => api.put<PurchaseOrder>(`/purchase-orders/${id}`, po).then(r => r.data),
};

export const asnService = {
  getAll: (params?: any) => api.get<Page<Asn>>('/asns', { params }).then(r => r.data),
  getById: (id: number) => api.get<Asn>(`/asns/${id}`).then(r => r.data),
  create: (asn: any) => api.post<Asn>('/asns', asn).then(r => r.data),
  arrive: (id: number, data: { doorNumber: string }) => api.post<Asn>(`/asns/${id}/arrive`, data).then(r => r.data),
  receive: (id: number, lines: ReceiveRequest[]) => api.post(`/asns/${id}/receive`, lines).then(r => r.data),
};
