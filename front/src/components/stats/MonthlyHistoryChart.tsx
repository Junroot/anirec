import { XAxis, YAxis, Tooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';
import type { MonthlyHistory } from '@/types/stats';

interface MonthlyHistoryChartProps {
  data: MonthlyHistory[];
}

export function MonthlyHistoryChart({ data }: MonthlyHistoryChartProps) {
  return (
    <div className="bg-surface rounded-xl border border-surface-lighter p-5">
      <h3 className="text-lg font-semibold text-text-primary mb-4">Monthly Activity</h3>
      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={data}>
          <defs>
            <linearGradient id="colorCount" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3} />
              <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis dataKey="month" stroke="#6b6985" fontSize={12} />
          <YAxis stroke="#6b6985" fontSize={12} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1e1b2e', border: '1px solid #363352', borderRadius: '8px', color: '#f1f0f7' }}
            labelStyle={{ color: '#a5a3b7' }}
          />
          <Area type="monotone" dataKey="count" stroke="#6366f1" fill="url(#colorCount)" strokeWidth={2} />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
