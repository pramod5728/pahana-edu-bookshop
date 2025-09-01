import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export const ProfilePage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        User Profile
      </Typography>
      <Paper sx={{ p: 3 }}>
        <Typography variant="body1">
          User profile management will be implemented here.
        </Typography>
      </Paper>
    </Box>
  );
};