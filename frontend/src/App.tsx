import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Box } from '@mui/material';
import { useAuthStore } from './store/authStore';
import { APP_ROUTES } from './constants';

// Layout Components
import { MainLayout } from './components/layout/MainLayout';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { AdminRoute } from './components/auth/AdminRoute';

// Page Components
import { LoginPage } from './pages/auth/LoginPage';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { CustomerListPage } from './pages/customers/CustomerListPage';
import { CustomerFormPage } from './pages/customers/CustomerFormPage';
import { CustomerViewPage } from './pages/customers/CustomerViewPage';
import { ItemListPage } from './pages/items/ItemListPage';
import { ItemFormPage } from './pages/items/ItemFormPage';
import { ItemViewPage } from './pages/items/ItemViewPage';
import { BillListPage } from './pages/bills/BillListPage';
import { BillFormPage } from './pages/bills/BillFormPage';
import { BillViewPage } from './pages/bills/BillViewPage';
import { ReportsPage } from './pages/reports/ReportsPage';
import { ProfilePage } from './pages/profile/ProfilePage';
import { NotFoundPage } from './pages/error/NotFoundPage';

// Notification Component
import { NotificationContainer } from './components/common/NotificationContainer';

function App() {
  const { isAuthenticated } = useAuthStore();

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Routes>
        {/* Public Routes */}
        <Route 
          path={APP_ROUTES.LOGIN} 
          element={
            isAuthenticated ? (
              <Navigate to={APP_ROUTES.DASHBOARD} replace />
            ) : (
              <LoginPage />
            )
          } 
        />

        {/* Protected Routes */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <MainLayout>
                <Routes>
                  {/* Dashboard */}
                  <Route path={APP_ROUTES.DASHBOARD} element={<DashboardPage />} />
                  <Route path={APP_ROUTES.HOME} element={<Navigate to={APP_ROUTES.DASHBOARD} replace />} />

                  {/* Customer Routes - Admin Only */}
                  <Route path={APP_ROUTES.CUSTOMERS} element={
                    <AdminRoute>
                      <CustomerListPage />
                    </AdminRoute>
                  } />
                  <Route path={APP_ROUTES.CUSTOMERS_NEW} element={
                    <AdminRoute>
                      <CustomerFormPage />
                    </AdminRoute>
                  } />
                  <Route path="/customers/:id/edit" element={
                    <AdminRoute>
                      <CustomerFormPage />
                    </AdminRoute>
                  } />
                  <Route path="/customers/:id" element={
                    <AdminRoute>
                      <CustomerViewPage />
                    </AdminRoute>
                  } />

                  {/* Item Routes */}
                  <Route path={APP_ROUTES.ITEMS} element={<ItemListPage />} />
                  <Route path={APP_ROUTES.ITEMS_NEW} element={<ItemFormPage />} />
                  <Route path="/items/:id/edit" element={<ItemFormPage />} />
                  <Route path="/items/:id" element={<ItemViewPage />} />

                  {/* Bill Routes */}
                  <Route path={APP_ROUTES.BILLS} element={<BillListPage />} />
                  <Route path={APP_ROUTES.BILLS_NEW} element={<BillFormPage />} />
                  <Route path="/bills/:id/edit" element={<BillFormPage />} />
                  <Route path="/bills/:id" element={<BillViewPage />} />

                  {/* Report Routes */}
                  <Route path={APP_ROUTES.REPORTS} element={<ReportsPage />} />
                  <Route path={APP_ROUTES.REPORTS_SALES} element={<ReportsPage />} />
                  <Route path={APP_ROUTES.REPORTS_INVENTORY} element={<ReportsPage />} />
                  <Route path={APP_ROUTES.REPORTS_CUSTOMERS} element={<ReportsPage />} />

                  {/* Profile Route */}
                  <Route path={APP_ROUTES.PROFILE} element={<ProfilePage />} />

                  {/* 404 Route */}
                  <Route path="*" element={<NotFoundPage />} />
                </Routes>
              </MainLayout>
            </ProtectedRoute>
          }
        />
      </Routes>

      {/* Global Notification Container */}
      <NotificationContainer />
    </Box>
  );
}

export default App;