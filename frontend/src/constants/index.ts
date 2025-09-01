export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || '/api';

export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    PROFILE: '/auth/profile',
    REGISTER: '/auth/register',
    CHANGE_PASSWORD: '/auth/change-password',
  },
  
  // Customers
  CUSTOMERS: {
    BASE: '/customers',
    BY_ID: (id: number) => `/customers/${id}`,
    BY_ACCOUNT: (accountNumber: string) => `/customers/account/${accountNumber}`,
    SEARCH: '/customers/search',
    AUTOCOMPLETE: '/customers/autocomplete',
    WITHOUT_BILLS: '/customers/without-bills',
    COUNT: '/customers/count',
  },
  
  // Items
  ITEMS: {
    BASE: '/items',
    BY_ID: (id: number) => `/items/${id}`,
    BY_CODE: (itemCode: string) => `/items/code/${itemCode}`,
    SEARCH: '/items/search',
    CATEGORIES: '/items/categories',
    AUTOCOMPLETE: '/items/autocomplete',
    LOW_STOCK: '/items/low-stock',
    OUT_OF_STOCK: '/items/out-of-stock',
    RESTOCK_NEEDED: '/items/restock-needed',
    INVENTORY_VALUE: '/items/inventory-value',
    STOCK: (id: number) => `/items/${id}/stock`,
    INCREASE_STOCK: (id: number) => `/items/${id}/stock/increase`,
    REDUCE_STOCK: (id: number) => `/items/${id}/stock/reduce`,
    AVAILABILITY: (id: number) => `/items/${id}/availability`,
    PRICE_RANGE: '/items/price-range',
    BY_CATEGORY: (category: string) => `/items/category/${category}`,
  },
  
  // Bills
  BILLS: {
    BASE: '/bills',
    BY_ID: (id: number) => `/bills/${id}`,
    BY_NUMBER: (billNumber: string) => `/bills/number/${billNumber}`,
    BY_CUSTOMER: (customerId: number) => `/bills/customer/${customerId}`,
    SEARCH: '/bills/search',
    BY_STATUS: (status: string) => `/bills/status/${status}`,
    DATE_RANGE: '/bills/date-range',
    TODAY: '/bills/today',
    OVERDUE: '/bills/overdue',
    PAY: (id: number) => `/bills/${id}/pay`,
    CANCEL: (id: number) => `/bills/${id}/cancel`,
    SALES_TOTAL: '/bills/sales-total',
    AVERAGE_AMOUNT: '/bills/average-amount',
  },
  
  // Reports
  REPORTS: {
    DASHBOARD: '/reports/dashboard',
    SALES: '/reports/sales',
    INVENTORY: '/reports/inventory',
    CUSTOMERS: '/reports/customers',
  },
};

export const APP_ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  DASHBOARD: '/dashboard',
  
  // Customers
  CUSTOMERS: '/customers',
  CUSTOMERS_NEW: '/customers/new',
  CUSTOMERS_EDIT: (id: number) => `/customers/${id}/edit`,
  CUSTOMERS_VIEW: (id: number) => `/customers/${id}`,
  
  // Items
  ITEMS: '/items',
  ITEMS_NEW: '/items/new',
  ITEMS_EDIT: (id: number) => `/items/${id}/edit`,
  ITEMS_VIEW: (id: number) => `/items/${id}`,
  
  // Bills
  BILLS: '/bills',
  BILLS_NEW: '/bills/new',
  BILLS_EDIT: (id: number) => `/bills/${id}/edit`,
  BILLS_VIEW: (id: number) => `/bills/${id}`,
  
  // Reports
  REPORTS: '/reports',
  REPORTS_SALES: '/reports/sales',
  REPORTS_INVENTORY: '/reports/inventory',
  REPORTS_CUSTOMERS: '/reports/customers',
  
  // Settings
  SETTINGS: '/settings',
  PROFILE: '/profile',
};

export const PERMISSIONS = {
  // Customer permissions
  CUSTOMER_READ: 'PERM_CUSTOMER_READ',
  CUSTOMER_WRITE: 'PERM_CUSTOMER_WRITE',
  CUSTOMER_DELETE: 'PERM_CUSTOMER_DELETE',
  
  // Item permissions
  ITEM_READ: 'PERM_ITEM_READ',
  ITEM_WRITE: 'PERM_ITEM_WRITE',
  ITEM_DELETE: 'PERM_ITEM_DELETE',
  
  // Bill permissions
  BILL_READ: 'PERM_BILL_READ',
  BILL_WRITE: 'PERM_BILL_WRITE',
  BILL_DELETE: 'PERM_BILL_DELETE',
  
  // User permissions
  USER_MANAGE: 'PERM_USER_MANAGE',
  
  // Report permissions
  REPORT_VIEW: 'PERM_REPORT_VIEW',
  
  // System permissions
  SYSTEM_CONFIG: 'PERM_SYSTEM_CONFIG',
};

export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
};

export const VALIDATION = {
  PASSWORD_MIN_LENGTH: 8,
  PHONE_REGEX: /^(\+94|0)[0-9]{9}$/,
  EMAIL_REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  ITEM_CODE_REGEX: /^[A-Z0-9-]+$/,
  USERNAME_REGEX: /^[a-zA-Z0-9_]+$/,
};

export const STORAGE_KEYS = {
  AUTH_TOKEN: 'pahana_auth_token',
  REFRESH_TOKEN: 'pahana_refresh_token',
  USER_PREFERENCES: 'pahana_user_preferences',
  THEME_MODE: 'pahana_theme_mode',
};

export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  DISPLAY_WITH_TIME: 'MMM dd, yyyy hh:mm a',
  INPUT: 'yyyy-MM-dd',
  ISO: 'yyyy-MM-dd\'T\'HH:mm:ss',
};

export const BILL_STATUS_COLORS = {
  DRAFT: '#9e9e9e',
  PENDING: '#ff9800',
  PAID: '#4caf50',
  CANCELLED: '#f44336',
  PARTIAL_PAID: '#2196f3',
  OVERDUE: '#d32f2f',
};

export const NOTIFICATION_TYPES = {
  SUCCESS: 'success' as const,
  ERROR: 'error' as const,
  WARNING: 'warning' as const,
  INFO: 'info' as const,
};