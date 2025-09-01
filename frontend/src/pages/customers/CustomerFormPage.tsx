import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  TextField,
  Button,
  Grid,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Divider,
  Breadcrumbs,
  Link,
  Chip,
  InputAdornment
} from '@mui/material';
import {
  Save as SaveIcon,
  Cancel as CancelIcon,
  Person as PersonIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  Home as HomeIcon,
  ArrowBack as ArrowBackIcon
} from '@mui/icons-material';
import { useNavigate, useParams, Link as RouterLink } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { useNotificationStore } from '../../store/notificationStore';
import { customerService } from '../../services/customerService';
import { Customer, CustomerRequest, UserRole } from '../../types';
import { APP_ROUTES } from '../../constants';

interface FormErrors {
  name?: string;
  address?: string;
  phone?: string;
  email?: string;
}

export const CustomerFormPage: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { hasRole } = useAuthStore();
  const { addNotification } = useNotificationStore();

  // Determine if this is edit mode
  const isEditMode = Boolean(id);
  const customerId = id ? parseInt(id, 10) : null;

  // State management
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [formData, setFormData] = useState<CustomerRequest>({
    name: '',
    address: '',
    phone: '',
    email: ''
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [generalError, setGeneralError] = useState<string | null>(null);

  // Check admin access
  const isAdmin = hasRole(UserRole.ADMIN);

  // Load customer data for editing
  useEffect(() => {
    if (isEditMode && customerId && isAdmin) {
      loadCustomer();
    }
  }, [isEditMode, customerId, isAdmin]);

  const loadCustomer = async () => {
    if (!customerId) return;

    try {
      setLoading(true);
      setGeneralError(null);

      const customerData = await customerService.getCustomerById(customerId);
      setCustomer(customerData);
      setFormData({
        name: customerData.name,
        address: customerData.address,
        phone: customerData.phone,
        email: customerData.email || ''
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load customer';
      setGeneralError(errorMessage);
      addNotification({
        type: 'error',
        title: 'Error',
        message: errorMessage
      });
    } finally {
      setLoading(false);
    }
  };

  // Form handlers
  const handleInputChange = (field: keyof CustomerRequest) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = event.target.value;
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear error for this field when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const validateForm = (): boolean => {
    const validationErrors = customerService.validateCustomerData(formData);
    
    if (validationErrors.length > 0) {
      const errorMap: FormErrors = {};
      validationErrors.forEach(error => {
        if (error.includes('name')) errorMap.name = error;
        else if (error.includes('address')) errorMap.address = error;
        else if (error.includes('phone')) errorMap.phone = error;
        else if (error.includes('email')) errorMap.email = error;
      });
      setErrors(errorMap);
      return false;
    }

    setErrors({});
    return true;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setSaving(true);
      setGeneralError(null);

      let result: Customer;
      if (isEditMode && customerId) {
        result = await customerService.updateCustomer(customerId, formData);
        addNotification({
          type: 'success',
          title: 'Success',
          message: `Customer "${result.name}" has been updated successfully`
        });
      } else {
        result = await customerService.createCustomer(formData);
        addNotification({
          type: 'success',
          title: 'Success',
          message: `Customer "${result.name}" has been created successfully`
        });
      }

      // Navigate back to list or to view page
      navigate(APP_ROUTES.CUSTOMERS_VIEW(result.id));
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 
        `Failed to ${isEditMode ? 'update' : 'create'} customer`;
      setGeneralError(errorMessage);
      addNotification({
        type: 'error',
        title: `${isEditMode ? 'Update' : 'Create'} Failed`,
        message: errorMessage
      });
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (isEditMode && customer) {
      navigate(APP_ROUTES.CUSTOMERS_VIEW(customer.id));
    } else {
      navigate(APP_ROUTES.CUSTOMERS);
    }
  };

  // Access control check
  if (!isAdmin) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Access Denied: You need administrator privileges to manage customers.
        </Alert>
      </Box>
    );
  }

  // Loading state for edit mode
  if (isEditMode && loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
        <CircularProgress size={40} />
      </Box>
    );
  }

  // Error state for edit mode
  if (isEditMode && generalError && !customer) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert 
          severity="error" 
          action={
            <Button size="small" onClick={() => navigate(APP_ROUTES.CUSTOMERS)}>
              Back to List
            </Button>
          }
        >
          {generalError}
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
          {isEditMode && customer && (
            <Link component={RouterLink} to={APP_ROUTES.CUSTOMERS_VIEW(customer.id)} underline="hover">
              {customer.name}
            </Link>
          )}
          <Typography color="text.primary">
            {isEditMode ? 'Edit' : 'New Customer'}
          </Typography>
        </Breadcrumbs>
        
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 2, gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<ArrowBackIcon />}
            onClick={handleCancel}
            disabled={saving}
          >
            Back
          </Button>
          <Typography variant="h4" component="h1">
            {isEditMode ? 'Edit Customer' : 'Add New Customer'}
          </Typography>
          {isEditMode && customer && (
            <Chip
              label={customer.accountNumber}
              variant="outlined"
              size="small"
            />
          )}
        </Box>
      </Box>

      {/* General Error Alert */}
      {generalError && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setGeneralError(null)}>
          {generalError}
        </Alert>
      )}

      {/* Form */}
      <Paper>
        <Card>
          <CardContent sx={{ p: 4 }}>
            <form onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                {/* Customer Name */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Customer Name"
                    value={formData.name}
                    onChange={handleInputChange('name')}
                    error={Boolean(errors.name)}
                    helperText={errors.name || 'Enter customer full name'}
                    required
                    disabled={saving}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <PersonIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>

                {/* Phone */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Phone Number"
                    value={formData.phone}
                    onChange={handleInputChange('phone')}
                    error={Boolean(errors.phone)}
                    helperText={errors.phone || 'Sri Lankan format: +94XXXXXXXXX or 0XXXXXXXXX'}
                    placeholder="+94771234567 or 0771234567"
                    required
                    disabled={saving}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <PhoneIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>

                {/* Email */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Email Address"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    error={Boolean(errors.email)}
                    helperText={errors.email || 'Optional: Customer email address'}
                    placeholder="customer@example.com"
                    disabled={saving}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <EmailIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>

                {/* Address */}
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Address"
                    value={formData.address}
                    onChange={handleInputChange('address')}
                    error={Boolean(errors.address)}
                    helperText={errors.address || 'Complete customer address'}
                    required
                    multiline
                    rows={3}
                    disabled={saving}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start" sx={{ alignSelf: 'flex-start', mt: 1 }}>
                          <HomeIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>

                {/* Form Actions */}
                <Grid item xs={12}>
                  <Divider sx={{ my: 2 }} />
                  <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                    <Button
                      variant="outlined"
                      onClick={handleCancel}
                      disabled={saving}
                      startIcon={<CancelIcon />}
                    >
                      Cancel
                    </Button>
                    <Button
                      type="submit"
                      variant="contained"
                      disabled={saving}
                      startIcon={saving ? <CircularProgress size={16} /> : <SaveIcon />}
                    >
                      {saving ? 'Saving...' : (isEditMode ? 'Update Customer' : 'Create Customer')}
                    </Button>
                  </Box>
                </Grid>
              </Grid>
            </form>
          </CardContent>
        </Card>
      </Paper>
    </Box>
  );
};