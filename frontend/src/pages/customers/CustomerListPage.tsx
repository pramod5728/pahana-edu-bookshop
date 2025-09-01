import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  IconButton,
  Button,
  TextField,
  InputAdornment,
  Chip,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Alert,
  CircularProgress,
  Fab
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
  Refresh as RefreshIcon,
  Person as PersonIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { useAuthStore } from '../../store/authStore';
import { useNotificationStore } from '../../store/notificationStore';
import { customerService, CustomerListParams } from '../../services/customerService';
import { Customer, UserRole } from '../../types';
import { APP_ROUTES } from '../../constants';

interface CustomerRowActionsProps {
  customer: Customer;
  onView: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

const CustomerRowActions: React.FC<CustomerRowActionsProps> = ({
  customer,
  onView,
  onEdit,
  onDelete
}) => {
  return (
    <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
      <Tooltip title="View customer details">
        <Button
          size="small"
          onClick={onView}
          variant="outlined"
          color="warning"
          sx={{
            borderRadius: 0.5,
            minWidth: '32px',
            width: '32px',
            height: '32px',
            padding: 0,
            '&:hover': {
              backgroundColor: 'warning.main',
              borderColor: 'white',
              color: 'white'
            }
          }}
        >
          <ViewIcon fontSize="small" />
        </Button>
      </Tooltip>
      
      <Tooltip title="Edit customer information">
        <Button
          size="small"
          onClick={onEdit}
          variant="outlined"
          color="primary"
          sx={{
            borderRadius: 0.5,
            minWidth: '32px',
            width: '32px',
            height: '32px',
            padding: 0,
            '&:hover': {
              backgroundColor: 'primary.main',
              borderColor: 'white',
              color: 'white'
            }
          }}
        >
          <EditIcon fontSize="small" />
        </Button>
      </Tooltip>
      
      <Tooltip title="Delete customer">
        <Button
          size="small"
          onClick={onDelete}
          variant="outlined"
          color="error"
          sx={{
            borderRadius: 0.5,
            minWidth: '32px',
            width: '32px',
            height: '32px',
            padding: 0,
            '&:hover': {
              backgroundColor: 'error.main',
              borderColor: 'white',
              color: 'white'
            }
          }}
        >
          <DeleteIcon fontSize="small" />
        </Button>
      </Tooltip>
    </Box>
  );
};

export const CustomerListPage: React.FC = () => {
  const navigate = useNavigate();
  const { hasRole } = useAuthStore();
  const { addNotification } = useNotificationStore();

  // State management
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalCount, setTotalCount] = useState(0);
  const [sortBy, setSortBy] = useState('name');
  const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('ASC');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [customerToDelete, setCustomerToDelete] = useState<Customer | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Ref for search input to maintain focus
  const searchInputRef = useRef<HTMLInputElement>(null);

  // Check admin access
  const isAdmin = hasRole(UserRole.ADMIN);

  // Fetch customers - stable function
  const fetchCustomers = useCallback(async (queryParam?: string) => {
    try {
      setLoading(true);
      setError(null);

      const params: CustomerListParams = {
        page,
        size: rowsPerPage,
        sortBy,
        sortDirection
      };

      const searchTerm = queryParam !== undefined ? queryParam : searchQuery;
      let result;
      if (searchTerm && searchTerm.trim()) {
        result = await customerService.searchCustomers({
          ...params,
          query: searchTerm.trim()
        });
      } else {
        result = await customerService.getCustomers(params);
      }

      setCustomers(result.content);
      setTotalCount(result.pagination.totalElements);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch customers';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, [page, rowsPerPage, sortBy, sortDirection]);

  // Error notification effect - separate from fetch function
  useEffect(() => {
    if (error) {
      addNotification({
        type: 'error',
        title: 'Error',
        message: error
      });
    }
  }, [error, addNotification]);

  // Initial load and data reload
  useEffect(() => {
    if (isAdmin) {
      fetchCustomers('');
    }
  }, [isAdmin, page, rowsPerPage, sortBy, sortDirection, fetchCustomers]);

  // Search with debouncing - completely isolated
  useEffect(() => {
    if (!isAdmin) return;
    
    const timeoutId = setTimeout(() => {
      fetchCustomers(searchQuery);
    }, 5000); // Reduced timeout for better responsiveness

    return () => clearTimeout(timeoutId);
  }, [searchQuery]); // Minimal dependencies

  // Search input handler - simple and stable
  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setSearchQuery(newValue);
    
    // Reset to first page when searching
    if (page !== 0) {
      setPage(0);
    }
  };

  const handlePageChange = (_: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleRefresh = () => {
    fetchCustomers(searchQuery);
  };

  const handleAddCustomer = () => {
    navigate(APP_ROUTES.CUSTOMERS_NEW);
  };

  const handleViewCustomer = (customer: Customer) => {
    navigate(APP_ROUTES.CUSTOMERS_VIEW(customer.id));
  };

  const handleEditCustomer = (customer: Customer) => {
    navigate(APP_ROUTES.CUSTOMERS_EDIT(customer.id));
  };

  const handleDeleteClick = (customer: Customer) => {
    setCustomerToDelete(customer);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!customerToDelete) return;

    try {
      setDeleting(true);
      await customerService.deleteCustomer(customerToDelete.id);
      
      addNotification({
        type: 'success',
        title: 'Success',
        message: `Customer "${customerToDelete.name}" has been deleted successfully`
      });

      // Refresh the list
      await fetchCustomers(searchQuery);
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
      setCustomerToDelete(null);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setCustomerToDelete(null);
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

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Customer Management
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={handleRefresh} disabled={loading}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddCustomer}
            disabled={loading}
          >
            Add Customer
          </Button>
        </Box>
      </Box>

      {/* Search */}
      <Paper sx={{ mb: 2, p: 2 }}>
        <TextField
          fullWidth
          placeholder="Search customers by name, account number, phone, or email..."
          value={searchQuery}
          onChange={handleSearchChange}
          inputRef={searchInputRef}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          disabled={loading}
          variant="outlined"
          size="medium"
        />
      </Paper>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Data Table */}
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Account Number</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Phone</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Total Bills</TableCell>
                <TableCell>Created Date</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                    <CircularProgress size={40} />
                  </TableCell>
                </TableRow>
              ) : customers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
                      <PersonIcon sx={{ fontSize: 48, color: 'text.secondary' }} />
                      <Typography variant="body1" color="text.secondary">
                        {searchQuery ? 'No customers found matching your search.' : 'No customers available.'}
                      </Typography>
                      {!searchQuery && (
                        <Button
                          variant="outlined"
                          startIcon={<AddIcon />}
                          onClick={handleAddCustomer}
                        >
                          Add First Customer
                        </Button>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ) : (
                customers.map((customer) => (
                  <TableRow key={customer.id} hover>
                    <TableCell>
                      <Typography variant="body2" fontFamily="monospace">
                        {customer.accountNumber}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontWeight={500}>
                        {customer.name}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {customer.phone}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color={customer.email ? 'text.primary' : 'text.secondary'}>
                        {customer.email || 'Not provided'}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={customer.totalBills}
                        size="small"
                        color={customer.totalBills > 0 ? 'primary' : 'default'}
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {format(new Date(customer.createdAt), 'MMM dd, yyyy')}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <CustomerRowActions
                        customer={customer}
                        onView={() => handleViewCustomer(customer)}
                        onEdit={() => handleEditCustomer(customer)}
                        onDelete={() => handleDeleteClick(customer)}
                      />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        {!loading && customers.length > 0 && (
          <TablePagination
            component="div"
            count={totalCount}
            page={page}
            onPageChange={handlePageChange}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={handleRowsPerPageChange}
            rowsPerPageOptions={[10, 20, 50, 100]}
            showFirstButton
            showLastButton
          />
        )}
      </Paper>

      {/* Floating Action Button */}
      <Fab
        color="primary"
        aria-label="add customer"
        sx={{
          position: 'fixed',
          bottom: 16,
          right: 16,
        }}
        onClick={handleAddCustomer}
        disabled={loading}
      >
        <AddIcon />
      </Fab>

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
            Are you sure you want to delete customer "{customerToDelete?.name}" 
            (Account: {customerToDelete?.accountNumber})?
            
            {customerToDelete && customerToDelete.totalBills > 0 && (
              <>
                <br /><br />
                <strong>Warning:</strong> This customer has {customerToDelete.totalBills} existing bills. 
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