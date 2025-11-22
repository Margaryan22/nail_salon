import { useAppSelector } from '../hooks/useAppSelector';
import { useAppDispatch } from '../hooks/useAppDispatch';
import { logout, fetchMe } from '../redux/authSlice';
import type { User } from '../types/userTypes';

export const useAuth = () => {
  const dispatch = useAppDispatch();

  const { user, isAuthenticated, isLoading, serverMessage, token } =
    useAppSelector((state) => state.auth);

  const handleLogout = () => dispatch(logout());

  // Проверка и восстановление сессии при монтировании (очень удобно в _app.tsx или layout)
  const checkAuth = () => {
    if (token && !user) {
      dispatch(fetchMe());
    }
  };

  return {
    user: user as User | null,
    isAuthenticated,
    isLoading,
    serverMessage,
    token,
    logout: handleLogout,
    checkAuth,
  };
};
