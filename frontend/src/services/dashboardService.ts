import api from './api';
import { DashboardKpis } from '../types';

export const dashboardService = {
  getKpis: () => api.get<DashboardKpis>('/dashboard/kpis').then(r => r.data),
  getInventoryByStatus: () => api.get<any[]>('/dashboard/inventory-by-status').then(r => r.data),
  getInboundTrend: () => api.get<any[]>('/dashboard/inbound-trend').then(r => r.data),
  getPickPerformance: () => api.get<any[]>('/dashboard/pick-performance').then(r => r.data),
};
