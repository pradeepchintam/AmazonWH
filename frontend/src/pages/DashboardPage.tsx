import { useQuery } from '@tanstack/react-query';
import { Box, Card, CardContent, Grid, Typography, CircularProgress } from '@mui/material';
import { Inventory2, ShoppingCart, Assignment, LocalShipping, MoveToInbox, TrendingUp } from '@mui/icons-material';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, LineChart, Line, CartesianGrid, Legend } from 'recharts';
import { dashboardService } from '../services/dashboardService';

const COLORS = ['#1976d2', '#f57c00', '#388e3c', '#d32f2f', '#7b1fa2'];

function KpiCard({ title, value, icon, color }: { title: string; value: number | string; icon: React.ReactNode; color: string }) {
  return (
    <Card>
      <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Box sx={{ bgcolor: color, borderRadius: 2, p: 1.5, color: 'white', display: 'flex' }}>{icon}</Box>
        <Box>
          <Typography variant="body2" color="text.secondary">{title}</Typography>
          <Typography variant="h5" fontWeight="bold">{value}</Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default function DashboardPage() {
  const { data: kpis, isLoading: kpiLoading } = useQuery({ queryKey: ['dashboard-kpis'], queryFn: dashboardService.getKpis });
  const { data: invByStatus } = useQuery({ queryKey: ['dashboard-inv-status'], queryFn: dashboardService.getInventoryByStatus });
  const { data: inboundTrend } = useQuery({ queryKey: ['dashboard-inbound'], queryFn: dashboardService.getInboundTrend });
  const { data: pickPerf } = useQuery({ queryKey: ['dashboard-pick-perf'], queryFn: dashboardService.getPickPerformance });

  if (kpiLoading) return <Box display="flex" justifyContent="center" mt={4}><CircularProgress /></Box>;

  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={3}>Dashboard</Typography>
      <Grid container spacing={2} mb={3}>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Total Inventory" value={kpis?.totalInventory ?? 0} icon={<Inventory2 />} color="#1976d2" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Open Orders" value={kpis?.openOrders ?? 0} icon={<ShoppingCart />} color="#f57c00" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Pending Picks" value={kpis?.pendingPicks ?? 0} icon={<Assignment />} color="#d32f2f" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Shipped Today" value={kpis?.shipmentsToday ?? 0} icon={<LocalShipping />} color="#388e3c" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Inbound Today" value={kpis?.inboundToday ?? 0} icon={<MoveToInbox />} color="#7b1fa2" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}><KpiCard title="Pick Rate" value={`${kpis?.pickCompletionRate ?? 0}%`} icon={<TrendingUp />} color="#0097a7" /></Grid>
      </Grid>
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card><CardContent>
            <Typography variant="h6" mb={2}>Inventory by Status</Typography>
            <ResponsiveContainer width="100%" height={250}>
              <PieChart><Pie data={invByStatus || []} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                {(invByStatus || []).map((_: any, i: number) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie><Tooltip /></PieChart>
            </ResponsiveContainer>
          </CardContent></Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card><CardContent>
            <Typography variant="h6" mb={2}>Inbound Trend (7 days)</Typography>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={inboundTrend || []}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" /><YAxis /><Tooltip /><Bar dataKey="qty" fill="#1976d2" /></BarChart>
            </ResponsiveContainer>
          </CardContent></Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card><CardContent>
            <Typography variant="h6" mb={2}>Pick Performance (7 days)</Typography>
            <ResponsiveContainer width="100%" height={250}>
              <LineChart data={pickPerf || []}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" /><YAxis /><Tooltip /><Legend />
                <Line type="monotone" dataKey="completed" stroke="#388e3c" /><Line type="monotone" dataKey="total" stroke="#1976d2" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent></Card>
        </Grid>
      </Grid>
    </Box>
  );
}
