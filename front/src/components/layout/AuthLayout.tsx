import { Outlet, Link } from 'react-router';

export function AuthLayout() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-surface px-4">
      <Link to="/" className="mb-8">
        <span className="text-3xl font-bold bg-gradient-to-r from-primary to-tertiary bg-clip-text text-transparent">
          AniRec
        </span>
      </Link>
      <div className="w-full max-w-md">
        <Outlet />
      </div>
    </div>
  );
}
