export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}

export interface ProductResponse { id: number; name: string; description: string; price: number; available: boolean }
export interface CreateProductRequest { name: string; description: string; price: number; available: boolean }
export interface UpdateProductRequest { name?: string; description?: string; price?: number; available?: boolean; categoryId?: number }

export interface UserResponse { id: number; fullName: string; email: string; documentTypeId: number; documentTypeName: string; documentNumber: string }
export interface CreateUserRequest { fullName: string; email: string; phone: string; documentNumber: string; documentTypeId: number; birthDate: string; country: string; address: string }
export interface UpdateUserRequest { fullName?: string; phone?: string; email?: string; documentTypeId?: number; documentNumber?: string; birthDate?: string; country?: string; address?: string }

export interface CategoryResponse { id: number; name: string; parentId: number | null }
export interface CreateCategoryRequest { name: string; parentId?: number | null }
export interface UpdateCategoryRequest { name?: string; parentId?: number | null }

export type CartStatus = "ACTIVE" | "CHECKED_OUT" | "ABANDONED"
export interface CartResponse { id: number; userId: number; status: CartStatus }
export interface CreateCartRequest { userId: number; status: CartStatus }
export interface UpdateCartRequest { userId?: number; status?: CartStatus }

export interface CartItemResponse { id: number; cartId: number; productId: number; quantity: number }
export interface CreateCartItemRequest { cartId: number; productId: number; quantity: number }
export interface UpdateCartItemRequest { cartId?: number; productId?: number; quantity?: number }

export type OrderStatus = "CREATED" | "PAID" | "CANCELLED"
export interface OrderResponse { id: number; userId: number; status: OrderStatus; totalAmount: number; currency: string }
export interface CreateOrderRequest { userId: number; status: OrderStatus; totalAmount: number; currency: string }
export interface UpdateOrderRequest { userId?: number; status?: OrderStatus; totalAmount?: number; currency?: string }

export interface OrderItemResponse { id: number; orderId: number; productId: number; quantity: number; unitPriceSnapshot: number; lineTotal: number }
export interface CreateOrderItemRequest { orderId: number; productId: number; quantity: number; unitPriceSnapshot: number }
export interface UpdateOrderItemRequest { orderId?: number; productId?: number; quantity?: number; unitPriceSnapshot?: number }

export type PaymentStatus = "SUCCEEDED" | "FAILED"
export interface PaymentResponse { id: number; orderId: number; status: PaymentStatus; providerRef: string; idempotencyKey: string }
export interface CreatePaymentRequest { orderId: number; status: PaymentStatus; providerRef: string; idempotencyKey: string }
export interface UpdatePaymentRequest { orderId?: number; status?: PaymentStatus; providerRef?: string; idempotencyKey?: string }

export interface InventoryResponse { id: number; productId: number; stock: number }
export interface CreateInventoryRequest { productId: number; stock: number }
export interface UpdateInventoryRequest { productId?: number; stock?: number }

export type MovementType = "DEBIT" | "CREDIT" | "RESERVE" | "RELEASE"
export interface InventoryMovementResponse { id: number; productId: number; orderId: number; type: MovementType; qty: number }
export interface CreateInventoryMovementRequest { productId: number; orderId: number; type: MovementType; qty: number }
export interface UpdateInventoryMovementRequest { productId?: number; orderId?: number; type?: MovementType; qty?: number }

export interface DocumentTypeResponse { id: number; code: string; name: string }
export interface CreateDocumentTypeRequest { code: string; name: string }
export interface UpdateDocumentTypeRequest { code?: string; name?: string }

export interface ProductCategoryResponse { id: number; productId: number; productName: string; categoryId: number; categoryName: string }
export interface CreateProductCategoryRequest { productId: number; categoryId: number }
export interface UpdateProductCategoryRequest { productId?: number; categoryId?: number }

export interface DeleteResponse { message: string }
