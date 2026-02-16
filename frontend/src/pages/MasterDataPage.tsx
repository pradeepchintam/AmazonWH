import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Tabs, Tab, Typography, Button, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, IconButton, Alert,
} from '@mui/material';
import { Add, Edit, Delete } from '@mui/icons-material';
import { skuService, vendorService, carrierService, locationService, dcService } from '../services/masterDataService';

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box mt={2}>{children}</Box> : null;
}

function CrudTable({ queryKey, fetchFn, createFn, updateFn, deleteFn, columns, formFields }: {
  queryKey: string; fetchFn: (p?: any) => Promise<any>; createFn: (d: any) => Promise<any>;
  updateFn: (id: number, d: any) => Promise<any>; deleteFn: (id: number) => Promise<any>;
  columns: { key: string; label: string }[]; formFields: { key: string; label: string; required?: boolean }[];
}) {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<any>(null);
  const [form, setForm] = useState<any>({});
  const [error, setError] = useState('');

  const { data } = useQuery({ queryKey: [queryKey], queryFn: () => fetchFn({ page: 0, size: 100 }) });
  const items = data?.content || data || [];

  const saveMutation = useMutation({
    mutationFn: (d: any) => editing ? updateFn(editing.id, d) : createFn(d),
    onSuccess: () => { qc.invalidateQueries({ queryKey: [queryKey] }); handleClose(); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error saving'),
  });
  const delMutation = useMutation({
    mutationFn: deleteFn,
    onSuccess: () => qc.invalidateQueries({ queryKey: [queryKey] }),
  });

  const handleClose = () => { setOpen(false); setEditing(null); setForm({}); setError(''); };
  const handleEdit = (item: any) => { setEditing(item); setForm(item); setOpen(true); };
  const handleSave = () => saveMutation.mutate(form);

  return (
    <Box>
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>Add New</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            {columns.map(c => <TableCell key={c.key} sx={{ fontWeight: 'bold' }}>{c.label}</TableCell>)}
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {items.map((item: any) => (
              <TableRow key={item.id}>
                {columns.map(c => <TableCell key={c.key}>{String(item[c.key] ?? '')}</TableCell>)}
                <TableCell>
                  <IconButton size="small" onClick={() => handleEdit(item)}><Edit fontSize="small" /></IconButton>
                  <IconButton size="small" color="error" onClick={() => { if (confirm('Delete?')) delMutation.mutate(item.id); }}><Delete fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
            {items.length === 0 && <TableRow><TableCell colSpan={columns.length + 1} align="center">No data</TableCell></TableRow>}
          </TableBody>
        </Table>
      </TableContainer>
      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>{editing ? 'Edit' : 'Create'}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {formFields.map(f => (
            <TextField key={f.key} fullWidth label={f.label} margin="dense" required={f.required}
              value={form[f.key] || ''} onChange={e => setForm({ ...form, [f.key]: e.target.value })} />
          ))}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button variant="contained" onClick={handleSave}>Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default function MasterDataPage() {
  const [tab, setTab] = useState(0);

  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Master Data</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)}>
        <Tab label="SKUs" /><Tab label="Vendors" /><Tab label="Carriers" /><Tab label="Locations" /><Tab label="DCs" />
      </Tabs>
      <TabPanel value={tab} index={0}>
        <CrudTable queryKey="skus" fetchFn={skuService.getAll} createFn={skuService.create}
          updateFn={skuService.update} deleteFn={skuService.delete}
          columns={[{ key: 'code', label: 'Code' }, { key: 'description', label: 'Description' }, { key: 'uom', label: 'UOM' }, { key: 'active', label: 'Active' }]}
          formFields={[{ key: 'code', label: 'Code', required: true }, { key: 'description', label: 'Description', required: true }, { key: 'uom', label: 'UOM' }, { key: 'weight', label: 'Weight' }]} />
      </TabPanel>
      <TabPanel value={tab} index={1}>
        <CrudTable queryKey="vendors" fetchFn={vendorService.getAll} createFn={vendorService.create}
          updateFn={vendorService.update} deleteFn={vendorService.delete}
          columns={[{ key: 'code', label: 'Code' }, { key: 'name', label: 'Name' }, { key: 'contactName', label: 'Contact' }, { key: 'email', label: 'Email' }]}
          formFields={[{ key: 'code', label: 'Code', required: true }, { key: 'name', label: 'Name', required: true }, { key: 'contactName', label: 'Contact Name' }, { key: 'email', label: 'Email' }, { key: 'phone', label: 'Phone' }]} />
      </TabPanel>
      <TabPanel value={tab} index={2}>
        <CrudTable queryKey="carriers" fetchFn={carrierService.getAll} createFn={carrierService.create}
          updateFn={carrierService.update} deleteFn={carrierService.delete}
          columns={[{ key: 'code', label: 'Code' }, { key: 'name', label: 'Name' }, { key: 'scac', label: 'SCAC' }]}
          formFields={[{ key: 'code', label: 'Code', required: true }, { key: 'name', label: 'Name', required: true }, { key: 'scac', label: 'SCAC' }, { key: 'contactName', label: 'Contact' }, { key: 'phone', label: 'Phone' }]} />
      </TabPanel>
      <TabPanel value={tab} index={3}>
        <CrudTable queryKey="locations" fetchFn={locationService.getAll} createFn={locationService.create}
          updateFn={locationService.update} deleteFn={locationService.delete}
          columns={[{ key: 'code', label: 'Code' }, { key: 'zone', label: 'Zone' }, { key: 'locType', label: 'Type' }, { key: 'pickSequence', label: 'Pick Seq' }]}
          formFields={[{ key: 'code', label: 'Code', required: true }, { key: 'zone', label: 'Zone', required: true }, { key: 'aisle', label: 'Aisle' }, { key: 'bay', label: 'Bay' }, { key: 'level', label: 'Level' }, { key: 'locType', label: 'Type' }]} />
      </TabPanel>
      <TabPanel value={tab} index={4}>
        <CrudTable queryKey="dcs" fetchFn={dcService.getAll} createFn={dcService.create}
          updateFn={dcService.update} deleteFn={dcService.delete}
          columns={[{ key: 'code', label: 'Code' }, { key: 'name', label: 'Name' }, { key: 'city', label: 'City' }, { key: 'state', label: 'State' }]}
          formFields={[{ key: 'code', label: 'Code', required: true }, { key: 'name', label: 'Name', required: true }, { key: 'address', label: 'Address' }, { key: 'city', label: 'City' }, { key: 'state', label: 'State' }, { key: 'zip', label: 'ZIP' }]} />
      </TabPanel>
    </Box>
  );
}
