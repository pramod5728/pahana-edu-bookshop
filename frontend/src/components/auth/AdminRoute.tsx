import React from 'react';
import { Alert, Box } from '@mui/material';
import { useAuthStore } from '../../store/authStore';
import { UserRole } from '../../types';

interface AdminRouteProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

/**
 * Component that protects routes requiring admin access
 * Renders children only if user has ADMIN role
 */
export const AdminRoute: React.FC<AdminRouteProps> = ({ 
  children, 
  fallback 
}) => {
  const { hasRole } = useAuthStore();
  const isAdmin = hasRole(UserRole.ADMIN);

  if (!isAdmin) {
    if (fallback) {
      return <>{fallback}</>;
    }
    
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Access Denied: You need administrator privileges to access this section.
        </Alert>
      </Box>
    );
  }

  return <>{children}</>;
};

export default AdminRoute;