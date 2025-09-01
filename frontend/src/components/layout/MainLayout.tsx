import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Divider,
  ListItemIcon,
  ListItemText,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  useTheme,
  useMediaQuery,
  Badge,
} from '@mui/material';
import {
  Menu as MenuIcon,
  AccountCircle,
  Settings,
  Logout,
  Dashboard,
  People,
  Inventory,
  Receipt,
  Analytics,
  Notifications,
} from '@mui/icons-material';
import { useAuthStore } from '../../store/authStore';
import { APP_ROUTES, PERMISSIONS } from '../../constants';
import { UserRole } from '../../types';

interface MainLayoutProps {
  children: React.ReactNode;
}

const drawerWidth = 240;

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const isMobile = useMediaQuery(theme.breakpoints.down('lg'));
  const { user, logout, hasPermission, hasRole } = useAuthStore();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleProfileMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate(APP_ROUTES.LOGIN);
  };

  const menuItems = [
    {
      text: 'Dashboard',
      icon: <Dashboard />,
      path: APP_ROUTES.DASHBOARD,
      permission: null,
      requiresRole: null,
    },
    {
      text: 'Customers',
      icon: <People />,
      path: APP_ROUTES.CUSTOMERS,
      permission: null,
      requiresRole: UserRole.ADMIN, // Admin only access
    },
    {
      text: 'Inventory',
      icon: <Inventory />,
      path: APP_ROUTES.ITEMS,
      permission: PERMISSIONS.ITEM_READ,
      requiresRole: null,
    },
    {
      text: 'Bills',
      icon: <Receipt />,
      path: APP_ROUTES.BILLS,
      permission: PERMISSIONS.BILL_READ,
      requiresRole: null,
    },
    {
      text: 'Reports',
      icon: <Analytics />,
      path: APP_ROUTES.REPORTS,
      permission: PERMISSIONS.REPORT_VIEW,
      requiresRole: null,
    },
  ];

  const drawer = (
    <Box>
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          Pahana Bookshop
        </Typography>
      </Toolbar>
      <Divider />
      <List>
        {menuItems.map((item) => {
          // Check role requirement first (for admin-only features)
          if (item.requiresRole && !hasRole(item.requiresRole)) {
            return null;
          }
          
          // Check permission requirement
          if (item.permission && !hasPermission(item.permission)) {
            return null;
          }

          return (
            <ListItem key={item.text} disablePadding>
              <ListItemButton
                onClick={() => {
                  navigate(item.path);
                  if (isMobile) {
                    setMobileOpen(false);
                  }
                }}
                selected={window.location.pathname === item.path}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      {/* App Bar */}
      <AppBar
        position="fixed"
        sx={{
          width: { lg: `calc(100% - ${drawerWidth}px)` },
          ml: { lg: `${drawerWidth}px` },
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { lg: 'none' } }}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            {/* You can dynamically set the page title here */}
          </Typography>

          {/* Notifications */}
          <IconButton color="inherit" sx={{ mr: 1 }}>
            <Badge badgeContent={4} color="error">
              <Notifications />
            </Badge>
          </IconButton>

          {/* User Menu */}
          <Button
            color="inherit"
            onClick={handleProfileMenuOpen}
            startIcon={<Avatar sx={{ width: 32, height: 32 }}>{user?.fullName.charAt(0)}</Avatar>}
          >
            {user?.fullName}
          </Button>

          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleProfileMenuClose}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
          >
            <MenuItem onClick={() => { navigate(APP_ROUTES.PROFILE); handleProfileMenuClose(); }}>
              <ListItemIcon>
                <AccountCircle />
              </ListItemIcon>
              <ListItemText>Profile</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => { navigate(APP_ROUTES.SETTINGS); handleProfileMenuClose(); }}>
              <ListItemIcon>
                <Settings />
              </ListItemIcon>
              <ListItemText>Settings</ListItemText>
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout}>
              <ListItemIcon>
                <Logout />
              </ListItemIcon>
              <ListItemText>Logout</ListItemText>
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Navigation Drawer */}
      <Box
        component="nav"
        sx={{ width: { lg: drawerWidth }, flexShrink: { lg: 0 } }}
      >
        {/* Mobile drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, // Better open performance on mobile.
          }}
          sx={{
            display: { xs: 'block', lg: 'none' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
        >
          {drawer}
        </Drawer>

        {/* Desktop drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', lg: 'block' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { lg: `calc(100% - ${drawerWidth}px)` },
          minHeight: '100vh',
          backgroundColor: 'background.default',
        }}
      >
        <Toolbar />
        {children}
      </Box>
    </Box>
  );
};