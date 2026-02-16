import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Tabs, Tab, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Chip, MenuItem, Select, FormControl, InputLabel, Alert,
} from '@mui/material';
import { SwapHoriz, Tune, CompareArrows } from '@mui/icons-material';
import { inventoryService, cycleCountService } from '../services/inventoryService';
import { Inventory, CycleCount } from '../types';

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box mt={2}>{children}</Box> : null;
}

const statusColors: Record<string, any> = {
  AVAILABLE: 'success', ALLOCATED: 'warning', DAMAGED: 'error', HOLD: 'default', IN_TRANSIT: 'info',
};

function InventoryBrowser() {
  const qc = useQueryClient();
  const [filters, setFilters] = useState<any>({ page: 0, size: 50 });
  const [adjustOpen, setAdjustOpen] = useState(false);
  const [statusOpen, setStatusOpen] = useState(false);
  const [transferOpen, setTransferOpen] = useState(false);
  const [selected, setSelected] = useState<Inventory | null>(null);
  const [adjustForm, setAdjustForm] = useState({ qty: 0, reason: '' });
  const [statusForm, setStatusForm] = useState({ newStatus: '' });
  const [transferForm, setTransferForm] = useState({ toLocationId: 0, qty: 0 });
  const [error, setError] = useState('');

  const { data } = useQuery({ queryKey: ['inventory', filters], queryFn: () => inventoryService.search(filters) });

  const adjustMut = useMutation({
    mutationFn: () => inventoryService.adjust(selected!.id, adjustForm),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); setAdjustOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const statusMut = useMutation({
    mutationFn: () => inventoryService.changeStatus(selected!.id, statusForm),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); setStatusOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const transferMut = useMutation({
    mutationFn: () => inventoryService.transfer(selected!.id, transferForm),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); setTransferOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  return (
    <Box>
      <Box display="flex" gap={2} mb={2}>
        <TextField size="small" label="SKU" value={filters.skuCode || ''}
          onChange={e => setFilters({ ...filters, skuCode: e.target.value })} />
        <TextField size="small" label="Location" value={filters.locationCode || ''}
          onChange={e => setFilters({ ...filters, locationCode: e.target.value })} />
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel>Status</InputLabel>
          <Select value={filters.status || ''} label="Status" onChange={e => setFilters({ ...filters, status: e.target.value })}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="AVAILABLE">Available</MenuItem>
            <MenuItem value="ALLOCATED">Allocated</MenuItem>
            <MenuItem value="DAMAGED">Damaged</MenuItem>
            <MenuItem value="HOLD">Hold</MenuItem>
          </Select>
        </FormControl>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>SKU</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Description</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Location</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Lot</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>On Hand</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Allocated</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Available</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(data?.content || []).map((inv: Inventory) => (
              <TableRow key={inv.id}>
                <TableCell>{inv.skuCode}</TableCell>
                <TableCell>{inv.skuDescription}</TableCell>
                <TableCell>{inv.locationCode}</TableCell>
                <TableCell><Chip size="small" label={inv.status} color={statusColors[inv.status] || 'default'} /></TableCell>
                <TableCell>{inv.lotNumber || '-'}</TableCell>
                <TableCell>{inv.qtyOnHand}</TableCell>
                <TableCell>{inv.qtyAllocated}</TableCell>
                <TableCell>{inv.qtyOnHand - inv.qtyAllocated}</TableCell>
                <TableCell>
                  <Button size="small" startIcon={<Tune />} onClick={() => { setSelected(inv); setAdjustForm({ qty: 0, reason: '' }); setAdjustOpen(true); }}>Adjust</Button>
                  <Button size="small" startIcon={<SwapHoriz />} onClick={() => { setSelected(inv); setStatusForm({ newStatus: '' }); setStatusOpen(true); }}>Status</Button>
                  <Button size="small" startIcon={<CompareArrows />} onClick={() => { setSelected(inv); setTransferForm({ toLocationId: 0, qty: 0 }); setTransferOpen(true); }}>Transfer</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={adjustOpen} onClose={() => setAdjustOpen(false)}>
        <DialogTitle>Adjust Inventory: {selected?.skuCode}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <Typography variant="body2" mb={1}>Current On Hand: {selected?.qtyOnHand}</Typography>
          <TextField fullWidth label="Adjustment Qty (+/-)" type="number" margin="dense"
            value={adjustForm.qty} onChange={e => setAdjustForm({ ...adjustForm, qty: +e.target.value })} />
          <TextField fullWidth label="Reason" margin="dense" required
            value={adjustForm.reason} onChange={e => setAdjustForm({ ...adjustForm, reason: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAdjustOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => adjustMut.mutate()}>Adjust</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={statusOpen} onClose={() => setStatusOpen(false)}>
        <DialogTitle>Change Status: {selected?.skuCode}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <Typography variant="body2" mb={1}>Current: {selected?.status}</Typography>
          <FormControl fullWidth margin="dense"><InputLabel>New Status</InputLabel>
            <Select value={statusForm.newStatus} label="New Status" onChange={e => setStatusForm({ newStatus: e.target.value })}>
              <MenuItem value="AVAILABLE">Available</MenuItem>
              <MenuItem value="DAMAGED">Damaged</MenuItem>
              <MenuItem value="HOLD">Hold</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStatusOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => statusMut.mutate()}>Change</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={transferOpen} onClose={() => setTransferOpen(false)}>
        <DialogTitle>Transfer: {selected?.skuCode}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <Typography variant="body2" mb={1}>From: {selected?.locationCode} (On Hand: {selected?.qtyOnHand})</Typography>
          <TextField fullWidth label="To Location ID" type="number" margin="dense"
            value={transferForm.toLocationId || ''} onChange={e => setTransferForm({ ...transferForm, toLocationId: +e.target.value })} />
          <TextField fullWidth label="Quantity" type="number" margin="dense"
            value={transferForm.qty} onChange={e => setTransferForm({ ...transferForm, qty: +e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTransferOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => transferMut.mutate()}>Transfer</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

function CycleCountTab() {
  const qc = useQueryClient();
  const [error, setError] = useState('');
  const { data } = useQuery({ queryKey: ['cycle-counts'], queryFn: () => cycleCountService.getAll({ page: 0, size: 50 }) });

  const recordMut = useMutation({
    mutationFn: ({ id, qty }: { id: number; qty: number }) => cycleCountService.record(id, { countedQty: qty }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['cycle-counts'] }),
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const approveMut = useMutation({
    mutationFn: (id: number) => cycleCountService.approve(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['cycle-counts'] }),
  });
  const createMut = useMutation({
    mutationFn: cycleCountService.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['cycle-counts'] }),
  });

  const statusColors: Record<string, any> = { PENDING: 'warning', IN_PROGRESS: 'info', COMPLETED: 'success', APPROVED: 'default' };

  return (
    <Box>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" onClick={() => {
          const locId = prompt('Location ID?');
          if (locId) createMut.mutate({ dcId: 1, locationId: +locId });
        }}>New Cycle Count</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>ID</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Location</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>SKU</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>System Qty</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Counted</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Variance</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(data?.content || []).map((cc: CycleCount) => (
              <TableRow key={cc.id}>
                <TableCell>{cc.id}</TableCell>
                <TableCell>{cc.locationCode}</TableCell>
                <TableCell>{cc.skuCode || 'All'}</TableCell>
                <TableCell><Chip size="small" label={cc.status} color={statusColors[cc.status] || 'default'} /></TableCell>
                <TableCell>{cc.systemQty ?? '-'}</TableCell>
                <TableCell>{cc.countedQty ?? '-'}</TableCell>
                <TableCell>{cc.variance ?? '-'}</TableCell>
                <TableCell>
                  {cc.status === 'PENDING' && <Button size="small" onClick={() => {
                    const qty = prompt('Counted quantity?');
                    if (qty) recordMut.mutate({ id: cc.id, qty: +qty });
                  }}>Count</Button>}
                  {cc.status === 'COMPLETED' && <Button size="small" color="success" onClick={() => approveMut.mutate(cc.id)}>Approve</Button>}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

export default function InventoryPage() {
  const [tab, setTab] = useState(0);
  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Inventory</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)}>
        <Tab label="Browse" /><Tab label="Cycle Counts" />
      </Tabs>
      <TabPanel value={tab} index={0}><InventoryBrowser /></TabPanel>
      <TabPanel value={tab} index={1}><CycleCountTab /></TabPanel>
    </Box>
  );
}
