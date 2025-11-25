import { useAppDispatch } from './useAppDispatch';
import { useAppSelector } from './useAppSelector';
import { logout, fetchMe } from '../redux/authSlice';
import type { User } from '../types/userTypes';

export const useAuth = () => {
  const dispatch = useAppDispatch();

  const { user, isAuthenticated, isLoading, serverMessage, accessToken } =
    useAppSelector((state) => state.auth);

  const handleLogout = () => dispatch(logout());

  const checkAuth = () => {
    if (accessToken && !user) {
      dispatch(fetchMe());
    }
  };

  return {
    user: user as User | null,
    isAuthenticated,
    isLoading,
    serverMessage,
    accessToken,
    logout: handleLogout,
    checkAuth,
  };
};
