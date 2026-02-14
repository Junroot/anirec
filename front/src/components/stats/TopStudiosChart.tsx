import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import type { StudioStat } from '@/types/stats';

interface TopStudiosChartProps {
  data: StudioStat[];
}

const COLORS = ['#a78bfa', '#818cf8', '#6366f1', '#c4b5fd', '#4f46e5', '#a78bfa'];

export function TopStudiosChart({ data }: TopStudiosChartProps) {
  return (
    <div className="bg-surface rounded-xl border border-surface-lighter p-5">
      <h3 className="text-lg font-semibold text-text-primary mb-4">Top Studios</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} layout="vertical" margin={{ left: 80 }}>
          <XAxis type="number" stroke="#6b6985" fontSize={12} />
          <YAxis type="category" dataKey="studio" stroke="#6b6985" fontSize={12} width={100} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1e1b2e', border: '1px solid #363352', borderRadius: '8px', color: '#f1f0f7' }}
            labelStyle={{ color: '#a5a3b7' }}
            formatter={(value: number | undefined, name: string | undefined) => {
              const v = value ?? 0;
              if (name === 'avgScore') return [v.toFixed(1), 'Avg Score'];
              return [v, 'Count'];
            }}
          />
          <Bar dataKey="count" radius={[0, 4, 4, 0]}>
            {data.map((_, index) => (
              <Cell key={index} fill={COLORS[index % COLORS.length]} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
