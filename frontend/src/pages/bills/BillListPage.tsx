import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const BillListPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Billing Management
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          Bill list and management features will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};