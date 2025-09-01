// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  errors?: string[];
  timestamp: string;
  path?: string;
  status?: number;
}

export interface PagedResponse<T> {
  content: T[];
  pagination: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
    numberOfElements: number;
  };
}

// User Types
export enum UserRole {
  ADMIN = 'ADMIN',
  MANAGER = 'MANAGER',
  EMPLOYEE = 'EMPLOYEE',
  VIEWER = 'VIEWER',
}

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: UserRole;
  phone?: string;
  active: boolean;
  lastLogin?: string;
  createdAt: string;
  updatedAt: string;
  displayName: string;
  isAccountLocked: boolean;
  failedLoginAttempts: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
    id: number;
    username: string;
    fullName: string;
    email: string;
    role: UserRole;
    active: boolean;
    lastLogin?: string;
    displayName: string;
  };
  permissions: string[];
}

// Customer Types
export interface Customer {
  id: number;
  accountNumber: string;
  name: string;
  address: string;
  phone: string;
  email?: string;
  createdAt: string;
  updatedAt: string;
  totalBills: number;
  displayName: string;
}

export interface CustomerRequest {
  name: string;
  address: string;
  phone: string;
  email?: string;
}

// Item Types
export interface Item {
  id: number;
  itemCode: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  category?: string;
  minimumStockLevel: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  displayName: string;
  isLowStock: boolean;
  isInStock: boolean;
}

export interface ItemRequest {
  itemCode: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  category?: string;
  minimumStockLevel?: number;
  active?: boolean;
}

// Bill Types
export enum BillStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  PAID = 'PAID',
  CANCELLED = 'CANCELLED',
  PARTIAL_PAID = 'PARTIAL_PAID',
  OVERDUE = 'OVERDUE',
}

export interface BillItem {
  id: number;
  item: Item;
  quantity: number;
  unitPrice: number;
  discountPercentage: number;
  discountAmount: number;
  totalPrice: number;
}

export interface Bill {
  id: number;
  billNumber: string;
  customer: Customer;
  billDate: string;
  subtotal: number;
  taxRate: number;
  taxAmount: number;
  discountAmount: number;
  totalAmount: number;
  status: BillStatus;
  notes?: string;
  createdAt: string;
  billItems: BillItem[];
  displayNumber: string;
  totalQuantity: number;
}

export interface BillItemRequest {
  itemId: number;
  quantity: number;
  discountPercentage?: number;
}

export interface BillRequest {
  customerId: number;
  items: BillItemRequest[];
  discountAmount?: number;
  notes?: string;
}

// Common Types
export interface SelectOption {
  value: string | number;
  label: string;
}

export interface TableColumn {
  field: string;
  headerName: string;
  width?: number;
  flex?: number;
  sortable?: boolean;
  filterable?: boolean;
  renderCell?: (params: any) => React.ReactNode;
}

export interface FormError {
  field: string;
  message: string;
}

// Store Types
export interface AuthState {
  user: AuthResponse['user'] | null;
  token: string | null;
  permissions: string[];
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  hasPermission: (permission: string) => boolean;
  hasRole: (role: UserRole) => boolean;
}

export interface NotificationState {
  notifications: Notification[];
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp'>) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  timestamp: Date;
  autoHide?: boolean;
  duration?: number;
}