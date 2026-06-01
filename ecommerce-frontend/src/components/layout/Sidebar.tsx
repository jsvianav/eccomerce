import { NavLink } from "react-router-dom"
import { cn } from "@/lib/utils"
import {
  LayoutDashboard,
  Package,
  Tag,
  ShoppingCart,
  ClipboardList,
  CreditCard,
  Warehouse,
  ArrowLeftRight,
  Users,
  Settings,
  Link2,
} from "lucide-react"

/**
 * EcommAdmin brand mark — E-shelves concept
 * Three horizontal bars of decreasing width read as:
 *   1. "E" letterform (EcommAdmin initial)
 *   2. Store catalog hierarchy (full / shorter / full)
 *   3. Dashboard row system
 */
function EShelvesLogo({ className }: { className?: string }) {
  return (
    <svg
      viewBox="0 0 16 16"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
      aria-hidden
    >
      {/* Vertical spine */}
      <rect x="2.5" y="2.5" width="1.5" height="11" rx="0.75" fill="currentColor"/>
      {/* Top bar — full width */}
      <rect x="2.5" y="2.5" width="11" height="1.5" rx="0.75" fill="currentColor"/>
      {/* Mid bar — shorter (catalog hierarchy) */}
      <rect x="2.5" y="7.25" width="7.5" height="1.5" rx="0.75" fill="currentColor"/>
      {/* Bottom bar — full width */}
      <rect x="2.5" y="12" width="11" height="1.5" rx="0.75" fill="currentColor"/>
    </svg>
  )
}

const navItems = [
  { to: "/dashboard",          label: "Dashboard",        icon: LayoutDashboard },
  { to: "/products",           label: "Productos",         icon: Package         },
  { to: "/categories",         label: "Categorías",        icon: Tag             },
  { to: "/product-categories", label: "Prod. — Categ.",   icon: Link2           },
  { to: "/carts",              label: "Carritos",          icon: ShoppingCart    },
  { to: "/orders",             label: "Pedidos",           icon: ClipboardList   },
  { to: "/payments",           label: "Pagos",             icon: CreditCard      },
  { to: "/inventory",          label: "Inventario",        icon: Warehouse       },
  { to: "/movements",          label: "Movimientos",       icon: ArrowLeftRight  },
  { to: "/users",              label: "Usuarios",          icon: Users           },
]

const settingsItems = [
  { to: "/settings", label: "Tipos Documento", icon: Settings },
]

export function Sidebar() {
  return (
    <aside className="flex h-screen w-[212px] shrink-0 flex-col bg-[hsl(var(--sidebar))] border-r border-border">

      {/* ── Logo (high-end-visual-design: nested pill logo island) ── */}
      <div className="flex h-14 items-center gap-2.5 px-4 border-b border-border/60">
        {/* Brand mark — E-shelves logo */}
        <div className="relative flex h-7 w-7 items-center justify-center">
          <div className="absolute inset-0 rounded-lg bg-primary/10 ring-1 ring-primary/20" />
          <EShelvesLogo className="relative h-3.5 w-3.5 text-primary" />
        </div>
        <div className="min-w-0">
          <p className="text-[13px] font-bold tracking-tight text-foreground leading-none">
            EcommAdmin
          </p>
          <p className="text-[10px] text-muted-foreground leading-none mt-0.5">
            Panel de gestión
          </p>
        </div>
      </div>

      {/* ── Navigation ── */}
      <nav className="flex-1 overflow-y-auto px-2 py-3 space-y-px">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              cn(
                /* Emil: specific transitions, not `all` */
                "group flex items-center gap-2.5 rounded-lg px-2.5 py-[7px] text-[13px] font-medium",
                "transition-colors duration-100",
                isActive
                  /* impeccable: bg highlight only, NO side-stripe (absolute ban) */
                  ? "bg-primary/10 text-primary"
                  : "text-muted-foreground hover:bg-accent hover:text-foreground"
              )
            }
          >
            {({ isActive }) => (
              <>
                <Icon
                  className={cn(
                    "h-[15px] w-[15px] shrink-0 transition-colors duration-100",
                    isActive
                      ? "text-primary"
                      : "text-muted-foreground/70 group-hover:text-foreground"
                  )}
                  strokeWidth={isActive ? 2.25 : 1.75}
                />
                {label}
              </>
            )}
          </NavLink>
        ))}

        {/* Settings section */}
        <div className="pt-3">
          <p className="px-2.5 pb-1.5 text-[10px] font-semibold uppercase tracking-widest text-muted-foreground/50">
            Configuración
          </p>
          {settingsItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                cn(
                  "group flex items-center gap-2.5 rounded-lg px-2.5 py-[7px] text-[13px] font-medium",
                  "transition-colors duration-100",
                  isActive
                    ? "bg-primary/10 text-primary"
                    : "text-muted-foreground hover:bg-accent hover:text-foreground"
                )
              }
            >
              {({ isActive }) => (
                <>
                  <Icon
                    className={cn(
                      "h-[15px] w-[15px] shrink-0 transition-colors duration-100",
                      isActive
                        ? "text-primary"
                        : "text-muted-foreground/70 group-hover:text-foreground"
                    )}
                    strokeWidth={isActive ? 2.25 : 1.75}
                  />
                  {label}
                </>
              )}
            </NavLink>
          ))}
        </div>
      </nav>

      {/* ── User footer (minimalist-ui §5: no rounded-full on large containers) ── */}
      <div className="border-t border-border/60 px-3 py-3">
        <div className="flex items-center gap-2.5 rounded-lg px-2 py-2">
          {/* Avatar — small, not pill-shaped */}
          <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg bg-primary/15 ring-1 ring-primary/20">
            <span className="text-[10px] font-bold text-primary leading-none">JV</span>
          </div>
          <div className="min-w-0">
            <p className="text-[12px] font-semibold text-foreground truncate leading-none">Admin</p>
            <p className="text-[10px] text-muted-foreground truncate leading-none mt-0.5">
              ecommerce@admin.co
            </p>
          </div>
        </div>
      </div>
    </aside>
  )
}
