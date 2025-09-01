import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const ItemListPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Inventory Management
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          Item list and inventory management features will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};