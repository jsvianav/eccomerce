import * as React from "react"
import {
  Table, TableHeader, TableBody,
  TableRow, TableHead, TableCell,
} from "@/components/ui/table"
import { Skeleton } from "@/components/ui/skeleton"
import { Input } from "@/components/ui/input"
import { Search, Inbox } from "lucide-react"

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export interface Column<T = any> {
  key: string
  header: string
  render?: (item: T) => React.ReactNode
  className?: string
}

interface DataTableProps<T> {
  data: T[]
  columns: Column<T>[]
  loading?: boolean
  emptyMessage?: string
  emptyAction?: React.ReactNode
}

export function DataTable<T extends object>({
  data,
  columns,
  loading = false,
  emptyMessage = "No hay datos disponibles",
  emptyAction,
}: DataTableProps<T>) {
  const [search, setSearch] = React.useState("")

  const filtered = React.useMemo(() => {
    if (!search.trim()) return data
    const q = search.toLowerCase()
    return data.filter((item) =>
      Object.values(item as Record<string, unknown>).some(
        (v) => typeof v === "string" && v.toLowerCase().includes(q)
      )
    )
  }, [data, search])

  return (
    <div className="space-y-3">
      {/* Search — high-end-visual-design: spatial rhythm */}
      <div className="relative w-64">
        <Search
          className="pointer-events-none absolute left-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-muted-foreground"
          strokeWidth={2}
        />
        <Input
          placeholder="Buscar..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="pl-8 h-8 text-[13px]"
        />
      </div>

      {/* Table — double-bezel wrapper (high-end-visual-design §4.A outer shell) */}
      <div className="rounded-xl ring-1 ring-border bg-card shadow-card overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="hover:bg-transparent border-border/60 bg-muted/30">
              {columns.map((col) => (
                <TableHead key={col.key} className={col.className}>
                  {col.header}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>

          <TableBody>
            {/* ── Loading: shimmer skeletons that match row shape ── */}
            {loading ? (
              Array.from({ length: 6 }).map((_, i) => (
                <TableRow key={i} className="hover:bg-transparent">
                  {columns.map((col, ci) => (
                    <TableCell key={col.key}>
                      {/* Vary width so skeletons feel natural (redesign-existing-projects) */}
                      <Skeleton
                        className="h-4 rounded"
                        style={{ width: `${50 + ((i * 13 + ci * 17) % 40)}%` }}
                      />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : filtered.length === 0 ? (
              /* ── Empty state (impeccable: empty states that teach the interface) ── */
              <TableRow className="hover:bg-transparent">
                <TableCell colSpan={columns.length} className="py-16">
                  <div className="flex flex-col items-center gap-3 text-center">
                    {/* Double-bezel icon container */}
                    <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-muted/60 ring-1 ring-border">
                      <Inbox
                        className="h-6 w-6 text-muted-foreground/40"
                        strokeWidth={1.5}
                      />
                    </div>
                    <div className="space-y-1">
                      <p className="text-sm font-semibold text-foreground">
                        {search ? "Sin resultados" : "Sin registros"}
                      </p>
                      <p className="text-[12px] text-muted-foreground max-w-xs">
                        {search
                          ? `No se encontró "${search}". Prueba con otros términos.`
                          : emptyMessage}
                      </p>
                    </div>
                    {!search && emptyAction && (
                      <div className="mt-1">{emptyAction}</div>
                    )}
                  </div>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((item, idx) => (
                <TableRow key={idx} className="animate-fade-up" style={{ animationDelay: `${idx * 18}ms` }}>
                  {columns.map((col) => (
                    <TableCell key={col.key} className={col.className}>
                      {col.render
                        ? col.render(item)
                        : String((item as Record<string, unknown>)[col.key] ?? "")}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* Footer count */}
      {!loading && filtered.length > 0 && (
        <p className="text-[11px] text-muted-foreground px-0.5">
          {search
            ? `${filtered.length} de ${data.length} ${data.length === 1 ? "registro" : "registros"}`
            : `${data.length} ${data.length === 1 ? "registro" : "registros"} en total`}
        </p>
      )}
    </div>
  )
}
