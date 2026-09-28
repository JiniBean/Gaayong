import type { CategoryItem } from './category'

export interface PayItem {
  id: string
  name: string
  amt: number | string
  dd: string
  ctg: string
  ctgId: string
  isPaid: boolean | number | string
}

export interface PayListResponse {
  year: number
  month: number
  total: number
  unpaid: number
  categoryTotal: number
  list: PayItem[]
  categoryList: CategoryItem[]
}

export interface PayPayload {
  name: string
  amt: string
  dd: string
  ctgId: string
  y?: string
  month?: string
  isPaid?: string
}
