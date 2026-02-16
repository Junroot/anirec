import type { User as SupabaseUser } from '@supabase/supabase-js';

export interface User {
  id: string;
  email: string;
  username: string;
  avatar?: string;
  createdAt: string;
}

export function mapSupabaseUser(supabaseUser: SupabaseUser): User {
  return {
    id: supabaseUser.id,
    email: supabaseUser.email ?? '',
    username: supabaseUser.user_metadata?.username ?? supabaseUser.email?.split('@')[0] ?? '',
    createdAt: supabaseUser.created_at,
  };
}
