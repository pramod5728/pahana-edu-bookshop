import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { apiService } from '../services/api';
import { API_ENDPOINTS, STORAGE_KEYS } from '../constants';
import { AuthState, LoginRequest, UserRole, AuthResponse } from '../types';

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      permissions: [],
      isAuthenticated: false,
      isLoading: false,

      login: async (credentials: LoginRequest) => {
        try {
          set({ isLoading: true });

          const response = await apiService.post<AuthResponse>(API_ENDPOINTS.AUTH.LOGIN, credentials);
          
          if (response.success && response.data) {
            const { accessToken, refreshToken, user, permissions } = response.data;

            // Store tokens
            localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, accessToken);
            localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);

            set({
              user,
              token: accessToken,
              permissions,
              isAuthenticated: true,
              isLoading: false,
            });
          } else {
            throw new Error(response.message || 'Login failed');
          }
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        // Clear storage
        localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);

        // Reset state
        set({
          user: null,
          token: null,
          permissions: [],
          isAuthenticated: false,
          isLoading: false,
        });

        // Call logout endpoint (optional)
        apiService.post(API_ENDPOINTS.AUTH.LOGOUT).catch(() => {
          // Ignore errors from logout endpoint
        });
      },

      refreshToken: async () => {
        try {
          const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
          if (!refreshToken) {
            throw new Error('No refresh token available');
          }

          const response = await apiService.post<AuthResponse>(API_ENDPOINTS.AUTH.REFRESH, {
            refreshToken,
          });

          if (response.success && response.data) {
            const { accessToken, user, permissions } = response.data;

            localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, accessToken);

            set({
              user,
              token: accessToken,
              permissions,
              isAuthenticated: true,
            });
          } else {
            throw new Error('Token refresh failed');
          }
        } catch (error) {
          // Refresh failed, logout user
          get().logout();
          throw error;
        }
      },

      hasPermission: (permission: string): boolean => {
        const { permissions } = get();
        return permissions.includes(permission);
      },

      hasRole: (role: UserRole): boolean => {
        const { user } = get();
        return user?.role === role;
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        permissions: state.permissions,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);