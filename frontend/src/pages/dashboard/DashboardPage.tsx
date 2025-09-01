import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Divider,
} from '@mui/material';
import {
  People,
  Inventory,
  Receipt,
  AttachMoney,
  Warning,
  CheckCircle,
  Schedule,
} from '@mui/icons-material';
import { useAuthStore } from '../../store/authStore';

export const DashboardPage: React.FC = () => {
  const { user } = useAuthStore();

  const stats = [
    {
      title: 'Total Customers',
      value: '1,234',
      icon: <People />,
      color: '#1976d2',
      change: '+12%',
    },
    {
      title: 'Items in Stock',
      value: '5,678',
      icon: <Inventory />,
      color: '#2e7d32',
      change: '+5%',
    },
    {
      title: 'Monthly Sales',
      value: '$45,690',
      icon: <AttachMoney />,
      color: '#ed6c02',
      change: '+18%',
    },
    {
      title: 'Pending Bills',
      value: '89',
      icon: <Schedule />,
      color: '#d32f2f',
      change: '-3%',
    },
  ];

  const recentActivities = [
    { text: 'New customer "John Doe" registered', time: '2 hours ago' },
    { text: 'Bill #BILL000123 was paid', time: '3 hours ago' },
    { text: 'Low stock alert for "Physics Textbook"', time: '5 hours ago' },
    { text: 'Bill #BILL000122 created for "Jane Smith"', time: '1 day ago' },
  ];

  const alerts = [
    { text: '5 items are out of stock', type: 'error', icon: <Warning /> },
    { text: '12 items have low stock', type: 'warning', icon: <Warning /> },
    { text: '23 bills are overdue', type: 'error', icon: <Schedule /> },
  ];

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Welcome back, {user?.fullName}!
      </Typography>
      
      <Typography variant="body1" color="text.secondary" gutterBottom sx={{ mb: 3 }}>
        Here's what's happening in your bookshop today.
      </Typography>

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="text.secondary" gutterBottom variant="body2">
                      {stat.title}
                    </Typography>
                    <Typography variant="h4" component="div">
                      {stat.value}
                    </Typography>
                    <Chip
                      label={stat.change}
                      size="small"
                      color={stat.change.startsWith('+') ? 'success' : 'error'}
                      sx={{ mt: 1 }}
                    />
                  </Box>
                  <Box sx={{ color: stat.color }}>
                    {React.cloneElement(stat.icon, { fontSize: 'large' })}
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3}>
        {/* Recent Activities */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Recent Activities
            </Typography>
            <List>
              {recentActivities.map((activity, index) => (
                <React.Fragment key={index}>
                  <ListItem>
                    <ListItemIcon>
                      <CheckCircle color="success" />
                    </ListItemIcon>
                    <ListItemText
                      primary={activity.text}
                      secondary={activity.time}
                    />
                  </ListItem>
                  {index < recentActivities.length - 1 && <Divider component="li" />}
                </React.Fragment>
              ))}
            </List>
          </Paper>
        </Grid>

        {/* Alerts */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Alerts & Notifications
            </Typography>
            <List>
              {alerts.map((alert, index) => (
                <React.Fragment key={index}>
                  <ListItem>
                    <ListItemIcon>
                      {React.cloneElement(alert.icon, { 
                        color: alert.type as any 
                      })}
                    </ListItemIcon>
                    <ListItemText primary={alert.text} />
                  </ListItem>
                  {index < alerts.length - 1 && <Divider component="li" />}
                </React.Fragment>
              ))}
            </List>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};