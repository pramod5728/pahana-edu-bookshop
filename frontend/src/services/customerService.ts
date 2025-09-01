import { apiService } from './api';
import { API_ENDPOINTS } from '../constants';
import { Customer, CustomerRequest, PagedResponse, ApiResponse } from '../types';

export interface CustomerSearchParams {
  query?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface CustomerListParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

/**
 * Customer Service API functions
 * All functions require ADMIN role as enforced by backend
 */
export const customerService = {
  /**
   * Get all customers with pagination
   */
  getCustomers: async (params: CustomerListParams = {}): Promise<PagedResponse<Customer>> => {
    const {
      page = 0,
      size = 20,
      sortBy = 'name',
      sortDirection = 'ASC'
    } = params;

    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDirection
    });

    const response = await apiService.get<PagedResponse<Customer>>(
      `${API_ENDPOINTS.CUSTOMERS.BASE}?${queryParams}`
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to fetch customers');
    }

    return response.data;
  },

  /**
   * Get customer by ID
   */
  getCustomerById: async (id: number): Promise<Customer> => {
    const response = await apiService.get<Customer>(
      API_ENDPOINTS.CUSTOMERS.BY_ID(id)
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to fetch customer');
    }

    return response.data;
  },

  /**
   * Get customer by account number
   */
  getCustomerByAccountNumber: async (accountNumber: string): Promise<Customer> => {
    const response = await apiService.get<Customer>(
      API_ENDPOINTS.CUSTOMERS.BY_ACCOUNT(accountNumber)
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to fetch customer');
    }

    return response.data;
  },

  /**
   * Create new customer
   */
  createCustomer: async (customerData: CustomerRequest): Promise<Customer> => {
    const response = await apiService.post<Customer>(
      API_ENDPOINTS.CUSTOMERS.BASE,
      customerData
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to create customer');
    }

    return response.data;
  },

  /**
   * Update existing customer
   */
  updateCustomer: async (id: number, customerData: CustomerRequest): Promise<Customer> => {
    const response = await apiService.put<Customer>(
      API_ENDPOINTS.CUSTOMERS.BY_ID(id),
      customerData
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to update customer');
    }

    return response.data;
  },

  /**
   * Delete customer
   */
  deleteCustomer: async (id: number): Promise<void> => {
    const response = await apiService.delete<string>(
      API_ENDPOINTS.CUSTOMERS.BY_ID(id)
    );

    if (!response.success) {
      throw new Error(response.message || 'Failed to delete customer');
    }
  },

  /**
   * Search customers
   */
  searchCustomers: async (params: CustomerSearchParams): Promise<PagedResponse<Customer>> => {
    const {
      query = '',
      page = 0,
      size = 20,
      sortBy = 'name',
      sortDirection = 'ASC'
    } = params;

    const queryParams = new URLSearchParams({
      query,
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDirection
    });

    const response = await apiService.get<PagedResponse<Customer>>(
      `${API_ENDPOINTS.CUSTOMERS.SEARCH}?${queryParams}`
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to search customers');
    }

    return response.data;
  },

  /**
   * Get customers for auto-complete
   */
  getCustomersForAutoComplete: async (prefix: string): Promise<Customer[]> => {
    const queryParams = new URLSearchParams({ prefix });
    
    const response = await apiService.get<Customer[]>(
      `${API_ENDPOINTS.CUSTOMERS.AUTOCOMPLETE}?${queryParams}`
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to fetch customer suggestions');
    }

    return response.data;
  },

  /**
   * Get customers without bills
   */
  getCustomersWithoutBills: async (): Promise<Customer[]> => {
    const response = await apiService.get<Customer[]>(
      API_ENDPOINTS.CUSTOMERS.WITHOUT_BILLS
    );

    if (!response.success || !response.data) {
      throw new Error(response.message || 'Failed to fetch customers without bills');
    }

    return response.data;
  },

  /**
   * Get total customer count
   */
  getTotalCustomerCount: async (): Promise<number> => {
    const response = await apiService.get<number>(
      API_ENDPOINTS.CUSTOMERS.COUNT
    );

    if (!response.success || response.data === undefined) {
      throw new Error(response.message || 'Failed to fetch customer count');
    }

    return response.data;
  },

  /**
   * Validate customer data before submission
   */
  validateCustomerData: (data: CustomerRequest): string[] => {
    const errors: string[] = [];

    if (!data.name?.trim()) {
      errors.push('Customer name is required');
    } else if (data.name.length < 2 || data.name.length > 100) {
      errors.push('Name must be between 2 and 100 characters');
    }

    if (!data.address?.trim()) {
      errors.push('Address is required');
    } else if (data.address.length < 10 || data.address.length > 500) {
      errors.push('Address must be between 10 and 500 characters');
    }

    if (!data.phone?.trim()) {
      errors.push('Phone number is required');
    } else if (!/^(\+94|0)[0-9]{9}$/.test(data.phone)) {
      errors.push('Invalid Sri Lankan phone number format');
    }

    if (data.email && data.email.trim()) {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email)) {
        errors.push('Invalid email format');
      } else if (data.email.length > 100) {
        errors.push('Email must not exceed 100 characters');
      }
    }

    return errors;
  }
};

export default customerService;