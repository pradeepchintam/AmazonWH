import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Tabs, Tab, Typography, Button, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Chip, IconButton, Alert, MenuItem, Select, FormControl, InputLabel,
} from '@mui/material';
import { Add, Visibility, CallReceived } from '@mui/icons-material';
import { poService, asnService } from '../services/inboundService';
import { vendorService, skuService, carrierService, locationService } from '../services/masterDataService';
import { PurchaseOrder, Asn, PurchaseOrderLine } from '../types';

const statusColor: Record<string, any> = {
  CREATED: 'info', PARTIAL: 'warning', RECEIVED: 'success', CLOSED: 'default',
  IN_TRANSIT: 'info', ARRIVED: 'warning', RECEIVING: 'secondary', PENDING: 'default',
};

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box mt={2}>{children}</Box> : null;
}

function PoTab() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [detail, setDetail] = useState<PurchaseOrder | null>(null);
  const [form, setForm] = useState<any>({ lines: [] });
  const [error, setError] = useState('');

  const { data: pos } = useQuery({ queryKey: ['pos'], queryFn: () => poService.getAll({ page: 0, size: 50 }) });
  const { data: vendors } = useQuery({ queryKey: ['vendors'], queryFn: () => vendorService.getAll({ page: 0, size: 100 }) });
  const { data: skus } = useQuery({ queryKey: ['skus'], queryFn: () => skuService.getAll({ page: 0, size: 100 }) });

  const createMut = useMutation({
    mutationFn: poService.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['pos'] }); setOpen(false); setForm({ lines: [] }); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const addLine = () => {
    setForm({ ...form, lines: [...form.lines, { skuId: '', expectedQty: 0, lineNumber: form.lines.length + 1 }] });
  };

  return (
    <Box>
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>New PO</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>PO Number</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Vendor</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Expected Date</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Lines</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(pos?.content || []).map((po: PurchaseOrder) => (
              <TableRow key={po.id}>
                <TableCell>{po.poNumber}</TableCell>
                <TableCell>{po.vendorName}</TableCell>
                <TableCell><Chip size="small" label={po.status} color={statusColor[po.status] || 'default'} /></TableCell>
                <TableCell>{po.expectedDate}</TableCell>
                <TableCell>{po.lines?.length || 0}</TableCell>
                <TableCell>
                  <IconButton size="small" onClick={async () => setDetail(await poService.getById(po.id))}><Visibility fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Create Purchase Order</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <TextField fullWidth label="PO Number" margin="dense" required
            value={form.poNumber || ''} onChange={e => setForm({ ...form, poNumber: e.target.value })} />
          <FormControl fullWidth margin="dense">
            <InputLabel>Vendor</InputLabel>
            <Select value={form.vendorId || ''} label="Vendor" onChange={e => setForm({ ...form, vendorId: e.target.value })}>
              {(vendors?.content || []).map((v: any) => <MenuItem key={v.id} value={v.id}>{v.name}</MenuItem>)}
            </Select>
          </FormControl>
          <TextField fullWidth label="Expected Date" type="date" margin="dense" InputLabelProps={{ shrink: true }}
            value={form.expectedDate || ''} onChange={e => setForm({ ...form, expectedDate: e.target.value })} />
          <Typography variant="subtitle1" mt={2} mb={1}>Lines</Typography>
          {form.lines.map((line: any, i: number) => (
            <Box key={i} display="flex" gap={1} mb={1}>
              <FormControl sx={{ minWidth: 200 }}>
                <InputLabel>SKU</InputLabel>
                <Select value={line.skuId} label="SKU" onChange={e => {
                  const lines = [...form.lines]; lines[i] = { ...line, skuId: e.target.value }; setForm({ ...form, lines });
                }}>
                  {(skus?.content || []).map((s: any) => <MenuItem key={s.id} value={s.id}>{s.code}</MenuItem>)}
                </Select>
              </FormControl>
              <TextField label="Qty" type="number" value={line.expectedQty}
                onChange={e => { const lines = [...form.lines]; lines[i] = { ...line, expectedQty: +e.target.value }; setForm({ ...form, lines }); }} />
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
        <DialogTitle>PO: {detail?.poNumber}</DialogTitle>
        <DialogContent>
          <Typography>Vendor: {detail?.vendorName} | Status: {detail?.status}</Typography>
          <Table size="small" sx={{ mt: 2 }}>
            <TableHead><TableRow>
              <TableCell>Line</TableCell><TableCell>SKU</TableCell><TableCell>Expected</TableCell>
              <TableCell>Received</TableCell><TableCell>Status</TableCell>
            </TableRow></TableHead>
            <TableBody>
              {detail?.lines?.map((l: PurchaseOrderLine) => (
                <TableRow key={l.id}>
                  <TableCell>{l.lineNumber}</TableCell><TableCell>{l.skuCode}</TableCell>
                  <TableCell>{l.expectedQty}</TableCell><TableCell>{l.receivedQty}</TableCell>
                  <TableCell><Chip size="small" label={l.status} /></TableCell>
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

function AsnTab() {
  const qc = useQueryClient();
  const [createOpen, setCreateOpen] = useState(false);
  const [receiveOpen, setReceiveOpen] = useState(false);
  const [selectedAsn, setSelectedAsn] = useState<Asn | null>(null);
  const [form, setForm] = useState<any>({ lines: [] });
  const [receiveData, setReceiveData] = useState<any[]>([]);
  const [error, setError] = useState('');

  const { data: asns } = useQuery({ queryKey: ['asns'], queryFn: () => asnService.getAll({ page: 0, size: 50 }) });
  const { data: pos } = useQuery({ queryKey: ['pos'], queryFn: () => poService.getAll({ page: 0, size: 100 }) });
  const { data: carriers } = useQuery({ queryKey: ['carriers'], queryFn: () => carrierService.getAll({ page: 0, size: 100 }) });
  const { data: locations } = useQuery({ queryKey: ['locations'], queryFn: () => locationService.getAll({ page: 0, size: 100 }) });

  const createMut = useMutation({
    mutationFn: asnService.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['asns'] }); setCreateOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const arriveMut = useMutation({
    mutationFn: ({ id, door }: { id: number; door: string }) => asnService.arrive(id, { doorNumber: door }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['asns'] }),
  });

  const receiveMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: any[] }) => asnService.receive(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['asns'] }); qc.invalidateQueries({ queryKey: ['pos'] }); setReceiveOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  const openReceive = async (asn: Asn) => {
    const full = await asnService.getById(asn.id);
    setSelectedAsn(full);
    setReceiveData(full.lines.map((l: any) => ({ asnLineId: l.id, qty: l.expectedQty - l.receivedQty, locationId: '', lotNumber: l.lotNumber || '' })));
    setReceiveOpen(true);
  };

  return (
    <Box>
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" startIcon={<Add />} onClick={() => setCreateOpen(true)}>New ASN</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>ASN Number</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>PO</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Carrier</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Expected</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(asns?.content || []).map((asn: Asn) => (
              <TableRow key={asn.id}>
                <TableCell>{asn.asnNumber}</TableCell>
                <TableCell>{asn.poNumber}</TableCell>
                <TableCell>{asn.carrierName}</TableCell>
                <TableCell><Chip size="small" label={asn.status} color={statusColor[asn.status] || 'default'} /></TableCell>
                <TableCell>{asn.expectedArrival}</TableCell>
                <TableCell>
                  {asn.status === 'CREATED' && <Button size="small" onClick={() => {
                    const door = prompt('Door number?', 'RCV-DOCK-01');
                    if (door) arriveMut.mutate({ id: asn.id, door });
                  }}>Arrive</Button>}
                  {(asn.status === 'ARRIVED' || asn.status === 'RECEIVING') && (
                    <Button size="small" color="success" startIcon={<CallReceived />} onClick={() => openReceive(asn)}>Receive</Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={createOpen} onClose={() => setCreateOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create ASN</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <TextField fullWidth label="ASN Number" margin="dense" required
            value={form.asnNumber || ''} onChange={e => setForm({ ...form, asnNumber: e.target.value })} />
          <FormControl fullWidth margin="dense"><InputLabel>PO</InputLabel>
            <Select value={form.poId || ''} label="PO" onChange={e => setForm({ ...form, poId: e.target.value })}>
              {(pos?.content || []).map((p: any) => <MenuItem key={p.id} value={p.id}>{p.poNumber}</MenuItem>)}
            </Select>
          </FormControl>
          <FormControl fullWidth margin="dense"><InputLabel>Carrier</InputLabel>
            <Select value={form.carrierId || ''} label="Carrier" onChange={e => setForm({ ...form, carrierId: e.target.value })}>
              {(carriers?.content || []).map((c: any) => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
            </Select>
          </FormControl>
          <TextField fullWidth label="Expected Arrival" type="date" margin="dense" InputLabelProps={{ shrink: true }}
            value={form.expectedArrival || ''} onChange={e => setForm({ ...form, expectedArrival: e.target.value })} />
          <TextField fullWidth label="Trailer Number" margin="dense"
            value={form.trailerNumber || ''} onChange={e => setForm({ ...form, trailerNumber: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => createMut.mutate(form)}>Create</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={receiveOpen} onClose={() => setReceiveOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Receive ASN: {selectedAsn?.asnNumber}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <Table size="small">
            <TableHead><TableRow>
              <TableCell>SKU</TableCell><TableCell>Expected</TableCell><TableCell>Already Rcvd</TableCell>
              <TableCell>Qty to Receive</TableCell><TableCell>Location</TableCell><TableCell>Lot</TableCell>
            </TableRow></TableHead>
            <TableBody>
              {selectedAsn?.lines?.map((line, i) => (
                <TableRow key={line.id}>
                  <TableCell>{line.skuCode}</TableCell>
                  <TableCell>{line.expectedQty}</TableCell>
                  <TableCell>{line.receivedQty}</TableCell>
                  <TableCell>
                    <TextField size="small" type="number" value={receiveData[i]?.qty || 0}
                      onChange={e => { const d = [...receiveData]; d[i] = { ...d[i], qty: +e.target.value }; setReceiveData(d); }} />
                  </TableCell>
                  <TableCell>
                    <FormControl size="small" sx={{ minWidth: 150 }}>
                      <Select value={receiveData[i]?.locationId || ''} onChange={e => {
                        const d = [...receiveData]; d[i] = { ...d[i], locationId: e.target.value }; setReceiveData(d);
                      }}>
                        {(locations?.content || []).map((l: any) => <MenuItem key={l.id} value={l.id}>{l.code}</MenuItem>)}
                      </Select>
                    </FormControl>
                  </TableCell>
                  <TableCell>
                    <TextField size="small" value={receiveData[i]?.lotNumber || ''}
                      onChange={e => { const d = [...receiveData]; d[i] = { ...d[i], lotNumber: e.target.value }; setReceiveData(d); }} />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReceiveOpen(false)}>Cancel</Button>
          <Button variant="contained" color="success" onClick={() => receiveMut.mutate({ id: selectedAsn!.id, data: receiveData })}>Receive</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default function InboundPage() {
  const [tab, setTab] = useState(0);
  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Inbound</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)}>
        <Tab label="Purchase Orders" /><Tab label="ASNs" />
      </Tabs>
      <TabPanel value={tab} index={0}><PoTab /></TabPanel>
      <TabPanel value={tab} index={1}><AsnTab /></TabPanel>
    </Box>
  );
}
