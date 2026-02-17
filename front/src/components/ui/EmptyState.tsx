import type { ReactNode } from 'react';
import type { LucideIcon } from 'lucide-react';
import { InboxIcon } from 'lucide-react';

interface EmptyStateProps {
  icon?: LucideIcon;
  title: string;
  description?: string;
  action?: ReactNode;
}

export function EmptyState({ icon: Icon = InboxIcon, title, description, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <Icon size={48} className="text-outline mb-4" />
      <h3 className="text-lg font-semibold text-on-surface mb-2">{title}</h3>
      {description && <p className="text-on-surface-variant text-sm max-w-md mb-6">{description}</p>}
      {action}
    </div>
  );
}
