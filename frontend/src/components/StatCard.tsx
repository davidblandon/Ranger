import "./ui.css";

interface StatCardProps {
  label: string;
  value: string | number;
  sub?: string;
  accent?: string;
}

export default function StatCard({ label, value, sub, accent }: StatCardProps) {
  return (
    <div className="stat" style={accent ? ({ ["--accent" as string]: accent }) : undefined}>
      <p className="eyebrow stat__label">{label}</p>
      <div className="stat__value">{value}</div>
      {sub && <p className="stat__sub">{sub}</p>}
    </div>
  );
}
