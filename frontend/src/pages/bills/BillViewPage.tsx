import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const BillViewPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Bill Details
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          Bill details view will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};