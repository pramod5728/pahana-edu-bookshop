import React from 'react';
import { Snackbar, Alert, AlertTitle, Box, IconButton } from '@mui/material';
import { Close } from '@mui/icons-material';
import { useNotificationStore } from '../../store/notificationStore';

export const NotificationContainer: React.FC = () => {
  const { notifications, removeNotification } = useNotificationStore();

  return (
    <Box
      sx={{
        position: 'fixed',
        top: 80,
        right: 16,
        zIndex: 9999,
        display: 'flex',
        flexDirection: 'column',
        gap: 1,
        maxWidth: 400,
      }}
    >
      {notifications.map((notification) => (
        <Snackbar
          key={notification.id}
          open={true}
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
          sx={{ position: 'relative' }}
        >
          <Alert
            severity={notification.type}
            action={
              <IconButton
                size="small"
                aria-label="close"
                color="inherit"
                onClick={() => removeNotification(notification.id)}
              >
                <Close fontSize="small" />
              </IconButton>
            }
            sx={{ width: '100%' }}
          >
            <AlertTitle>{notification.title}</AlertTitle>
            {notification.message}
          </Alert>
        </Snackbar>
      ))}
    </Box>
  );
};