import { create } from 'zustand';
import { NotificationState, Notification } from '../types';

export const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],

  addNotification: (notification) => {
    const id = Math.random().toString(36).substr(2, 9);
    const timestamp = new Date();
    
    const newNotification: Notification = {
      id,
      timestamp,
      autoHide: true,
      duration: 5000,
      ...notification,
    };

    set((state) => ({
      notifications: [newNotification, ...state.notifications],
    }));

    // Auto-remove notification if autoHide is true
    if (newNotification.autoHide) {
      setTimeout(() => {
        get().removeNotification(id);
      }, newNotification.duration);
    }
  },

  removeNotification: (id) => {
    set((state) => ({
      notifications: state.notifications.filter((notification) => notification.id !== id),
    }));
  },

  clearNotifications: () => {
    set({ notifications: [] });
  },
}));