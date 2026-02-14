import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import type { RatingDistribution } from '@/types/stats';

interface RatingHistogramProps {
  data: RatingDistribution[];
}

export function RatingHistogram({ data }: RatingHistogramProps) {
  return (
    <div className="bg-surface rounded-xl border border-surface-lighter p-5">
      <h3 className="text-lg font-semibold text-text-primary mb-4">Rating Distribution</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <XAxis dataKey="score" stroke="#6b6985" fontSize={12} />
          <YAxis stroke="#6b6985" fontSize={12} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1e1b2e', border: '1px solid #363352', borderRadius: '8px', color: '#f1f0f7' }}
            labelStyle={{ color: '#a5a3b7' }}
            formatter={(value: number | undefined) => [value ?? 0, 'Count']}
            labelFormatter={(label: React.ReactNode) => `Score: ${label}`}
          />
          <Bar dataKey="count" fill="#6366f1" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
