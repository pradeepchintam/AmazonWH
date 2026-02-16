import api from './api';
import { PickTask, Page } from '../types';

export const pickingService = {
  getAll: (params?: any) => api.get<Page<PickTask>>('/pick-tasks', { params }).then(r => r.data),
  getMyTasks: () => api.get<PickTask[]>('/pick-tasks/my-tasks').then(r => r.data),
  assign: (id: number, userId: number) => api.post<PickTask>(`/pick-tasks/${id}/assign`, { userId }).then(r => r.data),
  start: (id: number) => api.post<PickTask>(`/pick-tasks/${id}/start`).then(r => r.data),
  complete: (id: number, data: { qtyPicked: number }) => api.post<PickTask>(`/pick-tasks/${id}/complete`, data).then(r => r.data),
  short: (id: number, data: { qtyPicked: number }) => api.post<PickTask>(`/pick-tasks/${id}/short`, data).then(r => r.data),
};
