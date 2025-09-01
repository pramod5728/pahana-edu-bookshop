import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const ItemViewPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Item Details
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          Item details view will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};