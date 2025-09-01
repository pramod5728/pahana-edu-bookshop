import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Chip,
  Button,
  Alert,
  CircularProgress,
  Divider,
  Breadcrumbs,
  Link,
  Avatar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  ArrowBack as ArrowBackIcon,
  Person as PersonIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  Home as HomeIcon,
  CalendarToday as CalendarIcon,
  Receipt as ReceiptIcon,
  AccountBox as AccountBoxIcon
} from '@mui/icons-material';
import { useNavigate, useParams, Link as RouterLink } from 'react-router-dom';
import { format } from 'date-fns';
import { useAuthStore } from '../../store/authStore';
import { useNotificationStore } from '../../store/notificationStore';
import { customerService } from '../../services/customerService';
import { Customer, UserRole } from '../../types';
import { APP_ROUTES } from '../../constants';

interface CustomerInfoItemProps {
  icon: React.ReactNode;
  label: string;
  value: string | React.ReactNode;
  secondary?: boolean;
}

const CustomerInfoItem: React.FC<CustomerInfoItemProps> = ({ icon, label, value, secondary = false }) => (
  <ListItem sx={{ px: 0 }}>
    <ListItemIcon sx={{ minWidth: 40 }}>
      {icon}
    </ListItemIcon>
    <ListItemText
      primary={label}
      secondary={value}
      secondaryTypographyProps={{
        color: secondary ? 'text.secondary' : 'text.primary',
        fontWeight: secondary ? 'normal' : 500
      }}
    />
  </ListItem>
);

export const CustomerViewPage: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { hasRole } = useAuthStore();
  const { addNotification } = useNotificationStore();

  // State management
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  // Parse customer ID
  const customerId = id ? parseInt(id, 10) : null;
  
  // Check admin access
  const isAdmin = hasRole(UserRole.ADMIN);

  // Load customer data
  useEffect(() => {
    if (customerId && isAdmin) {
      loadCustomer();
    }
  }, [customerId, isAdmin]);

  const loadCustomer = async () => {
    if (!customerId) return;

    try {
      setLoading(true);
      setError(null);

      const customerData = await customerService.getCustomerById(customerId);
      setCustomer(customerData);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load customer';
      setError(errorMessage);
      addNotification({
        type: 'error',
        title: 'Error',
        message: errorMessage
      });
    } finally {
      setLoading(false);
    }
  };

  // Handlers
  const handleEdit = () => {
    if (customer) {
      navigate(APP_ROUTES.CUSTOMERS_EDIT(customer.id));
    }
  };

  const handleDeleteClick = () => {
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!customer) return;

    try {
      setDeleting(true);
      await customerService.deleteCustomer(customer.id);
      
      addNotification({
        type: 'success',
        title: 'Success',
        message: `Customer "${customer.name}" has been deleted successfully`
      });

      // Navigate back to list
      navigate(APP_ROUTES.CUSTOMERS);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to delete customer';
      addNotification({
        type: 'error',
        title: 'Delete Failed',
        message: errorMessage
      });
    } finally {
      setDeleting(false);
      setDeleteDialogOpen(false);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
  };

  const handleBack = () => {
    navigate(APP_ROUTES.CUSTOMERS);
  };

  // Access control check
  if (!isAdmin) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Access Denied: You need administrator privileges to view customer details.
        </Alert>
      </Box>
    );
  }

  // Loading state
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
        <CircularProgress size={40} />
      </Box>
    );
  }

  // Error state
  if (error || !customer) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert 
          severity="error" 
          action={
            <Button size="small" onClick={handleBack}>
              Back to List
            </Button>
          }
        >
          {error || 'Customer not found'}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header with Breadcrumbs */}
      <Box sx={{ mb: 3 }}>
        <Breadcrumbs>
          <Link component={RouterLink} to={APP_ROUTES.CUSTOMERS} underline="hover">
            Customers
          </Link>
          <Typography color="text.primary">
            {customer.name}
          </Typography>
        </Breadcrumbs>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Button
              variant="outlined"
              startIcon={<ArrowBackIcon />}
              onClick={handleBack}
            >
              Back to List
            </Button>
            <Typography variant="h4" component="h1">
              Customer Details
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button
              variant="outlined"
              startIcon={<EditIcon />}
              onClick={handleEdit}
            >
              Edit
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={handleDeleteClick}
            >
              Delete
            </Button>
          </Box>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Customer Information Card */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              {/* Customer Header */}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                <Avatar sx={{ width: 64, height: 64, bgcolor: 'primary.main' }}>
                  <PersonIcon sx={{ fontSize: 32 }} />
                </Avatar>
                <Box>
                  <Typography variant="h5" component="h2" gutterBottom>
                    {customer.name}
                  </Typography>
                  <Chip
                    label={customer.accountNumber}
                    variant="outlined"
                    size="small"
                    icon={<AccountBoxIcon />}
                  />
                </Box>
              </Box>

              <Divider sx={{ mb: 3 }} />

              {/* Customer Details */}
              <Typography variant="h6" gutterBottom>
                Contact Information
              </Typography>
              <List dense>
                <CustomerInfoItem
                  icon={<PhoneIcon color="action" />}
                  label="Phone Number"
                  value={customer.phone}
                />
                <CustomerInfoItem
                  icon={<EmailIcon color="action" />}
                  label="Email Address"
                  value={customer.email || (
                    <Typography variant="body2" color="text.secondary" fontStyle="italic">
                      No email provided
                    </Typography>
                  )}
                />
                <CustomerInfoItem
                  icon={<HomeIcon color="action" />}
                  label="Address"
                  value={(
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                      {customer.address}
                    </Typography>
                  )}
                />
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Summary Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Account Summary
              </Typography>
              <List dense>
                <CustomerInfoItem
                  icon={<ReceiptIcon color="action" />}
                  label="Total Bills"
                  value={(
                    <Chip
                      label={customer.totalBills}
                      size="small"
                      color={customer.totalBills > 0 ? 'primary' : 'default'}
                      variant="outlined"
                    />
                  )}
                />
                <CustomerInfoItem
                  icon={<CalendarIcon color="action" />}
                  label="Account Created"
                  value={format(new Date(customer.createdAt), 'PPP')}
                  secondary
                />
                {customer.updatedAt !== customer.createdAt && (
                  <CustomerInfoItem
                    icon={<CalendarIcon color="action" />}
                    label="Last Updated"
                    value={format(new Date(customer.updatedAt), 'PPP')}
                    secondary
                  />
                )}
              </List>

              {customer.totalBills > 0 && (
                <>
                  <Divider sx={{ my: 2 }} />
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<ReceiptIcon />}
                    onClick={() => {
                      // Navigate to bills filtered by this customer
                      navigate(`${APP_ROUTES.BILLS}?customerId=${customer.id}`);
                    }}
                  >
                    View Bills
                  </Button>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Additional Information */}
        <Grid item xs={12}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Account Status
              </Typography>
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Chip
                  label="Active Customer"
                  color="success"
                  variant="outlined"
                />
                {customer.totalBills === 0 && (
                  <Chip
                    label="No Bills Yet"
                    color="default"
                    variant="outlined"
                  />
                )}
                {customer.totalBills > 5 && (
                  <Chip
                    label="Regular Customer"
                    color="primary"
                    variant="outlined"
                  />
                )}
              </Box>
              
              {customer.totalBills === 0 && (
                <Alert severity="info" sx={{ mt: 2 }}>
                  This customer hasn't placed any bills yet. You can create a new bill for them from the Bills section.
                </Alert>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteCancel}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete customer "{customer.name}" 
            (Account: {customer.accountNumber})?
            
            {customer.totalBills > 0 && (
              <>
                <br /><br />
                <strong>Warning:</strong> This customer has {customer.totalBills} existing bills. 
                The delete operation may fail if bills exist.
              </>
            )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} disabled={deleting}>
            Cancel
          </Button>
          <Button
            onClick={handleDeleteConfirm}
            color="error"
            variant="contained"
            disabled={deleting}
            startIcon={deleting ? <CircularProgress size={16} /> : <DeleteIcon />}
          >
            {deleting ? 'Deleting...' : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};