import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { Toaster } from "sonner"
import Layout from "@/components/layout/Layout"
import Dashboard from "@/pages/Dashboard"
import ProductsPage from "@/pages/products/ProductsPage"
import UsersPage from "@/pages/users/UsersPage"
import CategoriesPage from "@/pages/categories/CategoriesPage"
import CartsPage from "@/pages/carts/CartsPage"
import OrdersPage from "@/pages/orders/OrdersPage"
import PaymentsPage from "@/pages/payments/PaymentsPage"
import InventoryPage from "@/pages/inventory/InventoryPage"
import InventoryMovementsPage from "@/pages/inventoryMovements/InventoryMovementsPage"
import DocumentTypesPage from "@/pages/settings/DocumentTypesPage"
import ProductCategoriesPage from "@/pages/productCategories/ProductCategoriesPage"
import NotFoundPage from "@/pages/NotFoundPage"

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, staleTime: 30_000 } },
})

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="products" element={<ProductsPage />} />
            <Route path="users" element={<UsersPage />} />
            <Route path="categories" element={<CategoriesPage />} />
            <Route path="carts" element={<CartsPage />} />
            <Route path="orders" element={<OrdersPage />} />
            <Route path="payments" element={<PaymentsPage />} />
            <Route path="inventory" element={<InventoryPage />} />
            <Route path="movements" element={<InventoryMovementsPage />} />
            <Route path="product-categories" element={<ProductCategoriesPage />} />
            <Route path="settings" element={<DocumentTypesPage />} />
          </Route>
          {/* 404 — outside the Layout shell so it gets full screen */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="bottom-right" richColors />
    </QueryClientProvider>
  )
}
