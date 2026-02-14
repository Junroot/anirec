import { createBrowserRouter } from 'react-router';
import { MainLayout } from '@/components/layout/MainLayout';
import { AuthLayout } from '@/components/layout/AuthLayout';
import { ProtectedRoute } from '@/components/layout/ProtectedRoute';
import { HomePage } from '@/pages/HomePage';
import { LoginPage } from '@/pages/LoginPage';
import { SignupPage } from '@/pages/SignupPage';
import { SearchAnimePage } from '@/pages/SearchAnimePage';
import { RecommendPage } from '@/pages/RecommendPage';
import { MyRatingsPage } from '@/pages/MyRatingsPage';
import { StatsPage } from '@/pages/StatsPage';
import { NotFoundPage } from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'search/anime', element: <SearchAnimePage /> },
      {
        path: 'recommend',
        element: (
          <ProtectedRoute>
            <RecommendPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-ratings',
        element: (
          <ProtectedRoute>
            <MyRatingsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'stats',
        element: (
          <ProtectedRoute>
            <StatsPage />
          </ProtectedRoute>
        ),
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
  {
    element: <AuthLayout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      { path: 'signup', element: <SignupPage /> },
    ],
  },
]);
