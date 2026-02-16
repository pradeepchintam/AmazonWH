import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Tabs, Tab, Typography, Button, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Chip, Checkbox, MenuItem, Select, FormControl, InputLabel, Alert, IconButton,
} from '@mui/material';
import { Add, Visibility, RocketLaunch } from '@mui/icons-material';
import { orderService, waveService } from '../services/orderService';
import { skuService, carrierService } from '../services/masterDataService';
import { WmsOrder, Wave } from '../types';

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box mt={2}>{children}</Box> : null;
}

const statusColors: Record<string, any> = {
  CREATED: 'info', ALLOCATED: 'warning', PICKING: 'secondary', PICKED: 'success', SHIPPED: 'default', CANCELLED: 'error',
};

function OrdersTab() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [detail, setDetail] = useState<WmsOrder | null>(null);
  const [form, setForm] = useState<any>({ lines: [], priority: 5 });
  const [error, setError] = useState('');

  const { data: orders } = useQuery({ queryKey: ['orders'], queryFn: () => orderService.getAll({ page: 0, size: 50 }) });
  const { data: skus } = useQuery({ queryKey: ['skus'], queryFn: () => skuService.getAll({ page: 0, size: 100 }) });
  const { data: carriers } = useQuery({ queryKey: ['carriers'], queryFn: () => carrierService.getAll({ page: 0, size: 100 }) });

  const createMut = useMutation({
    mutationFn: orderService.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['orders'] }); setOpen(false); setForm({ lines: [], priority: 5 }); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const addLine = () => {
    setForm({ ...form, lines: [...form.lines, { skuId: '', orderedQty: 0, lineNumber: form.lines.length + 1 }] });
  };

  return (
    <Box>
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>New Order</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>Order #</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Customer</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Priority</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Wave</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Lines</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(orders?.content || []).map((o: WmsOrder) => (
              <TableRow key={o.id}>
                <TableCell>{o.orderNumber}</TableCell>
                <TableCell>{o.customerName}</TableCell>
                <TableCell><Chip size="small" label={o.status} color={statusColors[o.status] || 'default'} /></TableCell>
                <TableCell>{o.priority}</TableCell>
                <TableCell>{o.waveNumber || '-'}</TableCell>
                <TableCell>{o.lines?.length || 0}</TableCell>
                <TableCell>
                  <IconButton size="small" onClick={async () => setDetail(await orderService.getById(o.id))}><Visibility fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Create Order</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <TextField fullWidth label="Order Number" margin="dense" required
            value={form.orderNumber || ''} onChange={e => setForm({ ...form, orderNumber: e.target.value })} />
          <TextField fullWidth label="Customer Name" margin="dense"
            value={form.customerName || ''} onChange={e => setForm({ ...form, customerName: e.target.value })} />
          <Box display="flex" gap={1}>
            <TextField fullWidth label="Ship To Address" margin="dense"
              value={form.shipToAddress || ''} onChange={e => setForm({ ...form, shipToAddress: e.target.value })} />
            <TextField label="City" margin="dense"
              value={form.shipToCity || ''} onChange={e => setForm({ ...form, shipToCity: e.target.value })} />
            <TextField label="State" margin="dense" sx={{ width: 100 }}
              value={form.shipToState || ''} onChange={e => setForm({ ...form, shipToState: e.target.value })} />
            <TextField label="ZIP" margin="dense" sx={{ width: 100 }}
              value={form.shipToZip || ''} onChange={e => setForm({ ...form, shipToZip: e.target.value })} />
          </Box>
          <Box display="flex" gap={1}>
            <FormControl fullWidth margin="dense"><InputLabel>Carrier</InputLabel>
              <Select value={form.carrierId || ''} label="Carrier" onChange={e => setForm({ ...form, carrierId: e.target.value })}>
                {(carriers?.content || []).map((c: any) => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
              </Select>
            </FormControl>
            <TextField label="Priority" type="number" margin="dense" sx={{ width: 120 }}
              value={form.priority} onChange={e => setForm({ ...form, priority: +e.target.value })} />
          </Box>
          <Typography variant="subtitle1" mt={2} mb={1}>Lines</Typography>
          {form.lines.map((line: any, i: number) => (
            <Box key={i} display="flex" gap={1} mb={1}>
              <FormControl sx={{ minWidth: 200 }}><InputLabel>SKU</InputLabel>
                <Select value={line.skuId} label="SKU" onChange={e => {
                  const lines = [...form.lines]; lines[i] = { ...line, skuId: e.target.value }; setForm({ ...form, lines });
                }}>
                  {(skus?.content || []).map((s: any) => <MenuItem key={s.id} value={s.id}>{s.code}</MenuItem>)}
                </Select>
              </FormControl>
              <TextField label="Qty" type="number" value={line.orderedQty}
                onChange={e => { const lines = [...form.lines]; lines[i] = { ...line, orderedQty: +e.target.value }; setForm({ ...form, lines }); }} />
            </Box>
          ))}
          <Button size="small" onClick={addLine}>+ Add Line</Button>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => createMut.mutate({ ...form, dcId: 1 })}>Create</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={!!detail} onClose={() => setDetail(null)} maxWidth="md" fullWidth>
        <DialogTitle>Order: {detail?.orderNumber}</DialogTitle>
        <DialogContent>
          <Typography>Customer: {detail?.customerName} | Status: {detail?.status} | Priority: {detail?.priority}</Typography>
          <Table size="small" sx={{ mt: 2 }}>
            <TableHead><TableRow>
              <TableCell>Line</TableCell><TableCell>SKU</TableCell><TableCell>Ordered</TableCell>
              <TableCell>Allocated</TableCell><TableCell>Picked</TableCell><TableCell>Status</TableCell>
            </TableRow></TableHead>
            <TableBody>
              {detail?.lines?.map(l => (
                <TableRow key={l.id}>
                  <TableCell>{l.lineNumber}</TableCell><TableCell>{l.skuCode}</TableCell>
                  <TableCell>{l.orderedQty}</TableCell><TableCell>{l.allocatedQty}</TableCell>
                  <TableCell>{l.pickedQty}</TableCell><TableCell><Chip size="small" label={l.status} /></TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </DialogContent>
        <DialogActions><Button onClick={() => setDetail(null)}>Close</Button></DialogActions>
      </Dialog>
    </Box>
  );
}

function WavesTab() {
  const qc = useQueryClient();
  const [selectedOrders, setSelectedOrders] = useState<number[]>([]);
  const [error, setError] = useState('');

  const { data: orders } = useQuery({ queryKey: ['orders'], queryFn: () => orderService.getAll({ page: 0, size: 100, status: 'CREATED' }) });
  const { data: waves } = useQuery({ queryKey: ['waves'], queryFn: () => waveService.getAll({ page: 0, size: 50 }) });

  const createMut = useMutation({
    mutationFn: () => waveService.create({ dcId: 1, orderIds: selectedOrders }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['waves'] }); qc.invalidateQueries({ queryKey: ['orders'] }); setSelectedOrders([]); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const releaseMut = useMutation({
    mutationFn: waveService.release,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['waves'] }); qc.invalidateQueries({ queryKey: ['orders'] }); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const toggleOrder = (id: number) => {
    setSelectedOrders(prev => prev.includes(id) ? prev.filter(o => o !== id) : [...prev, id]);
  };

  return (
    <Box>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Typography variant="h6" mb={1}>Waves</Typography>
      <TableContainer component={Paper} sx={{ mb: 3 }}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>Wave #</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Orders</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Released</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(waves?.content || []).map((w: Wave) => (
              <TableRow key={w.id}>
                <TableCell>{w.waveNumber}</TableCell>
                <TableCell><Chip size="small" label={w.status} color={w.status === 'RELEASED' ? 'success' : 'info'} /></TableCell>
                <TableCell>{w.orderCount ?? 0}</TableCell>
                <TableCell>{w.releasedAt || '-'}</TableCell>
                <TableCell>
                  {w.status === 'CREATED' && <Button size="small" color="warning" startIcon={<RocketLaunch />}
                    onClick={() => releaseMut.mutate(w.id)}>Release</Button>}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Typography variant="h6" mb={1}>Available Orders (select to create wave)</Typography>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell padding="checkbox" />
            <TableCell sx={{ fontWeight: 'bold' }}>Order #</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Customer</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Priority</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Lines</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(orders?.content || []).filter((o: WmsOrder) => !o.waveId).map((o: WmsOrder) => (
              <TableRow key={o.id} selected={selectedOrders.includes(o.id)}>
                <TableCell padding="checkbox">
                  <Checkbox checked={selectedOrders.includes(o.id)} onChange={() => toggleOrder(o.id)} />
                </TableCell>
                <TableCell>{o.orderNumber}</TableCell>
                <TableCell>{o.customerName}</TableCell>
                <TableCell>{o.priority}</TableCell>
                <TableCell>{o.lines?.length || 0}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {selectedOrders.length > 0 && (
        <Box mt={2}>
          <Button variant="contained" onClick={() => createMut.mutate()}>
            Create Wave ({selectedOrders.length} orders)
          </Button>
        </Box>
      )}
    </Box>
  );
}

export default function OrdersPage() {
  const [tab, setTab] = useState(0);
  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Orders & Waves</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)}>
        <Tab label="Orders" /><Tab label="Wave Planning" />
      </Tabs>
      <TabPanel value={tab} index={0}><OrdersTab /></TabPanel>
      <TabPanel value={tab} index={1}><WavesTab /></TabPanel>
    </Box>
  );
}
