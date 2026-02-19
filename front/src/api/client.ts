import { supabase } from '@/lib/supabase';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

async function getAuthHeaders(): Promise<Record<string, string>> {
  const { data } = await supabase.auth.getSession();
  const token = data.session?.access_token;
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function get<T>(
  path: string,
  params?: Record<string, string | number | undefined | null>,
  signal?: AbortSignal,
): Promise<T> {
  const url = new URL(path, BASE_URL);

  if (params) {
    for (const [key, value] of Object.entries(params)) {
      if (value != null) {
        url.searchParams.set(key, String(value));
      }
    }
  }

  const authHeaders = await getAuthHeaders();

  const response = await fetch(url.toString(), {
    headers: { 'Content-Type': 'application/json', ...authHeaders },
    signal,
  });

  if (!response.ok) {
    throw new ApiError(response.status, `HTTP ${response.status}: ${response.statusText}`);
  }

  return response.json() as Promise<T>;
}

async function put<T>(path: string, body: unknown): Promise<T> {
  const url = new URL(path, BASE_URL);
  const authHeaders = await getAuthHeaders();

  const response = await fetch(url.toString(), {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...authHeaders },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw new ApiError(response.status, `HTTP ${response.status}: ${response.statusText}`);
  }

  return response.json() as Promise<T>;
}

async function del(path: string): Promise<void> {
  const url = new URL(path, BASE_URL);
  const authHeaders = await getAuthHeaders();

  const response = await fetch(url.toString(), {
    method: 'DELETE',
    headers: { ...authHeaders },
  });

  if (!response.ok) {
    throw new ApiError(response.status, `HTTP ${response.status}: ${response.statusText}`);
  }
}

export const apiClient = { get, put, del };
