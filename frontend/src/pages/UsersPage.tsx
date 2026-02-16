import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Typography, Button, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, IconButton, Alert, Chip, MenuItem, Select, FormControl, InputLabel,
  TablePagination,
} from '@mui/material';
import { Add, Edit, ToggleOn, ToggleOff } from '@mui/icons-material';
import { userService } from '../services/userService';
import { dcService } from '../services/masterDataService';
import { User, DC } from '../types';

const ROLES = ['ADMIN', 'MANAGER', 'RECEIVER', 'PICKER', 'SHIPPER'];

export default function UsersPage() {
  const qc = useQueryClient();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<User | null>(null);
  const [form, setForm] = useState<any>({});
  const [error, setError] = useState('');

  const { data } = useQuery({
    queryKey: ['users', page, size],
    queryFn: () => userService.getAll({ page, size }),
  });
  const { data: dcs } = useQuery({
    queryKey: ['dcs'],
    queryFn: () => dcService.getAll(),
  });

  const dcList: DC[] = Array.isArray(dcs) ? dcs : [];

  const saveMutation = useMutation({
    mutationFn: (d: any) => editing ? userService.update(editing.id, d) : userService.create(d),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['users'] }); handleClose(); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error saving user'),
  });

  const toggleMutation = useMutation({
    mutationFn: (id: number) => userService.toggleActive(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
  });

  const handleClose = () => { setOpen(false); setEditing(null); setForm({}); setError(''); };

  const handleEdit = (user: User) => {
    setEditing(user);
    setForm({
      username: user.username,
      fullName: user.fullName,
      email: user.email || '',
      dcId: user.dcId || '',
      roles: user.roles || [],
      password: '',
    });
    setOpen(true);
  };

  const handleAdd = () => {
    setForm({ username: '', fullName: '', email: '', password: '', dcId: '', roles: ['RECEIVER'] });
    setOpen(true);
  };

  const handleSave = () => {
    const payload = {
      ...form,
      dcId: form.dcId || null,
      password: form.password || undefined,
    };
    if (!payload.password) delete payload.password;
    saveMutation.mutate(payload);
  };

  const users = data?.content || [];

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5" fontWeight="bold">Users</Typography>
        <Button variant="contained" startIcon={<Add />} onClick={handleAdd}>Add User</Button>
      </Box>

      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Username</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Full Name</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Email</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>DC</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Roles</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Active</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user: User) => (
              <TableRow key={user.id}>
                <TableCell>{user.username}</TableCell>
                <TableCell>{user.fullName}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.dcCode || '—'}</TableCell>
                <TableCell>
                  {user.roles.map(r => (
                    <Chip key={r} label={r} size="small" sx={{ mr: 0.5 }} />
                  ))}
                </TableCell>
                <TableCell>
                  <Chip label={user.active ? 'Active' : 'Inactive'} size="small"
                    color={user.active ? 'success' : 'default'} />
                </TableCell>
                <TableCell>
                  <IconButton size="small" onClick={() => handleEdit(user)}><Edit fontSize="small" /></IconButton>
                  <IconButton size="small" onClick={() => toggleMutation.mutate(user.id)}
                    color={user.active ? 'warning' : 'success'}>
                    {user.active ? <ToggleOff fontSize="small" /> : <ToggleOn fontSize="small" />}
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {users.length === 0 && (
              <TableRow><TableCell colSpan={7} align="center">No users found</TableCell></TableRow>
            )}
          </TableBody>
        </Table>
        {data && (
          <TablePagination component="div" count={data.totalElements} page={page}
            onPageChange={(_, p) => setPage(p)} rowsPerPage={size}
            onRowsPerPageChange={e => { setSize(parseInt(e.target.value)); setPage(0); }} />
        )}
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>{editing ? 'Edit User' : 'Create User'}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <TextField fullWidth label="Username" margin="dense" required disabled={!!editing}
            value={form.username || ''} onChange={e => setForm({ ...form, username: e.target.value })} />
          <TextField fullWidth label="Full Name" margin="dense" required
            value={form.fullName || ''} onChange={e => setForm({ ...form, fullName: e.target.value })} />
          <TextField fullWidth label="Email" margin="dense" type="email"
            value={form.email || ''} onChange={e => setForm({ ...form, email: e.target.value })} />
          <TextField fullWidth label={editing ? 'Password (leave blank to keep)' : 'Password'} margin="dense"
            type="password" required={!editing}
            value={form.password || ''} onChange={e => setForm({ ...form, password: e.target.value })} />
          <FormControl fullWidth margin="dense">
            <InputLabel>Role</InputLabel>
            <Select label="Role" value={form.roles?.[0] || ''}
              onChange={e => setForm({ ...form, roles: [e.target.value] })}>
              {ROLES.map(r => <MenuItem key={r} value={r}>{r}</MenuItem>)}
            </Select>
          </FormControl>
          <FormControl fullWidth margin="dense">
            <InputLabel>Distribution Center</InputLabel>
            <Select label="Distribution Center" value={form.dcId || ''}
              onChange={e => setForm({ ...form, dcId: e.target.value })}>
              <MenuItem value="">None</MenuItem>
              {dcList.map((dc: DC) => <MenuItem key={dc.id} value={dc.id}>{dc.code} — {dc.name}</MenuItem>)}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button variant="contained" onClick={handleSave}>Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
