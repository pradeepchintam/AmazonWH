import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, Chip, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Alert,
} from '@mui/material';
import { PlayArrow, CheckCircle, Warning } from '@mui/icons-material';
import { pickingService } from '../services/pickingService';
import { PickTask } from '../types';

const statusColors: Record<string, any> = {
  PENDING: 'default', ASSIGNED: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success', SHORT: 'error',
};

export default function PickingPage() {
  const qc = useQueryClient();
  const [completeOpen, setCompleteOpen] = useState(false);
  const [selected, setSelected] = useState<PickTask | null>(null);
  const [qtyPicked, setQtyPicked] = useState(0);
  const [error, setError] = useState('');

  const { data } = useQuery({ queryKey: ['pick-tasks'], queryFn: () => pickingService.getAll({ page: 0, size: 100 }) });

  const assignMut = useMutation({
    mutationFn: ({ id, userId }: { id: number; userId: number }) => pickingService.assign(id, userId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['pick-tasks'] }),
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const startMut = useMutation({
    mutationFn: (id: number) => pickingService.start(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['pick-tasks'] }),
  });
  const completeMut = useMutation({
    mutationFn: () => pickingService.complete(selected!.id, { qtyPicked }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['pick-tasks'] }); setCompleteOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });
  const shortMut = useMutation({
    mutationFn: () => pickingService.short(selected!.id, { qtyPicked }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['pick-tasks'] }); setCompleteOpen(false); },
    onError: (e: any) => setError(e.response?.data?.message || 'Error'),
  });

  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={2}>Pick Tasks</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>ID</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Wave</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Order</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>SKU</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>From Location</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Pick Seq</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Qty to Pick</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Qty Picked</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Assigned To</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow></TableHead>
          <TableBody>
            {(data?.content || []).map((t: PickTask) => (
              <TableRow key={t.id}>
                <TableCell>{t.id}</TableCell>
                <TableCell>{t.waveNumber}</TableCell>
                <TableCell>{t.orderNumber}</TableCell>
                <TableCell>{t.skuCode}</TableCell>
                <TableCell>{t.fromLocationCode}</TableCell>
                <TableCell>{t.pickSequence}</TableCell>
                <TableCell>{t.qtyToPick}</TableCell>
                <TableCell>{t.qtyPicked}</TableCell>
                <TableCell><Chip size="small" label={t.status} color={statusColors[t.status] || 'default'} /></TableCell>
                <TableCell>{t.assignedToName || '-'}</TableCell>
                <TableCell>
                  {t.status === 'PENDING' && <Button size="small" onClick={() => {
                    const uid = prompt('User ID to assign?');
                    if (uid) assignMut.mutate({ id: t.id, userId: +uid });
                  }}>Assign</Button>}
                  {t.status === 'ASSIGNED' && <Button size="small" startIcon={<PlayArrow />} onClick={() => startMut.mutate(t.id)}>Start</Button>}
                  {t.status === 'IN_PROGRESS' && (
                    <Button size="small" startIcon={<CheckCircle />} color="success" onClick={() => {
                      setSelected(t); setQtyPicked(t.qtyToPick); setCompleteOpen(true);
                    }}>Complete</Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
            {(!data?.content || data.content.length === 0) && (
              <TableRow><TableCell colSpan={11} align="center">No pick tasks. Release a wave to generate picks.</TableCell></TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={completeOpen} onClose={() => setCompleteOpen(false)}>
        <DialogTitle>Complete Pick: {selected?.skuCode}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <Typography variant="body2">From: {selected?.fromLocationCode}</Typography>
          <Typography variant="body2" mb={2}>Required: {selected?.qtyToPick}</Typography>
          <TextField fullWidth label="Qty Picked" type="number" value={qtyPicked}
            onChange={e => setQtyPicked(+e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCompleteOpen(false)}>Cancel</Button>
          <Button color="warning" startIcon={<Warning />} onClick={() => shortMut.mutate()}>Short Pick</Button>
          <Button variant="contained" color="success" onClick={() => completeMut.mutate()}>Complete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
