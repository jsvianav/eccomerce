import { useQuery } from "@tanstack/react-query"
import { getProducts } from "@/api/products"
import { getOrders } from "@/api/orders"
import { getInventory } from "@/api/inventory"
import { getUsers } from "@/api/users"
import { Skeleton } from "@/components/ui/skeleton"
import { StatusBadge } from "@/components/StatusBadge"
import { formatCurrency } from "@/lib/utils"
import { TrendingUp, Package, Users, TriangleAlert, ShoppingBag } from "lucide-react"
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Cell,
} from "recharts"

/* ── Status colours (restrained palette, impeccable product register) ── */
const STATUS_BAR: Record<string, string> = {
  CREATED:   "hsl(220 72% 60%)",   // indigo-ish
  PAID:      "hsl(145 55% 44%)",   // emerald
  CANCELLED: "hsl(0   65% 57%)",   // red
}
const STATUS_LABEL: Record<string, string> = {
  CREATED: "Creados", PAID: "Pagados", CANCELLED: "Cancelados",
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function ChartTooltip({ active, payload, label }: any) {
  if (!active || !payload?.length) return null
  return (
    <div className="rounded-xl bg-card ring-1 ring-border shadow-[0_4px_20px_-2px_rgb(0_0_0_/_0.12)] px-3 py-2.5">
      <p className="text-[11px] font-semibold text-muted-foreground uppercase tracking-wide mb-0.5">
        {STATUS_LABEL[label] ?? label}
      </p>
      <p className="text-xl font-bold tabular leading-none text-foreground">
        {payload[0].value}
      </p>
    </div>
  )
}

export default function Dashboard() {
  const products  = useQuery({ queryKey: ["products"],  queryFn: getProducts  })
  const orders    = useQuery({ queryKey: ["orders"],    queryFn: getOrders    })
  const inventory = useQuery({ queryKey: ["inventory"], queryFn: getInventory })
  const users     = useQuery({ queryKey: ["users"],     queryFn: getUsers     })

  const loading = products.isLoading || orders.isLoading || inventory.isLoading || users.isLoading

  const paidTotal  = (orders.data ?? []).filter(o => o.status === "PAID").reduce((s,o) => s + o.totalAmount, 0)
  const paidCount  = (orders.data ?? []).filter(o => o.status === "PAID").length
  const lowStock   = (inventory.data ?? []).filter(i => i.stock < 10).length
  const available  = (products.data ?? []).filter(p => p.available).length

  const ordersByStatus = ["CREATED", "PAID", "CANCELLED"].map(status => ({
    status,
    count: (orders.data ?? []).filter(o => o.status === status).length,
  }))

  const recent = [...(orders.data ?? [])].reverse().slice(0, 6)

  /* ── Bento: asymmetric grid (high-end-visual-design §3.B Asymmetrical Bento) ──
     Row 1: [Revenue 2col] [Orders 1col] [Products 1col]  [Users 1col]
     Row 2: [Chart 2col]   [Low-stock 1col]               [Recent orders 2col]
  ── */
  return (
    <div className="space-y-4">
      {/* Page eyebrow (minimalist-ui §3: typographic hierarchy) */}
      <div>
        <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-0.5">
          Vista general
        </p>
        <h2 className="text-xl font-bold tracking-tight text-foreground">Dashboard</h2>
      </div>

      {/* ─── Bento row 1: KPIs ─── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">

        {/* Hero KPI — Revenue (spans 2 on lg) */}
        <div className="lg:col-span-2 rounded-2xl bg-card ring-1 ring-border shadow-card p-5 flex flex-col justify-between min-h-[130px]">
          <div className="flex items-start justify-between">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Ingresos pagados
            </p>
            <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary/10">
              <TrendingUp className="h-3.5 w-3.5 text-primary" strokeWidth={2.25} />
            </div>
          </div>
          {loading ? (
            <div className="space-y-2 mt-3">
              <Skeleton className="h-9 w-40" />
              <Skeleton className="h-3.5 w-24" />
            </div>
          ) : (
            <div className="mt-auto pt-3">
              <p className="text-3xl font-bold tracking-tight tabular text-foreground">
                {formatCurrency(paidTotal)}
              </p>
              <p className="text-[12px] text-muted-foreground mt-1">
                {paidCount} {paidCount === 1 ? "pedido" : "pedidos"} pagados
              </p>
            </div>
          )}
        </div>

        {/* Orders */}
        <KpiCard
          label="Pedidos"
          icon={ShoppingBag}
          loading={loading}
          value={orders.data?.length ?? 0}
          sub={`${(orders.data ?? []).filter(o => o.status === "CREATED").length} pendientes`}
        />

        {/* Products */}
        <KpiCard
          label="Productos"
          icon={Package}
          loading={loading}
          value={products.data?.length ?? 0}
          sub={`${available} disponibles`}
        />
      </div>

      {/* ─── Bento row 2: secondary KPIs + chart + recent orders ─── */}
      <div className="grid grid-cols-2 lg:grid-cols-5 gap-3">

        {/* Users */}
        <div className="col-span-1 rounded-2xl bg-card ring-1 ring-border shadow-card p-4 flex flex-col">
          <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-3">
            Usuarios
          </p>
          {loading ? <Skeleton className="h-8 w-16" /> : (
            <p className="text-2xl font-bold tabular text-foreground mt-auto">{users.data?.length ?? 0}</p>
          )}
          <div className="mt-2 flex items-center gap-1.5">
            <div className="flex h-5 w-5 items-center justify-center rounded-md bg-primary/10">
              <Users className="h-3 w-3 text-primary" strokeWidth={2} />
            </div>
            <span className="text-[11px] text-muted-foreground">registrados</span>
          </div>
        </div>

        {/* Low stock alert */}
        <div className={`col-span-1 rounded-2xl ring-1 shadow-card p-4 flex flex-col ${
          lowStock > 0
            ? "bg-amber-50 dark:bg-amber-900/15 ring-amber-200 dark:ring-amber-700/40"
            : "bg-card ring-border"
        }`}>
          <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-3">
            Stock bajo
          </p>
          {loading ? <Skeleton className="h-8 w-12" /> : (
            <p className={`text-2xl font-bold tabular mt-auto ${
              lowStock > 0 ? "text-amber-600 dark:text-amber-400" : "text-foreground"
            }`}>
              {lowStock}
              {lowStock > 0 && (
                <TriangleAlert className="inline ml-1.5 h-4 w-4 align-middle" />
              )}
            </p>
          )}
          <p className="text-[11px] text-muted-foreground mt-1">items con &lt; 10 uds.</p>
        </div>

        {/* Chart */}
        <div className="col-span-2 lg:col-span-2 rounded-2xl bg-card ring-1 ring-border shadow-card p-4">
          <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-3">
            Pedidos por estado
          </p>
          {loading ? (
            <Skeleton className="h-32 w-full rounded-xl" />
          ) : (
            <ResponsiveContainer width="100%" height={128}>
              <BarChart data={ordersByStatus} barSize={28} margin={{ top: 0, right: 0, left: -28, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="hsl(var(--border))" />
                <XAxis
                  dataKey="status"
                  tick={{ fontSize: 10, fill: "hsl(var(--muted-foreground))" }}
                  axisLine={false} tickLine={false}
                  tickFormatter={v => STATUS_LABEL[v] ?? v}
                />
                <YAxis
                  tick={{ fontSize: 10, fill: "hsl(var(--muted-foreground))" }}
                  axisLine={false} tickLine={false} allowDecimals={false}
                />
                <Tooltip content={<ChartTooltip />} cursor={{ fill: "hsl(var(--muted) / 0.5)" }} />
                <Bar dataKey="count" radius={[5, 5, 0, 0]}>
                  {ordersByStatus.map(e => (
                    <Cell key={e.status} fill={STATUS_BAR[e.status] ?? "#94a3b8"} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* Recent orders */}
        <div className="col-span-2 lg:col-span-1 rounded-2xl bg-card ring-1 ring-border shadow-card p-4">
          <div className="flex items-center justify-between mb-3">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Recientes
            </p>
            {recent.length > 0 && (
              <span className="text-[10px] text-muted-foreground">{recent.length} pedidos</span>
            )}
          </div>
          {loading ? (
            <div className="space-y-2.5">
              {Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-10 w-full rounded-lg" />)}
            </div>
          ) : recent.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 text-center">
              <ShoppingBag className="h-8 w-8 text-muted-foreground/20 mb-2" />
              <p className="text-[12px] text-muted-foreground">Sin pedidos aún</p>
            </div>
          ) : (
            <div className="space-y-1.5">
              {recent.map(order => (
                <div key={order.id} className="flex items-center justify-between rounded-lg px-2.5 py-2 hover:bg-muted/50 transition-colors duration-100">
                  <div className="min-w-0">
                    <p className="text-[12px] font-semibold text-foreground tabular">#{order.id}</p>
                    <p className="text-[10px] text-muted-foreground">U#{order.userId}</p>
                  </div>
                  <div className="flex items-center gap-2 ml-2 shrink-0">
                    <span className="text-[12px] font-semibold tabular text-foreground">
                      {formatCurrency(order.totalAmount, order.currency)}
                    </span>
                    <StatusBadge status={order.status} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

/* ── Small KPI card component ── */
function KpiCard({
  label,
  icon: Icon,
  loading,
  value,
  sub,
}: {
  label: string
  icon: React.ComponentType<{ className?: string; strokeWidth?: number }>
  loading: boolean
  value: number
  sub: string
}) {
  return (
    <div className="rounded-2xl bg-card ring-1 ring-border shadow-card p-4 flex flex-col">
      <div className="flex items-center justify-between mb-3">
        <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
          {label}
        </p>
        <div className="flex h-6 w-6 items-center justify-center rounded-md bg-muted">
          <Icon className="h-3.5 w-3.5 text-muted-foreground" strokeWidth={1.75} />
        </div>
      </div>
      {loading ? (
        <Skeleton className="h-8 w-14 mt-auto" />
      ) : (
        <p className="text-2xl font-bold tabular text-foreground mt-auto">{value}</p>
      )}
      <p className="text-[11px] text-muted-foreground mt-1">{sub}</p>
    </div>
  )
}
