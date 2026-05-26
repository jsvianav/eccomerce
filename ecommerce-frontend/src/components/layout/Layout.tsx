import { Outlet, useLocation } from "react-router-dom"
import { Sidebar } from "./Sidebar"
import { Header } from "./Header"

const pageTitles: Record<string, string> = {
  "/dashboard": "Dashboard",
  "/products": "Productos",
  "/categories": "Categorías",
  "/carts": "Carritos",
  "/orders": "Pedidos",
  "/payments": "Pagos",
  "/inventory": "Inventario",
  "/movements": "Movimientos de Inventario",
  "/users": "Usuarios",
  "/settings": "Tipos de Documento",
}

export default function Layout() {
  const location = useLocation()
  const title = pageTitles[location.pathname] ?? "EcommAdmin"

  return (
    <div className="flex h-screen overflow-hidden bg-background">
      <Sidebar />
      <div className="flex flex-1 flex-col overflow-hidden min-w-0">
        <Header title={title} />
        <main className="flex-1 overflow-y-auto">
          <div className="p-6 max-w-[1400px] animate-fade-in">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
