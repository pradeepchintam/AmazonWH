import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, Chip, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Alert, MenuItem, Select, FormControl, InputLabel,
} from '@mui/material';
import { Add, LocalShipping } from '@mui/icons-material';
import { shippingService } from '../services/shippingService';
import { orderService } from '../services/orderService';
import { carrierService } from '../services/masterDataService';
import { Shipment, WmsOrder } from '../types';

const statusColors: Record<string, any> = {
  CREATED: 'info', LOADING: 'warning', LOADED: 'secondary', SHIPPED: 'success',
};

export default function ShippingPage() {
  const qc = useQueryClient();
  const [createOpen, setCreateOpen] = useState(false);
  const [loadOpen, setLoadOpen] = useState(false);
  const [selectedShipment, setSelectedShipment] = useState<Shipment | null>(null);
  const [form, setForm] = useState<any>({});
  const [error, setError] = useState('');

  const { data: shipments } = useQuery({ queryKey: ['shipments'], queryFn: () => shippingService.getAll({ page: 0, size: 50 }) });
  const { data: carriers } = useQuery({ queryKey: ['carriers'], queryFn: () => carrierService.getAll({ page: 0, size: 100 }) });
  const { data: pickedOrders } = useQuery({ queryKey: ['picked-orders'], queryFn: () => orderService.getAll({ page: 0, size: 100, status: 'PICKED' }) });

  const createMut = useMutation({
    mutationFn: shippingService.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['shipments'] }); setCreateOpen(false); setForm({}); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const addOrderMut = useMutation({
    mutationFn: ({ shipId, orderId }: { shipId: number; orderId: number }) => shippingService.addOrder(shipId, orderId),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['shipments'] }); qc.invalidateQueries({ queryKey: ['picked-orders'] }); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const shipMut = useMutation({
    mutationFn: shippingService.ship,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['shipments'] }); qc.invalidateQueries({ queryKey: ['picked-orders'] }); setLoadOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const openLoad = async (shipment: Shipment) => {
    const full = await shippingService.getById(shipment.id);
    setSelectedShipment(full);
    setLoadOpen(true);
  };

  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Shipping</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" startIcon={<Add />} onClick={() => setCreateOpen(true)}>New Shipment</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>Shipment #</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Carrier</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>BOL</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Trailer</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Door</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Orders</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(shipments?.content || []).map((s: Shipment) => (
              <TableRow key={s.id}>
                <TableCell>{s.shipmentNumber}</TableCell>
                <TableCell>{s.carrierName}</TableCell>
                <TableCell><Chip size="small" label={s.status} color={statusColors[s.status] || 'default'} /></TableCell>
                <TableCell>{s.bolNumber || '-'}</TableCell>
                <TableCell>{s.trailerNumber || '-'}</TableCell>
                <TableCell>{s.doorNumber || '-'}</TableCell>
                <TableCell>{s.lines?.length || 0}</TableCell>
                <TableCell>
                  {(s.status === 'CREATED' || s.status === 'LOADING') && (
                    <Button size="small" onClick={() => openLoad(s)}>Load</Button>
                  )}
                  {s.status === 'LOADED' && (
                    <Button size="small" color="success" startIcon={<LocalShipping />} onClick={() => shipMut.mutate(s.id)}>Ship</Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={createOpen} onClose={() => setCreateOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Shipment</DialogTitle>
        <DialogContent>
          <TextField fullWidth label="Shipment Number" margin="dense" required
            value={form.shipmentNumber || ''} onChange={e => setForm({ ...form, shipmentNumber: e.target.value })} />
          <FormControl fullWidth margin="dense"><InputLabel>Carrier</InputLabel>
            <Select value={form.carrierId || ''} label="Carrier" onChange={e => setForm({ ...form, carrierId: e.target.value })}>
              {(carriers?.content || []).map((c: any) => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
            </Select>
          </FormControl>
          <TextField fullWidth label="Trailer Number" margin="dense"
            value={form.trailerNumber || ''} onChange={e => setForm({ ...form, trailerNumber: e.target.value })} />
          <TextField fullWidth label="Door Number" margin="dense"
            value={form.doorNumber || ''} onChange={e => setForm({ ...form, doorNumber: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => createMut.mutate({ ...form, dcId: 1 })}>Create</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={loadOpen} onClose={() => setLoadOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Load Shipment: {selectedShipment?.shipmentNumber}</DialogTitle>
        <DialogContent>
          <Typography variant="subtitle1" mb={1}>Loaded Orders:</Typography>
          {selectedShipment?.lines?.map(l => (
            <Chip key={l.id} label={l.orderNumber} sx={{ mr: 1, mb: 1 }} />
          ))}
          {(!selectedShipment?.lines || selectedShipment.lines.length === 0) && (
            <Typography variant="body2" color="text.secondary">No orders loaded yet</Typography>
          )}
          <Typography variant="subtitle1" mt={2} mb={1}>Available Picked Orders:</Typography>
          <Table size="small">
            <TableHead><TableRow>
              <TableCell>Order #</TableCell><TableCell>Customer</TableCell><TableCell>Action</TableCell>
            </TableRow></TableHead>
            <TableBody>
              {(pickedOrders?.content || []).map((o: WmsOrder) => (
                <TableRow key={o.id}>
                  <TableCell>{o.orderNumber}</TableCell>
                  <TableCell>{o.customerName}</TableCell>
                  <TableCell>
                    <Button size="small" variant="outlined" onClick={() => addOrderMut.mutate({ shipId: selectedShipment!.id, orderId: o.id })}>
                      Add to Shipment
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setLoadOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
