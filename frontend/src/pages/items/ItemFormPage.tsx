import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const ItemFormPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Item Form
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          Item creation and editing form will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};