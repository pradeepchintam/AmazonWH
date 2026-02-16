import api from './api';
import { DC, Sku, Vendor, Carrier, Location, Page } from '../types';

export const dcService = {
  getAll: () => api.get<DC[]>('/dcs').then(r => r.data),
  getById: (id: number) => api.get<DC>(`/dcs/${id}`).then(r => r.data),
  create: (dc: Partial<DC>) => api.post<DC>('/dcs', dc).then(r => r.data),
  update: (id: number, dc: Partial<DC>) => api.put<DC>(`/dcs/${id}`, dc).then(r => r.data),
  delete: (id: number) => api.delete(`/dcs/${id}`),
};

export const skuService = {
  getAll: (params?: any) => api.get<Page<Sku>>('/skus', { params }).then(r => r.data),
  getById: (id: number) => api.get<Sku>(`/skus/${id}`).then(r => r.data),
  create: (sku: Partial<Sku>) => api.post<Sku>('/skus', sku).then(r => r.data),
  update: (id: number, sku: Partial<Sku>) => api.put<Sku>(`/skus/${id}`, sku).then(r => r.data),
  delete: (id: number) => api.delete(`/skus/${id}`),
};

export const vendorService = {
  getAll: (params?: any) => api.get<Page<Vendor>>('/vendors', { params }).then(r => r.data),
  getById: (id: number) => api.get<Vendor>(`/vendors/${id}`).then(r => r.data),
  create: (v: Partial<Vendor>) => api.post<Vendor>('/vendors', v).then(r => r.data),
  update: (id: number, v: Partial<Vendor>) => api.put<Vendor>(`/vendors/${id}`, v).then(r => r.data),
  delete: (id: number) => api.delete(`/vendors/${id}`),
};

export const carrierService = {
  getAll: (params?: any) => api.get<Page<Carrier>>('/carriers', { params }).then(r => r.data),
  getById: (id: number) => api.get<Carrier>(`/carriers/${id}`).then(r => r.data),
  create: (c: Partial<Carrier>) => api.post<Carrier>('/carriers', c).then(r => r.data),
  update: (id: number, c: Partial<Carrier>) => api.put<Carrier>(`/carriers/${id}`, c).then(r => r.data),
  delete: (id: number) => api.delete(`/carriers/${id}`),
};

export const locationService = {
  getAll: (params?: any) => api.get<Page<Location>>('/locations', { params }).then(r => r.data),
  getById: (id: number) => api.get<Location>(`/locations/${id}`).then(r => r.data),
  create: (l: Partial<Location>) => api.post<Location>('/locations', l).then(r => r.data),
  update: (id: number, l: Partial<Location>) => api.put<Location>(`/locations/${id}`, l).then(r => r.data),
  delete: (id: number) => api.delete(`/locations/${id}`),
};
