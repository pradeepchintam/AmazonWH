import api from './api';
import { User, Page } from '../types';

export const userService = {
  getAll: (params?: { page?: number; size?: number }) =>
    api.get<Page<User>>('/users', { params }).then(r => r.data),
  create: (user: Partial<User> & { password?: string }) =>
    api.post<User>('/users', user).then(r => r.data),
  update: (id: number, user: Partial<User> & { password?: string }) =>
    api.put<User>(`/users/${id}`, user).then(r => r.data),
  toggleActive: (id: number) =>
    api.put<User>(`/users/${id}/toggle-active`).then(r => r.data),
};
